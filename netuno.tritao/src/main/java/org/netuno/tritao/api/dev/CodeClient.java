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

package org.netuno.tritao.api.dev;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.netuno.proteu.Proteu;
import org.netuno.proteu.Path;
import org.netuno.psamata.DB;
import org.netuno.psamata.Values;
import org.netuno.tritao.Web;
import org.netuno.tritao.auth.Auth;
import org.netuno.tritao.config.Config;
import org.netuno.tritao.hili.Hili;
import org.netuno.tritao.resource.Header;
import org.netuno.tritao.resource.Out;
import org.netuno.tritao.resource.Req;

/**
 * Code Client Service
 * @author Eduardo Fonseca Velasques - @eduveks
 */
@Path("/org/netuno/tritao/api/dev/CodeClient")
public class CodeClient extends Web {
    private static Logger logger = LogManager.getLogger(CodeClient.class);
    
    public CodeClient() {
        super();
    }

    public CodeClient(Proteu proteu, Hili hili) {
        super(proteu, hili);
    }
    
    @Override
    public void run() throws Exception {
        Header _header = resource(Header.class);
        if (!Auth.isDevAuthenticated(getProteu(), getHili())) {
            _header.status(Proteu.HTTPStatus.Forbidden403);
            return;
        }
        Req _req = resource(Req.class);
        Out _out = resource(Out.class);
        Values results = new Values();
        if (!_req.getString("source").equals("")) {
            
            String[] commandsLines = _req.getString("commands").split("\\;");
            for (String _commandLine : commandsLines) {
                String commandLine = _commandLine.trim();
                Values command = new Values();
                command.set("command", commandLine);
                try {
                    if (commandLine.toLowerCase().startsWith("select") || commandLine.toLowerCase().startsWith("script")) {
                        command.set("type", "query");
                        int count = 0;
                        Statement stat = null;
                        ResultSet rs = null;
                        long time = java.lang.System.currentTimeMillis();
                        try {
                            stat = Config.getDBExecutor(getProteu()).getConnection().createStatement();
                            rs = stat.executeQuery(commandLine);
                            Values columns = new Values();
                            command.set("columns", columns);
                            for (int x = 1; x <= rs.getMetaData().getColumnCount(); x++) {
                            	columns.add(rs.getMetaData().getColumnName(x));
                            }
                            int alternate = 0;
                            Values records = new Values();
                            command.set("records", records);
                            while (rs.next()) {
                                count++;
                                records.add(DB.getValues(rs));
                                alternate = alternate == 0 ? 1 : 0;
                                if (count == 1000) {
                                    break;
                                }
                            }
                        } catch (SQLException e) {
                            error(command, e);
                        } finally {
                            try {
                                if (rs != null) {
                                    rs.close();
                                }
                            } finally {
                                if (stat != null) {
                                    stat.close();
                                }
                            }
                        }
                        command.set("count", count);
                        command.set("time", java.lang.System.currentTimeMillis() - time);
                    } else {
                        command.set("type", "update");
                        Statement stat = null;
                        long time = java.lang.System.currentTimeMillis();
                        try {
                            stat = Config.getDBExecutor(getProteu()).getConnection().createStatement();
                            int count = stat.executeUpdate(commandLine);
                            command.set("count", count);
                            command.set("time", java.lang.System.currentTimeMillis() - time);
                        } catch (SQLException e) {
                            error(command, e);
                        } finally {
                            if (stat != null) {
                                stat.close();
                            }
                        }
                    }
                } catch (Exception e) {
                    error(command, e);
                }
                results.add(command);
            }
        }
        _out.json(results);
    }

    private void error(Values command, Exception e) throws Exception {
    	command.set("message", e.getMessage());
        command.set("error", true);
        logger.warn(command.getString("query") + command.getString("update"), e);
    }
    
}
