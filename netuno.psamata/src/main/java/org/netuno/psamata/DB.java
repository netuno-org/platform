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

package org.netuno.psamata;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.sql.*;
import java.sql.Date;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;

/**
 * DB.
 * @author Eduardo Fonseca Velasques - @eduveks
 */
public class DB {
    private static Logger logger = LogManager.getLogger(DB.class);

    /**
     * Key.
     */
    private String key = "";

    /**
     * Connection.
     */
    private Connection con = null;

    private static final ThreadLocal<DateFormat> dateFormat = new ThreadLocal<DateFormat>(){
        @Override
        protected DateFormat initialValue() {
            return new SimpleDateFormat("yyyy-MM-dd");
        }
    };

    private static final ThreadLocal<DateFormat> datetimeFormat = new ThreadLocal<DateFormat>(){
        @Override
        protected DateFormat initialValue() {
            return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        }
    };

    private static final ThreadLocal<DateFormat> timeFormat = new ThreadLocal<DateFormat>(){
        @Override
        protected DateFormat initialValue() {
            return new SimpleDateFormat("HH:mm:ss");
        }
    };

    /**
     * DB.
     * @param connection DB Connection
     * @throws SQLException Create Statement Exception
     */
    public DB(final String key, final Connection connection) {
        this.key = key;
        this.con = connection;
    }

    public String getKey() {
        return key;
    }

    /**
     * Sql Injection.
     * @param text Text
     * @return Text ok to use in sql querys
     */
    public static String sqlInjectionRawPath(final String text) throws PsamataException {
    	if (text.matches("^[A-Za-z_]+[A-Za-z0-9_\\.]*$")) {
    		return text;
    	}
    	throw new PsamataException("Invalid SQL raw path with "+ text);
    }
    /**
     * Sql Injection.
     * @param text Text
     * @return Text ok to use in sql querys
     */
    public static String toRawPath(final String text) throws PsamataException {
        return DB.sqlInjectionRawPath(text);
    }
    /**
     * Sql Injection.
     * @param text Text
     * @return Text ok to use in sql querys
     */
    public static String sqlInjectionRawName(final String text) throws PsamataException {
    	if (text.matches("^[A-Za-z_]+[A-Za-z0-9_]*$")) {
    		return text;
    	}
    	throw new PsamataException("Invalid SQL raw name with "+ text);
    }
    /**
     * Sql Injection.
     * @param text Text
     * @return Text ok to use in sql querys
     */
    public static String toRawName(final String text) throws PsamataException {
        return DB.sqlInjectionRawName(text);
    }
    /**
     * Sql Injection.
     * @param text Text
     * @return Text ok to use in sql querys
     */
    public static String sqlInjection(final String text) {
        return text.replace("'", "''");
    }
    /**
     * Sql Injection.
     * @param text Text
     * @return Text ok to use in sql querys
     */
    public static String toString(final String text) {
        return DB.sqlInjection(text);
    }

    /**
     * Sql injection integer.
     * @param text Text
     * @return Text ok to use in sql querys
     */
    public static String sqlInjectionBoolean(final String text) {
        try {
            if (text.equalsIgnoreCase("true")) {
                return "true";
            } else if (text.equalsIgnoreCase("false")) {
                return "false";
            }
            return Integer.parseInt(text) > 0 ? "true" : "false";
        } catch (Exception e) {
            return "false";
        }
    }
    /**
     * Sql injection integer.
     * @param text Text
     * @return Text ok to use in sql querys
     */
    public String toBoolean(final String text) {
        return DB.sqlInjectionBoolean(text);
    }

    /**
     * Sql injection integer.
     * @param text Text
     * @return Text ok to use in sql querys
     */
    public static String sqlInjectionInt(final String text) {
        try {
            if (text.indexOf(".") > 0) {
                return Integer.toString(Math.round(Float.parseFloat(text)));
            }
            return Integer.toString(Integer.parseInt(text));
        } catch (Exception e) {
            return "0";
        }
    }
    /**
     * Sql injection integer.
     * @param text Text
     * @return Text ok to use in sql querys
     */
    public static String toInt(final String text) {
        return DB.sqlInjectionInt(text);
    }
    
    /**
     * Sql injection sequence integer.
     * @param text Text
     * @return Text ok to use in sql querys
     */
    public static String sqlInjectionIntSequence(final String text) {
        try {
        	String[] values = text.split("\\,");
        	String result = "";
        	for (String value : values) {
        		if (!result.isEmpty()) {
        			result = result.concat(", ");
        		}
        		result = result.concat(Integer.toString(Integer.parseInt(value.trim())));
        	}
        	return result;
        } catch (Exception e) {
            return "";
        }
    }
    /**
     * Sql injection sequence integer.
     * @param text Text
     * @return Text ok to use in sql querys
     */
    public static String toIntSequence(final String text) {
        return DB.sqlInjectionIntSequence(text);
    }
    
    /**
     * Sql injection list integer.
     * @param text Text
     * @return Text ok to use in sql querys
     */
    public static List<String> sqlInjectionIntList(final String text) {
        try {
        	String[] values = text.split("\\,");
        	List<String> result = new ArrayList<String>();
        	for (int i = 0; i < values.length; i++) {
        		result.add(Integer.toString(Integer.parseInt(values[i].trim())));
        	}
        	return result;
        } catch (Exception e) {
            return null;
        }
    }
    /**
     * Sql injection list integer.
     * @param text Text
     * @return Text ok to use in sql querys
     */
    public List<String> toIntList(final String text) {
        return DB.sqlInjectionIntList(text);
    }
    
    

    /**
     * Sql injection integer.
     * @param text Text
     * @return Text ok to use in sql querys
     */
    public static String sqlInjectionLong(final String text) {
        try {
            if (text.indexOf(".") > 0) {
                return Long.toString(Math.round(Double.parseDouble(text)));
            }
            return Long.toString(Long.parseLong(text));
        } catch (Exception e) {
            return "0";
        }
    }
    /**
     * Sql injection long.
     * @param text Text
     * @return Text ok to use in sql querys
     */
    public String toLong(final String text) {
        return DB.sqlInjectionLong(text);
    }
    
    /**
     * Sql injection sequence long.
     * @param text Text
     * @return Text ok to use in sql querys
     */
    public static String sqlInjectionLongSequence(final String text) {
        try {
        	String[] values = text.split("\\,");
        	String result = "";
        	for (String value : values) {
        		if (!result.isEmpty()) {
        			result = result.concat(", ");
        		}
        		result = result.concat(Long.toString(Long.parseLong(value.trim())));
        	}
        	return result;
        } catch (Exception e) {
            return "";
        }
    }
    /**
     * Sql injection sequence long.
     * @param text Text
     * @return Text ok to use in sql querys
     */
    public String toLongSequence(final String text) {
        return DB.sqlInjectionLongSequence(text);
    }
    
    /**
     * Sql injection list long.
     * @param text Text
     * @return Text ok to use in sql querys
     */
    public static List<String> sqlInjectionLongList(final String text) {
        try {
        	String[] values = text.split("\\,");
        	List<String> result = new ArrayList<String>();
        	for (int i = 0; i < values.length; i++) {
        		result.add(Long.toString(Long.parseLong(values[i].trim())));
        	}
        	return result;
        } catch (Exception e) {
            return null;
        }
    }
    /**
     * Sql injection list long.
     * @param text Text
     * @return Text ok to use in sql querys
     */
    public List<String> toLongList(final String text) {
        return DB.sqlInjectionLongList(text);
    }

    /**
     * Sql injection float.
     * @param text Text
     * @return Text ok to use in sql querys
     */
    public static String sqlInjectionFloat(final String text) {
        try {
            return Float.toString(Float.parseFloat(text)).replace(",", ".");
        } catch (Exception e) {
            return "0";
        }
    }
    /**
     * Sql injection double.
     * @param text Text
     * @return Text ok to use in sql querys
     */
    public static String toFloat(final String text) {
        return DB.sqlInjectionFloat(text);
    }

    /**
     * Sql injection double.
     * @param text Text
     * @return Text ok to use in sql querys
     */
    public static String sqlInjectionDouble(final String text) {
        try {
            return Double.toString(Double.parseDouble(text)).replace(",", ".");
        } catch (Exception e) {
            return "0";
        }
    }
    /**
     * Sql injection float.
     * @param text Text
     * @return Text ok to use in sql querys
     */
    public String toDouble(final String text) {
        return DB.sqlInjectionDouble(text);
    }
    
    /**
     * Create new batch.
     * @return new batch created.
     * @throws SQLException SQL Exception
     */
    public DBBatch batch() throws SQLException {
        return new DBBatch(this);
    }
    
    /**
     * Create new batch.
     * @param sql SQL command to be used as batch.
     * @return new batch created.
     * @throws SQLException SQL Exception
     */
    public DBBatch batch(final String sql) throws SQLException {
        return new DBBatch(this, sql);
    }

    /**
     * Execute Query.
     * @param query Query
     * @return Result
     * @throws SQLException SQL Exception
     */
    public List<Values> query(final String query) throws SQLException {
        return executeQuery(getKey(), con, query);
    }
    /**
     * Execute Query.
     * @param query Query
     * @return Result
     * @throws SQLException SQL Exception
     */
    public List<Values> executeQuery(final String query
    ) throws SQLException {
        return executeQuery(getKey(), con, query);
    }
    /**
     * Execute Query.
     * @param key Key
     * @param con Connection
     * @param query Query
     * @return Result
     * @throws SQLException SQL Exception
     */
    public static List<Values> query(final String key, final Connection con, final String query) throws SQLException {
        return executeQuery(key, con, query, null);
    }
    /**
     * Execute Query.
     * @param key Key
     * @param con Connection
     * @param query Query
     * @return Result
     * @throws SQLException SQL Exception
     */
    public static List<Values> executeQuery(final String key, final Connection con, final String query) throws SQLException {
        return executeQuery(key, con, query, null);
    }
    /**
     * Execute Query.
     * @param query Query
     * @param params Parameters
     * @return Result
     * @throws SQLException SQL Exception
     */
    public List<Values> query(final String query, final Object... params) throws SQLException {
        return DB.executeQuery(getKey(), con, query, params);
    }
    /**
     * Execute Query.
     * @param query Query
     * @param params Parameters
     * @return Result
     * @throws SQLException SQL Exception
     */
    public List<Values> executeQuery(final String query, final Object... params) throws SQLException {
        return DB.executeQuery(getKey(), con, query, params);
    }
    /**
     * Execute Query.
     * @param key Key
     * @param query Query
     * @param params Parameters
     * @return Result
     * @throws SQLException SQL Exception
     */
    public static List<Values> query(final String key,final Connection con, final String query, final Object... params) throws SQLException {
        return DB.executeQuery(key, con, query, params);
    }
    /**
     * Execute Query.
     * @param key Key
     * @param con Connection
     * @param query Query
     * @param params Parameters
     * @return Result
     * @throws SQLException SQL Exception
     */
    public static List<Values> executeQuery(final String key, final Connection con, final String query, final Object... params) throws SQLException {
        logger.trace(key + " >> Executing SQL Query: "+ query);
        List<Values> datasource = new ArrayList<Values>();
        PreparedStatement stat = null;
        ResultSet rs = null;
        try {
            stat = con.prepareStatement(query);
            if (params != null) {
                prepareStatementParams(stat, params);
            }
            rs = stat.executeQuery();
            while (rs.next()) {
                datasource.add(getValues(rs));
            }
        } catch (SQLException e) {
            throw e;
        } catch (Exception e) {
            throw new Error(e);
        } finally {
            if (rs != null) {
                rs.close();
            }
            if (stat != null) {
                stat.close();
            }
        }
        return datasource;
    }

    /*public static List<Values> executeQuery(final Connection con, final String query, final Values params) throws SQLException {
        List<Values> datasource = new ArrayList<Values>();
        PreparedStatement stat = null;
        ResultSet rs = null;
        try {
            stat = statement(con, query, params);
            rs = stat.executeQuery();
            while (rs.next()) {
                datasource.add(getValues(rs));
            }
        } catch (SQLException e) {
            throw e;
        } catch (Exception e) {
            throw new Error(e);
        } finally {
            if (rs != null) {
                rs.close();
            }
            if (stat != null) {
                stat.close();
            }
        }
        return datasource;
    }*/

    public Values toValues(ResultSet rs) throws SQLException, IOException {
        return DB.getValues(rs);
    }
    /**
     * Get Values from current row of a ResultSet
     * @param rs ResultSet
     * @return Values with data row
     * @throws SQLException
     * @throws IOException
     */
    public static Values getValues(ResultSet rs) throws SQLException, IOException {
    	Values row = new Values();
        for (int x = 1; x <= rs.getMetaData().getColumnCount(); x++) {
            Object o = null;
            if (rs.getMetaData().getColumnType(x) == Types.CLOB) {
                Clob clob = rs.getClob(x);
                if (clob != null) {
                    o = org.netuno.psamata.io.InputStream.readAll(clob.getCharacterStream());
                }
            } else if (rs.getMetaData().getColumnType(x) == Types.BLOB) {
                Blob blob = rs.getBlob(x);
                if (blob != null) {
                    o = org.netuno.psamata.io.InputStream.readAll(blob.getBinaryStream());
                }
            } else if (rs.getMetaData().getColumnType(x) == Types.VARBINARY) {
                byte[] bytes = (byte[])rs.getObject(x);
                o = new String(bytes);
            } else {
                o = rs.getObject(x);
            }
            row.set(rs.getMetaData().getColumnLabel(x), o);
        }
        return row;
    }
    /**
     * Execute Update Query.
     * @param query Query
     * @return Result
     * @throws SQLException SQL Exception
     */
    public final int execute(final String query) throws SQLException {
        return DB.execute(getKey(), con, query);
    }
    /**
     * Execute Update Query.
     * @param sql Query
     * @param params Parameters
     * @return Result
     * @throws SQLException SQL Exception
     */
    public int execute(final String sql, final Object... params) throws SQLException {
        return DB.execute(getKey(), con, sql, params);
    }
    /**
     * Execute Update Query.
     * @param key Key
     * @param con Connection
     * @param sql Query
     * @return Result
     * @throws SQLException SQL Exception
     */
    public static int execute(final String key, final Connection con, final String sql) throws SQLException {
        return execute(key, con, sql, null);
    }
    /**
     * Execute Update Query.
     * @param key Key
     * @param con Connection
     * @param sql Query
     * @param params Parameters
     * @return Result
     * @throws SQLException SQL Exception
     */
    public static int execute(final String key, final Connection con, final String sql, final Object... params) throws SQLException {
        logger.trace(key +" >> Executing SQL Command: "+ sql);
    	PreparedStatement stat = con.prepareStatement(sql);
        if (params != null) {
            prepareStatementParams(stat, params);
        }
        int result = stat.executeUpdate();
        stat.close();
        return result;
    }

    /**
     * Execute Insert Query.
     * @param query Query
     * @return Result
     * @throws SQLException SQL Exception
     */
    public final int insert(final String query) throws SQLException {
        return DB.insert(getKey(), con, query);
    }
    /**
     * Execute Insert Query.
     * @param sql Query
     * @param params Parameters
     * @return Result
     * @throws SQLException SQL Exception
     */
    public int insert(final String sql, final Object... params) throws SQLException {
        return DB.insert(getKey(), con, sql, params);
    }
    /**
     * Execute Insert Query.
     * @param key Key
     * @param con Connection
     * @param sql Query
     * @return Result
     * @throws SQLException SQL Exception
     */
    public static int insert(final String key, final Connection con, final String sql) throws SQLException {
        return insert(key, con, sql, null);
    }
    /**
     * Execute Insert Query.
     * @param key Key
     * @param con Connection
     * @param sql Query
     * @param params Parameters
     * @return Result
     * @throws SQLException SQL Exception
     */
    public static int insert(final String key, final Connection con, final String sql, final Object... params) throws SQLException {
        logger.trace(key + " >> Executing SQL Insert: "+ sql);
        PreparedStatement stat = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
        if (params != null) {
            prepareStatementParams(stat, params);
        }
        stat.executeUpdate();
        ResultSet rs = stat.getGeneratedKeys();
        int id = 0;
        if (rs.next()) {
            id = rs.getInt(1);
        }
        stat.close();
        return id;
    }

    /*
    private static PreparedStatement statement(Connection con, String query, Values params) throws SQLException {
        PreparedStatement stat = con.prepareStatement(query);
        if (params != null) {
            for (Object _key : params.keySet()) {
                String key = _key.toString();
                Object param = params.get(_key);
                if (param instanceof String) {
                    stat.setString(key, (String) param);
                } else if (param instanceof Integer) {
                    stat.setInt(i, (Integer) param);
                } else if (param instanceof Boolean) {
                    stat.setBoolean(i, (Boolean) param);
                } else if (param instanceof Short) {
                    stat.setShort(i, (Short) param);
                } else if (param instanceof Long) {
                    stat.setLong(i, (Long) param);
                } else if (param instanceof Double) {
                    double doubleValue = ((Double) param).doubleValue();
                    int intValue = (int) doubleValue;
                    if (intValue == doubleValue) {
                        stat.setInt(i, intValue);
                    } else {
                        stat.setDouble(i, doubleValue);
                    }
                } else if (param instanceof Float) {
                    float floatValue = ((Float) param).floatValue();
                    int intValue = (int) floatValue;
                    if (intValue == floatValue) {
                        stat.setInt(i, intValue);
                    } else {
                        stat.setFloat(i, floatValue);
                    }
                } else if (param instanceof java.util.Date) {
                    stat.setDate(i, new java.sql.Date(((java.util.Date) param).getTime()), new GregorianCalendar());
                } else if (param instanceof java.util.Calendar) {
                    stat.setDate(i, new java.sql.Date(((java.util.Calendar) param).getTimeInMillis()), (java.util.Calendar) param);
                } else if (param instanceof Date) {
                    stat.setDate(i, (Date) param);
                } else if (param instanceof Time) {
                    stat.setTime(i, (Time) param);
                } else if (param instanceof Timestamp) {
                    stat.setTimestamp(i, (Timestamp) param, new GregorianCalendar());
                } else if (param instanceof Object) {
                    stat.setObject(i, (Object) param);
                }
            }
        }
        return stat;
    }
    */

    private static void prepareStatementParams(PreparedStatement stat, Object... params) throws SQLException {
    	for (int i = 1; i <= params.length; i++) {
    		Object param = params[i - 1];
    		if (param instanceof String) {
    			stat.setString(i, (String)param);
    		} else if (param instanceof Integer) {
    			stat.setInt(i, (Integer)param);
    		} else if (param instanceof Boolean) {
    			stat.setBoolean(i, (Boolean)param);
    		} else if (param instanceof Short) {
    			stat.setShort(i, (Short)param);
    		} else if (param instanceof Long) {
    			stat.setLong(i, (Long)param);
    		} else if (param instanceof Double) {
    			double doubleValue = ((Double)param).doubleValue();
    			int intValue = (int)doubleValue;
    			if (intValue == doubleValue) {
        			stat.setInt(i, intValue);
    			} else {
    				stat.setDouble(i, doubleValue);
    			}
    		} else if (param instanceof Float) {
    			float floatValue = ((Float)param).floatValue();
    			int intValue = (int)floatValue;
    			if (intValue == floatValue) {
        			stat.setInt(i, intValue);
    			} else {
    				stat.setFloat(i, floatValue);
    			}
    		} else if (param instanceof java.util.Date) {
    			stat.setDate(i, new java.sql.Date(((java.util.Date)param).getTime()), new GregorianCalendar());
    		} else if (param instanceof java.util.Calendar) {
    			stat.setDate(i, new java.sql.Date(((java.util.Calendar)param).getTimeInMillis()), (java.util.Calendar)param);
    		} else if (param instanceof Date) {
    			stat.setDate(i, (Date)param);
    		} else if (param instanceof LocalDate) {
				stat.setDate(i, Date.valueOf((LocalDate)param));
    		} else if (param instanceof Time) {
    			stat.setTime(i, (Time)param);
    		} else if (param instanceof LocalTime) {
				stat.setTime(i, Time.valueOf((LocalTime)param));
    		} else if (param instanceof Timestamp) {
				stat.setTimestamp(i, (Timestamp)param);
    		} else if (param instanceof LocalDateTime) {
				stat.setTimestamp(i, Timestamp.valueOf((LocalDateTime)param));
    		} else if (param instanceof Instant) {
				stat.setTimestamp(i, Timestamp.from((Instant)param));
    		} else if (param instanceof Object) {
				stat.setObject(i, (Object)param);
			}
    	}
    }

    public static DateFormat getDateFormat() {
        return dateFormat.get();
    }

    public static DateFormat getDateTimeFormat() {
        return datetimeFormat.get();
    }

    public static DateFormat getTimeFormat() {
        return timeFormat.get();
    }
    
    public static class DBBatch {
        private Statement stat = null;
        private PreparedStatement preparedStat = null;
        
        public DBBatch(DB db) throws SQLException {
            stat = db.con.createStatement();
        }
        
        public DBBatch(DB db, String sql) throws SQLException {
            preparedStat = db.con.prepareStatement(sql);
        }
        
        public DBBatch add(String sql) throws SQLException {
            stat.addBatch(sql);
            return this;
        }
        
        public DBBatch put(final Object... params) throws SQLException {
            prepareStatementParams(preparedStat, params);
            preparedStat.addBatch();
            return this;
        }
        
        public void clear() throws SQLException {
            if (stat != null) {
                stat.clearBatch();
            }
            if (preparedStat != null) {
                preparedStat.clearBatch();
            }
        }
        
        public int[] execute() throws SQLException {
            if (stat != null) {
                return stat.executeBatch();
            }
            if (preparedStat != null) {
                return preparedStat.executeBatch();
            }
            return null;
        }
        
        public void close() throws SQLException {
            if (stat != null) {
                stat.close();
            }
            if (preparedStat != null) {
                preparedStat.close();
            }
        }
    }
}