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

package org.netuno.tritao;

import org.json.JSONArray;
import org.json.JSONObject;
import org.netuno.proteu.Proteu;
import org.netuno.psamata.Values;

import java.util.Arrays;
import java.util.List;

import org.netuno.tritao.config.Config;
import org.netuno.tritao.config.Hili;
import org.netuno.tritao.resource.Lang;
import org.netuno.tritao.util.Rule;
import org.netuno.tritao.util.TemplateBuilder;

/**
 * User Management Service
 * @author Eduardo Fonseca Velasques - @eduveks
 */
public class User {
    @SuppressWarnings("unchecked")
	public static void _main(Proteu proteu, Hili hili) throws Exception {
    	if (!Auth.isAuthenticated(proteu, hili, Auth.Type.SESSION, true)) {
            return;
        }
    	Lang lang = new Lang(proteu, hili);
    	if (proteu.getRequestAll().getString("service").equals("json")) {
	    	String json = "";
	        if (proteu.getRequestAll().hasKey("data_uid")) {
	        	String dataId = proteu.getRequestAll().getString("data_uid");
	        	Values user = Config.getDataBaseBuilder(proteu).getUserByUId(dataId);
	        	JSONObject jsonObject = new JSONObject();
	        	if (user != null) {
	                jsonObject.put("id", dataId);
	                jsonObject.put("label", user.getHtmlEncode("user") +" - "+ user.getHtmlEncode("name"));
	            }
	        	json = jsonObject.toString();
	        } else {
				boolean allowAll = proteu.getRequestAll().getBoolean("allow_all");
		        List<Values> rsQuery = Config.getDataBaseBuilder(proteu).selectUserSearch(proteu.getRequestAll().getString("q"));
		        JSONArray jsonArray = new JSONArray();
		        String usersMode = proteu.getRequestAll().getString("users_mode");
	            String[] users = proteu.getRequestAll().getString("users").split(",");
	            String groupsMode = proteu.getRequestAll().getString("groups_mode");
	            String[] groups = proteu.getRequestAll().getString("groups").split(",");
	            java.util.Arrays.sort(users);
	            java.util.Arrays.sort(groups);
		        for (Values queryRow : rsQuery) {
		        	String id = queryRow.getString("uid");
		        	if (queryRow.getString("id").equals(Auth.getUser(proteu, hili, Auth.Type.SESSION).getString("id"))) {
		        		if (!proteu.getRequestAll().getBoolean("allow_user_logged")) {
		        			continue;
		        		}
		        	}
		        	if (groupsMode.equals("exclude")
		        			&& Arrays.binarySearch(groups, queryRow.getString("group_name")) > -1) {
			        	continue;
		        	} else if (groupsMode.equals("only")
		        			&& Arrays.binarySearch(groups, queryRow.getString("group_name")) < 0) {
			        	continue;
		        	}
		        	if (usersMode.equals("exclude")
		        			&& Arrays.binarySearch(users, queryRow.getString("user")) > -1) {
			        	continue;
			        } else if (usersMode.equals("only")
			        		&& Arrays.binarySearch(users, queryRow.getString("user")) < 0) {
			        	continue;
		        	}
		        	String label = queryRow.getHtmlEncode("user") + " - " + queryRow.getHtmlEncode("name");
					JSONObject jsonObject = new JSONObject();
		            jsonObject.put("id", id);
		            jsonObject.put("label", label);
		            if (allowAll == false && (queryRow.getString("active").length() == 0
							|| queryRow.getString("active").equals("false")
							|| queryRow.getString("active").equals("0"))) {
		                jsonObject.put("disabled", true);
		            } else {
		            	jsonObject.put("disabled", false);
		            }
		            jsonArray.put(jsonObject);
		        }
		        json = jsonArray.toString();
	        }
	        String callback = proteu.getRequestAll().getString("callback");
	        if (callback.length() > 0) {
	        	proteu.getOutput().print(callback);
	        	proteu.getOutput().print("(");
	        }
	        proteu.getOutput().print(json);
	        if (callback.length() > 0) {
	        	proteu.getOutput().print(")");
	        }
	        return;
    	}
    	if (!Rule.getRule(proteu, hili).isAdmin()) {
    		return;
    	}
		if (proteu.getRequestAll().getString("service").equals("impersonate")) {
			Values user = Config.getDataBaseBuilder(proteu).getUserByUId(proteu.getRequestAll().getString("uid"));
			Auth.backupSession(proteu, hili);
			if (user != null && Auth.userSignIn(proteu, hili, user.getInt("id"))) {
				JSONObject jsonResult = new JSONObject();
				jsonResult.put("result", true);
				proteu.getOutput().print(jsonResult.toString());
				return;
			} else {
				JSONObject jsonResult = new JSONObject();
				jsonResult.put("result", false);
				proteu.getOutput().print(jsonResult.toString());
				return;
			}
		}
    	if (proteu.getRequestAll().getString("service").equals("form_rules")) {
			Values user = Config.getDataBaseBuilder(proteu).getUserByUId(proteu.getRequestAll().getString("uid"));
			if (user != null) {
				Values group = Config.getDataBaseBuilder(proteu).getGroupByUId(user.getString("group_uid"));
				proteu.getOutput().print(listTablesRules(proteu, hili, user, lang, group != null ? group.getString("id") : "0", "form", "0", 0));
			}
    		return;
    	} else if (proteu.getRequestAll().getString("service").equals("report_rules")) {
			Values user = Config.getDataBaseBuilder(proteu).getUserByUId(proteu.getRequestAll().getString("uid"));
			if (user != null) {
				Values group = Config.getDataBaseBuilder(proteu).getGroupByUId(user.getString("group_uid"));
				proteu.getOutput().print(listTablesRules(proteu, hili, user, lang, group != null ? group.getString("id") : "0", "report", "0", 0));
			}
    		return;
    	}
    	Values data = new Values();
    	boolean restore = false;
        if (proteu.getRequestAll().getString("execute").equals("save") && proteu.getRequestAll().getString("uid").isEmpty()) {
			Values group = Config.getDataBaseBuilder(proteu).getGroupByUId(proteu.getRequestAll().getString("group_uid"));
        	int id = Config.getDataBaseBuilder(proteu).insertUser(
        			proteu.getRequestAll().getString("name"), 
        			proteu.getRequestAll().getString("username"), 
        			proteu.getRequestAll().getString("password").isEmpty() ? "" : Config.getPasswordBuilder(proteu).getCryptPassword(proteu, hili, proteu.getRequestAll().getString("username"), proteu.getRequestAll().getString("password")),
					proteu.getRequestAll().getString("mail"),
					group != null ? group.getString("id") : "0",
        			proteu.getRequestAll().getString("active"));
            if (id > 0) {
				Values user = Config.getDataBaseBuilder(proteu).getUserById(Integer.toString(id));
            	proteu.getRequestAll().set("id", id);
				proteu.getRequestAll().set("uid", user.get("uid"));
            	saveRules(proteu, hili, user);
            	if (new java.io.File(Config.getPathAppImages(proteu).concat("/avatar/generic_user.jpg")).exists()) {
	            	org.netuno.psamata.io.FileUtils.copy(Config.getPathAppImages(proteu).concat("/avatar/generic_user.jpg"),
	            			Config.getPathAppImages(proteu).concat("/avatar/")
	            			.concat(Config.getDabaBase(proteu)).concat("_")
	            			.concat(proteu.getRequestAll().getString("username"))
	            			.concat(".jpg"));
            	}
            	TemplateBuilder.output(proteu, hili, "user/notification/new_saved", data);
            } else {
            	TemplateBuilder.output(proteu, hili, "user/notification/error_exists", data);
            	restore = true;
            }
        } else if (proteu.getRequestAll().getString("execute").equals("save") && !proteu.getRequestAll().getString("uid").isEmpty()) {
        	Values user = Config.getDataBaseBuilder(proteu).getUserByUId(proteu.getRequestAll().getString("uid"));
			if (user != null) {
				if (!user.getString("id").equals(Auth.getUser(proteu, hili, Auth.Type.SESSION).getString("id"))) {
					saveRules(proteu, hili, user);
					Values group = Config.getDataBaseBuilder(proteu).getGroupByUId(proteu.getRequestAll().getString("group_uid"));

					if (Config.getDataBaseBuilder(proteu).updateUser(
							user.getString("id"), proteu.getRequestAll().getString("name"),
							proteu.getRequestAll().getString("username"),
							proteu.getRequestAll().getString("password").isEmpty() ? "" : Config.getPasswordBuilder(proteu).getCryptPassword(proteu, hili, proteu.getRequestAll().getString("username"), proteu.getRequestAll().getString("password")),
							proteu.getRequestAll().getString("mail"),
							group != null ? group.getString("id") : "0",
							proteu.getRequestAll().getString("active"))) {
						TemplateBuilder.output(proteu, hili, "user/notification/saved", data);
					} else {
						TemplateBuilder.output(proteu, hili, "user/notification/error_exists", data);
						restore = true;
					}
				}
			}
        } else if (proteu.getRequestAll().getString("execute").equals("delete") && !proteu.getRequestAll().getString("uid").isEmpty()) {
			Values user = Config.getDataBaseBuilder(proteu).getUserByUId(proteu.getRequestAll().getString("uid"));
			if (user != null) {
				if (!user.getString("id").equals(Auth.getUser(proteu, hili, Auth.Type.SESSION).getString("id"))) {
					java.io.File fileAvatar = new java.io.File(Config.getPathAppImages(proteu).concat("/avatar/")
							.concat(Config.getDabaBase(proteu)).concat("_")
							.concat(user.getString("user"))
							.concat(".jpg"));
					if (fileAvatar.exists()) {
						fileAvatar.delete();
					}
					Config.getDataBaseBuilder(proteu).deleteUserRules(user.getString("id"));
					Config.getDataBaseBuilder(proteu).deleteUser(user.getString("id"));
					TemplateBuilder.output(proteu, hili, "user/notification/deleted", data);
					proteu.getRequestAll().remove("id");
					proteu.getRequestPost().remove("id");
					proteu.getRequestAll().remove("uid");
					proteu.getRequestPost().remove("uid");
				}
            }
		} else if (proteu.getRequestAll().hasKey("uid") && proteu.getRequestAll().getString("uid").isEmpty()) {
        	TemplateBuilder.output(proteu, hili, "user/notification/new", data);
        }
		Values user = null;
		if (!proteu.getRequestAll().getString("uid").isEmpty()) {
			user = Config.getDataBaseBuilder(proteu).getUserByUId(proteu.getRequestAll().getString("uid"));
		}
        if (restore) {
        	data.set("user.id.value", proteu.getRequestAll().getString("id"));
			data.set("user.uid.value", proteu.getRequestAll().getString("uid"));
        	data.set("user.name.value", proteu.getRequestAll().getString("name"));
        	data.set("user.username.value", proteu.getRequestAll().getString("user"));
			data.set("user.mail.value", proteu.getRequestAll().getString("mail"));
        	data.set("user.group_id.value", proteu.getRequestAll().getString("group_id"));
			data.set("user.group_uid.value", proteu.getRequestAll().getString("group_uid"));
			data.set("user.password.validation", "");
			if (proteu.getRequestAll().getString("active").equals("1")) {
        		data.set("user.active.checked", "checked");
        	} else {
        		data.set("user.active.checked", "");
        	}
        } else if (user != null) {
        	data.set("user.id.value", user.getString("id"));
			data.set("user.uid.value", user.getString("uid"));
        	data.set("user.name.value", user.getString("name"));
        	data.set("user.username.value", user.getString("user"));
			data.set("user.mail.value", user.getString("mail"));
        	data.set("user.group_id.value", user.getString("group_id"));
			Values group = Config.getDataBaseBuilder(proteu).getGroupById(user.getString("group_id"));
			if (group != null) {
				data.set("user.group_uid.value", group.getString("uid"));
			}
			data.set("user.password.validation", "");
        	if (user.getString("active").equals("1") || user.getString("active").equals("true")) {
        		data.set("user.active.checked", "checked");
        	} else {
        		data.set("user.active.checked", "");
        	}
        } else {
			data.set("user.password.validation", "required");
		}
        TemplateBuilder.output(proteu, hili, "user/form", data);
		if (proteu.getRequestAll().hasKey("uid") && proteu.getRequestAll().getString("uid").isEmpty()) {
            TemplateBuilder.output(proteu, hili, "user/when_new", data);
        }
    }
    
    private static void saveRules(Proteu proteu, Hili hili, Values user) {
    	if (proteu.getRequestAll().getString("execute").equals("save")) {
            for (Values tritaoTable : Config.getDataBaseBuilder(proteu).selectTable()) {
            	if (tritaoTable.getInt("group_id") == -2) {
            		continue;
            	}
            	List<Values> rulesGroup = Config.getDataBaseBuilder(proteu).selectGroupRule(user.getString("group_id"), tritaoTable.getString("id"));
                Values ruleGroup = null;
                if (rulesGroup.size() > 0) {
                    ruleGroup = rulesGroup.get(0);
                }
            	if (proteu.getRequestAll().hasKey("form_rule_active_"+ tritaoTable.getString("id"))
            		|| proteu.getRequestAll().hasKey("form_rule_read_"+ tritaoTable.getString("id"))
	    			|| proteu.getRequestAll().hasKey("form_rule_write_"+ tritaoTable.getString("id"))
	    			|| proteu.getRequestAll().hasKey("form_rule_delete_"+ tritaoTable.getString("id"))) {
	                Config.getDataBaseBuilder(proteu).setUserRule(user.getString("id"), tritaoTable.getString("id")
	                        , ruleGroup != null && ruleGroup.getInt("active") > 0 ? ruleGroup.getString("active") : proteu.getRequestAll().getBoolean("form_rule_active_"+ tritaoTable.getString("id")) ? "1" : "0"
	                        , ruleGroup != null && ruleGroup.getInt("rule_read") > 0 ? ruleGroup.getString("rule_read") : proteu.getRequestAll().getString("form_rule_read_"+ tritaoTable.getString("id"))
	                        , ruleGroup != null && ruleGroup.getInt("rule_write") > 0 ? ruleGroup.getString("rule_write") : proteu.getRequestAll().getString("form_rule_write_"+ tritaoTable.getString("id"))
	                        , ruleGroup != null && ruleGroup.getInt("rule_delete") > 0 ? ruleGroup.getString("rule_delete") : proteu.getRequestAll().getString("form_rule_delete_"+ tritaoTable.getString("id")));
            	}
            }
			proteu.getRequestAll().set("report", "true");
            for (Values tritaoReport : Config.getDataBaseBuilder(proteu).selectTable()) {
            	if (tritaoReport.getInt("group_id") == -2) {
            		continue;
            	}
            	List<Values> rulesGroup = Config.getDataBaseBuilder(proteu).selectGroupRule(user.getString("group_id"), tritaoReport.getString("id"));
                Values ruleGroup = null;
                if (rulesGroup.size() > 0) {
                    ruleGroup = rulesGroup.get(0);
                }
            	if (proteu.getRequestAll().hasKey("report_rule_active_"+ tritaoReport.getString("id"))
                	|| proteu.getRequestAll().hasKey("report_rule_read_"+ tritaoReport.getString("id"))
	    			|| proteu.getRequestAll().hasKey("report_rule_write_"+ tritaoReport.getString("id"))
	    			|| proteu.getRequestAll().hasKey("report_rule_delete_"+ tritaoReport.getString("id"))) {
	                Config.getDataBaseBuilder(proteu).setUserRule(user.getString("id"), tritaoReport.getString("id")
	                        , ruleGroup != null && ruleGroup.getInt("active") > 0 ? ruleGroup.getString("active") : proteu.getRequestAll().getBoolean("report_rule_active_"+ tritaoReport.getString("id")) ? "1" : "0"
	                        , ruleGroup != null && ruleGroup.getInt("rule_read") > 0 ? ruleGroup.getString("rule_read") : proteu.getRequestAll().getString("report_rule_read_"+ tritaoReport.getString("id"))
	                        , ruleGroup != null && ruleGroup.getInt("rule_write") > 0 ? ruleGroup.getString("rule_write") : proteu.getRequestAll().getString("report_rule_write_"+ tritaoReport.getString("id"))
	                        , ruleGroup != null && ruleGroup.getInt("rule_delete") > 0 ? ruleGroup.getString("rule_delete") : proteu.getRequestAll().getString("report_rule_delete_"+ tritaoReport.getString("id")));
            	}
            }
			proteu.getRequestAll().set("report", "false");
        }
    }
    
    private static String listTablesRules(Proteu proteu, Hili hili, Values user, Lang lang, String groupId, String type, String id, int level) throws Exception {
        List<Values> rsTableByParent;
        if (type.equals("report")) {
			proteu.getRequestAll().set("report", "true");
        	rsTableByParent = Config.getDataBaseBuilder(proteu).selectTablesByParent(id);
			proteu.getRequestAll().set("report", "false");
        } else {
        	rsTableByParent = Config.getDataBaseBuilder(proteu).selectTablesByParent(id);
        }
        String content = "";
        Values data = new Values();
        for (Values rowTritaoTableByParent : rsTableByParent) {
        	if (rowTritaoTableByParent.getInt("group_id") == -2) {
        		continue;
        	}
            String tableId = rowTritaoTableByParent.getString("id");
            List<Values> rules = Config.getDataBaseBuilder(proteu).selectUserRule(Integer.toString(user.getInt("id")), tableId);
			Values rule = null;
            if (rules.size() > 0) {
                rule = rules.get(0);
            }
            List<Values> rulesGroup = Config.getDataBaseBuilder(proteu).selectGroupRule(groupId, tableId);
            Values ruleGroup = null;
            if (rulesGroup.size() > 0) {
                ruleGroup = rulesGroup.get(0);
            }
            data.set("rule.displayname", rowTritaoTableByParent.getString("displayname"));
            if (ruleGroup != null  && ruleGroup.getBoolean("active")) {
            	data.set("rule.active", TemplateBuilder.getOutput(proteu, hili, "user/rule/active_readonly", data));
            } else {
                data.set("field.name", type +"_rule_active_"+ tableId);
                data.set("field.checked", rule != null && (rule.getBoolean("active") || rule.getString("active").equals("1")) ? "checked" : "");
            	data.set("rule.active", TemplateBuilder.getOutput(proteu, hili, "user/rule/active_field", data));
            }
            data.set("rule.permission.read", listTablesRuleField(proteu, hili, lang, data, type, tableId, rule, "rule_read", ruleGroup));
            data.set("rule.permission.write", listTablesRuleField(proteu, hili, lang, data, type, tableId, rule, "rule_write", ruleGroup));
            data.set("rule.permission.delete", listTablesRuleField(proteu, hili, lang, data, type, tableId, rule, "rule_delete", ruleGroup));
            data.set("rules.children", listTablesRules(proteu, hili, user, lang, groupId, type, rowTritaoTableByParent.getString("id"), level + 1));
            content = content.concat(TemplateBuilder.getOutput(proteu, hili, "user/rule/item", data));
        }
        return content;
    }
    
    private static String listTablesRuleField(Proteu proteu, Hili hili, Lang lang, Values fieldData, String type, String tableId, Values rule, String field, Values ruleGroup) throws Exception {
        if (ruleGroup != null  && ruleGroup.getInt(field) > Rule.NONE) {
        	String content = "";
            switch (ruleGroup.getInt(field)) {
                case Rule.ALL:
                	content = lang.get("netuno.rules.option.all");
                    break;
                case Rule.GROUP:
                	content = lang.get("netuno.rules.option.group");
                    break;
                case Rule.OWN:
                	content = lang.get("netuno.rules.option.own");
                    break;
                default:
                    break;
            }
            fieldData.set("readonly.content", content);
            return TemplateBuilder.getOutput(proteu, hili, "user/rule/permission_readonly", fieldData);
        } else {
        	fieldData.set("field.name", type +"_"+ field +"_"+ tableId);
        	fieldData.set("field.option.none.value", Rule.NONE);
        	fieldData.set("field.option.all.value", Rule.ALL);
        	fieldData.set("field.option.group.value", Rule.GROUP);
        	fieldData.set("field.option.own.value", Rule.OWN);
        	fieldData.set("field.option.none.selected", rule != null && rule.getInt(field) == Rule.NONE ? " selected" : "");
        	fieldData.set("field.option.all.selected", rule != null && rule.getInt(field) == Rule.ALL ? " selected" : "");
        	fieldData.set("field.option.group.selected", rule != null && rule.getInt(field) == Rule.GROUP ? " selected" : "");
        	fieldData.set("field.option.own.selected", rule != null && rule.getInt(field) == Rule.OWN ? " selected" : "");
        	return TemplateBuilder.getOutput(proteu, hili, "user/rule/permission_field", fieldData);
        }
    }
}
