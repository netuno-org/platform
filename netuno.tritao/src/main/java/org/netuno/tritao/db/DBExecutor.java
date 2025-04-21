/*
 * Licensed to the Netuno.org under one or more
 * contributor license agreements. See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The Netuno.org licenses this file to You under the Apache License, Version
 * 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.netuno.tritao.db;

import java.sql.Connection;
import java.sql.SQLException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.netuno.proteu.Proteu;
import org.netuno.psamata.DB;
import org.netuno.psamata.Values;
import org.netuno.tritao.config.Config;
import org.netuno.tritao.hili.Hili;

import java.text.DateFormat;
import java.util.List;
import java.util.ArrayList;

/**
 * Database Manager
 * @author Eduardo Fonseca Velasques - @eduveks
 */
public class DBExecutor {

    private static Logger logger = LogManager.getLogger(DBExecutor.class);

    private List<DBConnection> dbConnections = new ArrayList<DBConnection>();
    private Proteu proteu = null;
    private Hili hili = null;
    private String key = null;
    private String dbName = null;

    public DBExecutor(Proteu proteu, Hili hili, String key) {
        this.proteu = proteu;
        this.hili = hili;
        this.key = key;
    }

    public Connection getConnection() throws SQLException {
        String dbName = Config.getDabaBaseNamingBase(proteu, key)
                + Config.getDabaBase(proteu, key);
        DBConnection dbConnectionClosed = null;
        for (DBConnection dbConnection : dbConnections) {
            if (dbConnection.getDb().equals(dbName)) {
                if (dbConnection.getConnection().isClosed()) {
                    dbConnectionClosed = dbConnection;
                    break;
                } else {
                    return dbConnection.getConnection();
                }
            }
        }
        try {
            if (org.netuno.proteu.Config.getDataSources().hasKey(dbName)) {
                Object ds = org.netuno.proteu.Config.getDataSources().get(dbName);
                java.lang.reflect.Method method = ds.getClass().getMethod(
                        "getConnection", new Class[0]);
                Connection con = (Connection) method.invoke(ds, new Object[0]);
                if (dbConnectionClosed == null) {
                    dbConnections.add(new DBConnection(dbName, con));
                } else {
                    dbConnectionClosed.setConnection(con);
                }
                return con;
            }
        } catch (Exception e) {
            logger.trace(e);
            throw new DBError(e).setLogError("Connection to " + Config.getDabaBase(proteu, key));
        }
        try {
            logger.info(Config.getDabaBase(proteu, key) + " new connection");
            javax.naming.InitialContext ctx = new javax.naming.InitialContext();
            Object ds = ctx.lookup(dbName);
            java.lang.reflect.Method method = ds.getClass().getMethod(
                    "getConnection", new Class[0]);
            Connection con = (Connection) method.invoke(ds, new Object[0]);
            if (dbConnectionClosed == null) {
                dbConnections.add(new DBConnection(dbName, con));
            } else {
                dbConnectionClosed.setConnection(con);
            }
            return con;
        } catch (Exception e) {
            throw new DBError(e).setLogError("Connection to " + Config.getDabaBase(proteu, key));
        }
    }

    public List<Values> query(String sql, Object... params) {
        try {
            Connection con = getConnection();
            List<Values> result = null;
            if (params != null) {
                result = DB.executeQuery(con, sql, params);
            } else {
                result = DB.executeQuery(con, sql);
            }
            logger.info(Config.getDabaBase(proteu, key) + " query executed: " + sql);
            return result;
        } catch (Exception e) {
            logger.trace("Query executing on " + Config.getDabaBase(proteu, key), e);
            throw new DBError(e).setLogError("Query executing on " + Config.getDabaBase(proteu, key) + ": " + sql);
        }
    }

    public List<Values> query(String sql) {
        return query(sql, null);
    }

    public int execute(String sql, Object... params) {
        try {
            Connection con = getConnection();
            int result = DB.execute(con, sql, params);
            logger.info(Config.getDabaBase(proteu, key) + " query executed: " + sql);
            return result;
        } catch (Exception e) {
            logger.trace("Query executing on " + Config.getDabaBase(proteu, key), e);
            throw new DBError(e).setLogError("Query executing on " + Config.getDabaBase(proteu, key) + ": " + sql);
        }
    }

    public int execute(String sql) {
        return execute(sql, null);
    }

    public int insert(String sql, Object... params) {
        try {
            Connection con = getConnection();
            int result = DB.insert(con, sql, params);
            logger.info(Config.getDabaBase(proteu, key) + " query inserted: " + sql);
            return result;
        } catch (Exception e) {
            throw new DBError(e).setLogError("Query inserting on " + Config.getDabaBase(proteu, key) + ": " + sql);
        }
    }

    public int insert(String sql) {
        return insert(sql, null);
    }

    public void closeConnections() {
        List<DBConnection> dbConnsClosed = new ArrayList<DBConnection>();
        for (DBConnection dbConn : dbConnections) {
            String db = "";
            try {
                db = dbConn.getDb();
                if (dbConn.getConnection() != null) {
                    dbConn.getConnection().close();
                    dbConn.setConnection(null);
                    dbConnsClosed.add(dbConn);
                    logger.debug(dbConn.getDb() + " connection closed");
                }
            } catch (Throwable t) {
                logger.warn("Connection closing on " + db, t);
            }
        }
        for (DBConnection dbConn : dbConnsClosed) {
            dbConnections.remove(dbConn);
        }
    }

    public void scriptSave(Proteu proteu, Hili hili, String tableName, DataItem dataItem) {
        try {
            hili.sandbox().bind("dataItem", dataItem);
            boolean setupRunning = proteu.getConfig().getBoolean("_setup:running", false);
            hili.sandbox().runScriptIfExists(Config.getPathAppActions(proteu), "" + tableName + "/" + (setupRunning ? "setup_" : "") + "save");
        } finally {
            hili.sandbox().unbind("dataItem");
        }
    }

    public void scriptSaved(Proteu proteu, Hili hili, String tableName, DataItem dataItem) {
        try {
            hili.sandbox().bind("dataItem", dataItem);
            boolean setupRunning = proteu.getConfig().getBoolean("_setup:running", false);
            hili.sandbox().runScriptIfExists(Config.getPathAppActions(proteu), "" + tableName + "/" + (setupRunning ? "setup_" : "") + "saved");
        } finally {
            hili.sandbox().unbind("dataItem");
        }
    }

    public void scriptRemove(Proteu proteu, Hili hili, String tableName, DataItem dataItem) {
        try {
            hili.sandbox().bind("dataItem", dataItem);
            boolean setupRunning = proteu.getConfig().getBoolean("_setup:running", false);
            hili.sandbox().runScriptIfExists(Config.getPathAppActions(proteu), "" + tableName + "/" + (setupRunning ? "setup_" : "") + "remove");
        } finally {
            hili.sandbox().unbind("dataItem");
        }
    }

    public void scriptRemoved(Proteu proteu, Hili hili, String tableName, DataItem dataItem) {
        try {
            hili.sandbox().bind("dataItem", dataItem);
            boolean setupRunning = proteu.getConfig().getBoolean("_setup:running", false);
            hili.sandbox().runScriptIfExists(Config.getPathAppActions(proteu), "" + tableName + "/" + (setupRunning ? "setup_" : "") + "removed");
        } finally {
            hili.sandbox().unbind("dataItem");
        }
    }

    public boolean isDateFormat(String v) {
        try {
            DB.getDateFormat().parse(v);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public boolean isDateTimeFormat(String v) {
        try {
            DB.getDateTimeFormat().parse(v);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public DateFormat getDateFormat() {
        return DB.getDateFormat();
    }

    public DateFormat getDateTimeFormat() {
        return DB.getDateTimeFormat();
    }

    private class DBConnection {
        private String db = "";
        private java.sql.Connection connection = null;
        public DBConnection(String db, Connection connection) {
            this.db = db;
            this.connection = connection;
        }

        public Connection getConnection() {
            return connection;
        }

        public void setConnection(Connection connection) {
            this.connection = connection;
        }

        public String getDb() {
            return db;
        }
    }
}
