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

package org.netuno.tritao.util;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.netuno.proteu.Proteu;
import org.netuno.psamata.Values;
import org.netuno.tritao.auth.Auth;
import org.netuno.tritao.config.Config;
import org.netuno.tritao.hili.Hili;
import org.netuno.tritao.db.Builder;

/**
 * Rule - User Permissions
 * @author Eduardo Fonseca Velasques - @eduveks
 */
public class Rule {
    private static Logger logger = LogManager.getLogger(Rule.class);

    protected static char[] LO;

    public static final int GLOBAL_GROUP_ID = 0;
    public static final int ADMINISTRATOR_GROUP_ID = -1;
    public static final int DEVELOPER_GROUP_ID = -2;

    public static final int NONE = 0;
    public static final int ALL = 1;
    public static final int GROUP = 2;
    public static final int OWN = 3;

    private int read = NONE;
    private int write = NONE;
    private int delete = NONE;
    
    private Values userData = new Values();
    private Values groupData = new Values();

    public Rule() {
        this.read = NONE;
        this.write = NONE;
        this.delete = NONE;
    }
    
    public Rule(Values userData, Values groupData) {
    	this.userData = userData;
    	this.groupData = groupData;
        this.read = NONE;
        this.write = NONE;
        this.delete = NONE;
    }

    public Rule(Values userData, Values groupData, int read, int write, int delete) {
    	this.userData = userData;
    	this.groupData = groupData;
        this.read = read;
        this.write = write;
        this.delete = delete;
    }

    public Values getUserData() {
        return userData;
    }

    public Values getGroupData() {
        return groupData;
    }

    public boolean isAdmin() {
        if (groupData.hasKey("netuno_group")) {
            int groupId = groupData.getInt("netuno_group");
            if (groupId == ADMINISTRATOR_GROUP_ID || groupId == DEVELOPER_GROUP_ID) {
                return true;
            }
        }
    	return false;
    }

    public boolean isDev() {
        if (groupData.hasKey("netuno_group")) {
            int groupId = groupData.getInt("netuno_group");
            if (groupId == DEVELOPER_GROUP_ID) {
                return true;
            }
        }
        return false;
    }

    public int getDelete() {
        return delete;
    }

    public int getRead() {
        return read;
    }

    public int getWrite() {
        return write;
    }

    public boolean haveAccess() {
        return Math.max(delete, Math.max(read, write)) > NONE;
    }

    public static Rule getRule(Proteu proteu, Hili hili) {
        if (Auth.getUser(proteu, hili) == null) {
            return new Rule();
        }
        if (Auth.getGroup(proteu, hili) == null) {
            return new Rule();
        }
        return getRule(Config.getDBBuilder(proteu), Auth.getUser(proteu, hili).getString("id"), Auth.getGroup(proteu, hili).getString("id"), "0");
    }
    
    public static Rule getRule(Proteu proteu, Hili hili, String table_id) {
        if (Auth.getUser(proteu, hili) == null) {
            return new Rule();
        }
        if (Auth.getGroup(proteu, hili) == null) {
            return new Rule();
        }
        return getRule(Config.getDBBuilder(proteu), Auth.getUser(proteu, hili).getString("id"), Auth.getGroup(proteu, hili).getString("id"), table_id);
    }

    public static Rule getRule(Proteu proteu, Hili hili, String user_id, String group_id) {
        return getRule(Config.getDBBuilder(proteu), user_id, group_id, "0");
    }
    
    public static Rule getRule(Proteu proteu, Hili hili, String user_id, String group_id, String table_id) {
        return getRule(Config.getDBBuilder(proteu), user_id, group_id, table_id);
    }

    public static Rule getRule(Builder dbBuilder, String user_id, String group_id, String table_id) {
    	if (user_id.isEmpty() || group_id.isEmpty() || table_id.isEmpty()) {
    		return new Rule();
    	}
        Values user = dbBuilder.getUserById(user_id);
        Values group = dbBuilder.getGroupById(group_id);
        List<Values> tables = dbBuilder.selectTable(table_id);
        if (user != null && group != null && tables.size() > 0) {
            Values table = tables.get(0);
            Values tableGroup = null;
            if (table.getInt("group_id") > 0) {
                List<Values> rsTableGroup = dbBuilder.selectGroup(table.getString("group_id"));
                if (rsTableGroup.size() > 0) {
                    tableGroup = rsTableGroup.get(0);
                }
            }
            logger.info("RULE TO USER=" + user.getString("name") + " TABLE=" + table.getString("name"));
            if (group.getInt("netuno_group") == Rule.DEVELOPER_GROUP_ID) {
                logger.info("RULE # Developer");
                return new Rule(user, group, Rule.ALL, Rule.ALL, Rule.ALL);
            }
            if (group.getInt("netuno_group") == Rule.ADMINISTRATOR_GROUP_ID && (tableGroup != null && tableGroup.getInt("netuno_group") == Rule.DEVELOPER_GROUP_ID)) {
                return new Rule(user, group);
            }
            if (group.getInt("netuno_group") == Rule.ADMINISTRATOR_GROUP_ID) {
                logger.info("RULE # Administrator");
                return new Rule(user, group, Rule.ALL, Rule.ALL, Rule.ALL);
            }
            if (table.getInt("group_id") < 0) {
                logger.info("RULE # TABLE GROUP "+ table.getInt("group_id"));
                return new Rule(user, group);
            }
            List<Values> userRules = dbBuilder.selectUserRule(user_id, table_id, "1");
            List<Values> groupRules = dbBuilder.selectGroupRule(user.getString("group_id"), table_id, "1");
            Rule rule = new Rule(user, group);
            if (userRules.size() > 0) {
                Values userRule = userRules.get(0);
                logger.info("RULE USER # read=" + userRule.getString("rule_read") + " write=" + userRule.getString("rule_write") + " delete=" +  userRule.getString("rule_delete"));
                rule = new Rule(user, group, userRule.getInt("rule_read"), userRule.getInt("rule_write"), userRule.getInt("rule_delete"));
            }
            if (groupRules.size() > 0) {
                Values groupRule = groupRules.get(0);
                logger.info("RULE GROUP # read=" + groupRule.getString("rule_read") + " write=" + groupRule.getString("rule_write") + " delete=" +  groupRule.getString("rule_delete"));
                rule = new Rule(user, group
                		, rule.getRead() > 0 ? rule.getRead() : groupRule.getInt("rule_read")
                        , rule.getWrite() > 0 ? rule.getWrite() : groupRule.getInt("rule_write")
                        , rule.getDelete() > 0 ? rule.getDelete() : groupRule.getInt("rule_delete"));
            }
            return rule;
        } else if (user != null && group != null) {
            return new Rule(user, group);
        }
        logger.info("RULE # NOT FOUND");
        return new Rule();
    }

    public static boolean hasDesignFieldViewAccess(Proteu proteu, Hili hili, Values design) {
    	if (design.getInt("view_user_id") == 0 && design.getInt("view_group_id") == 0) {
    		return true;
    	}
    	if (design.getInt("view_user_id") != 0) {
    		if (design.getInt("view_user_id") == Auth.getUser(proteu, hili).getInt("id")) {
    			return true;
    		}
    		return false;
    	}
    	if (design.getInt("view_group_id") != 0) {
    		if (design.getInt("view_group_id") < 0 && Rule.getRule(proteu, hili).isAdmin()) {
    			return true;
    		}
    		if (design.getInt("view_group_id") == Auth.getGroup(proteu, hili).getInt("id")) {
    			return true;
    		}
    		return false;
    	}
    	return false;
    }

    public static boolean hasDesignFieldEditAccess(Proteu proteu, Hili hili, Values design) {
        if (design.getInt("edit_user_id") == 0 && design.getInt("edit_group_id") == 0) {
            return true;
        }
        if (design.getInt("edit_user_id") != 0) {
            if (design.getInt("edit_user_id") == Auth.getUser(proteu, hili).getInt("id")) {
                return true;
            }
            return false;
        }
        if (design.getInt("edit_group_id") != 0) {
            if (design.getInt("edit_group_id") < 0 && Rule.getRule(proteu, hili).isAdmin()) {
                return true;
            }
            if (design.getInt("edit_group_id") == Auth.getGroup(proteu, hili).getInt("id")) {
                return true;
            }
            return false;
        }
        return false;
    }
}
