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
import org.netuno.tritao.config.Config;
import org.netuno.tritao.hili.Hili;
import org.netuno.tritao.util.Rule;
import org.netuno.tritao.util.TemplateBuilder;
import org.netuno.psamata.Values;

import java.util.Arrays;
import java.util.List;

/**
 * Group Management Service
 * @author Eduardo Fonseca Velasques - @eduveks
 */
public class Group {
    @SuppressWarnings("unchecked")
	public static void _main(Proteu proteu, Hili hili) throws Exception {
    	if (!Auth.isAuthenticated(proteu, hili, Auth.Type.SESSION, true)) {
            return;
        }
    	if (proteu.getRequestAll().getString("service").equals("json")) {
	    	String json = "";
	        if (proteu.getRequestAll().hasKey("data_uid")) {
	        	String dataId = proteu.getRequestAll().getString("data_uid");
	        	Values group = Config.getDataBaseBuilder(proteu).getGroupByUId(dataId);
	        	JSONObject jsonObject = new JSONObject();
	        	if (group != null) {
	                jsonObject.put("id", dataId);
	                jsonObject.put("label", group.getHtmlEncode("name"));
	            }
	        	json = jsonObject.toString();
	        } else {
				boolean allowAll = proteu.getRequestAll().getBoolean("allow_all");
		        List<Values> rsQuery = Config.getDataBaseBuilder(proteu).selectGroupSearch(proteu.getRequestAll().getString("q"));
		        JSONArray jsonArray = new JSONArray();
	            String groupsMode = proteu.getRequestAll().getString("groups_mode");
	            String[] groups = proteu.getRequestAll().getString("groups").split(",");
		        for (Values queryRow : rsQuery) {
		        	if (groupsMode.equals("exclude")
		        			&& Arrays.binarySearch(groups, queryRow.getString("group_name")) > -1) {
			        	continue;
		        	} else if (groupsMode.equals("only")
		        			&& Arrays.binarySearch(groups, queryRow.getString("group_name")) < 0) {
			        	continue;
		        	}
		        	String id = queryRow.getString("uid");
		        	String label = queryRow.getHtmlEncode("name");
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
    	if (proteu.getRequestAll().getString("service").equals("form_rules")) {
			Values group = Config.getDataBaseBuilder(proteu).getGroupByUId(proteu.getRequestAll().getString("uid"));
			if (group != null) {
				proteu.getOutput().print(listTablesRules(proteu, hili, group, "form", "0", 0));
			}
    		return;
    	} else if (proteu.getRequestAll().getString("service").equals("report_rules")) {
			Values group = Config.getDataBaseBuilder(proteu).getGroupByUId(proteu.getRequestAll().getString("uid"));
			if (group != null) {
				proteu.getOutput().print(listTablesRules(proteu, hili, group, "report", "0", 0));
			}
    		return;
    	}
    	Values data = new Values();
    	boolean restore = false;
        if (proteu.getRequestAll().getString("execute").equals("save") && proteu.getRequestAll().getString("uid").isEmpty()) {
        	int id = Config.getDataBaseBuilder(proteu).insertGroup(proteu.getRequestAll().getString("name"), proteu.getRequestAll().getString("admin").equals("1") ? "-1" : "0", proteu.getRequestAll().getString("mail"), proteu.getRequestAll().getString("active").equals("1") ? "1" : "0");
            if (id > 0) {
				Values group = Config.getDataBaseBuilder(proteu).getGroupById(Integer.toString(id));
            	proteu.getRequestAll().set("id", id);
				proteu.getRequestAll().set("uid", group.get("uid"));
            	saveRules(proteu, hili, group);
            	TemplateBuilder.output(proteu, hili, "group/notification/new_saved", data);
            } else {
            	TemplateBuilder.output(proteu, hili, "group/notification/error_exists", data);
            	restore = true;
            }
        } else if (proteu.getRequestAll().getString("execute").equals("save") && !proteu.getRequestAll().getString("uid").isEmpty()) {
        	Values group = Config.getDataBaseBuilder(proteu).getGroupByUId(proteu.getRequestAll().getString("uid"));
			if (group != null) {
				saveRules(proteu, hili, group);
				if (Config.getDataBaseBuilder(proteu).updateGroup(group.getString("id"), proteu.getRequestAll().getString("name"), proteu.getRequestAll().getString("admin").equals("1") ? "-1" : "0", proteu.getRequestAll().getString("mail"), proteu.getRequestAll().getString("active").equals("1") ? "1" : "0")) {
					TemplateBuilder.output(proteu, hili, "group/notification/saved", data);
				} else {
					TemplateBuilder.output(proteu, hili, "group/notification/error_exists", data);
					restore = true;
				}
			}
        } else if (proteu.getRequestAll().getString("execute").equals("delete") && !proteu.getRequestAll().getString("uid").isEmpty()) {
			Values group = Config.getDataBaseBuilder(proteu).getGroupByUId(proteu.getRequestAll().getString("uid"));
			if (group != null) {
                Config.getDataBaseBuilder(proteu).deleteGroupRules(group.getString("id"));
                Config.getDataBaseBuilder(proteu).deleteGroup(group.getString("id"));
                TemplateBuilder.output(proteu, hili, "group/notification/deleted", data);
				proteu.getRequestAll().remove("id");
				proteu.getRequestPost().remove("id");
				proteu.getRequestAll().remove("uid");
				proteu.getRequestPost().remove("uid");
            }
        } else if (proteu.getRequestAll().hasKey("uid") && proteu.getRequestAll().getString("uid").isEmpty()) {
        	TemplateBuilder.output(proteu, hili, "group/notification/new", data);
        }
        Values group = null;
        if (!proteu.getRequestAll().getString("uid").isEmpty()) {
			group = Config.getDataBaseBuilder(proteu).getGroupByUId(proteu.getRequestAll().getString("uid"));
        }
    	if (restore) {
        	data.set("group.id.value", proteu.getRequestAll().getString("id"));
			data.set("group.uid.value", proteu.getRequestAll().getString("uid"));
        	data.set("group.name.value", proteu.getRequestAll().getString("name"));
			data.set("group.mail.value", proteu.getRequestAll().getString("mail"));
        	if (proteu.getRequestAll().getString("admin").equals("1")) {
        		data.set("group.admin.checked", "checked");
        	} else {
        		data.set("group.admin.checked", "");
        	}
        	if (proteu.getRequestAll().getString("active").equals("1")) {
        		data.set("group.active.checked", "checked");
        	} else {
        		data.set("group.active.checked", "");
        	}
        } else if (group != null) {
        	data.set("group.id.value", group.getString("id"));
			data.set("group.uid.value", group.getString("uid"));
        	data.set("group.name.value", group.getString("name"));
			data.set("group.mail.value", group.getString("mail"));
        	if (group.getString("netuno_group").equals("-1")) {
        		data.set("group.admin.checked", "checked");
        	} else {
        		data.set("group.admin.checked", "");
        	}
        	if (group.getString("active").equals("1") || group.getString("active").equals("true")) {
        		data.set("group.active.checked", "checked");
        	} else {
        		data.set("group.active.checked", "");
        	}
        }
        TemplateBuilder.output(proteu, hili, "group/form", data);
        if (proteu.getRequestAll().hasKey("uid") && proteu.getRequestAll().getString("uid").isEmpty()) {
            TemplateBuilder.output(proteu, hili, "group/when_new", data);
        }
    }
    
    private static void saveRules(Proteu proteu, Hili hili, Values group) {
    	if (proteu.getRequestAll().getString("execute").equals("save")) {
            for (Values tritaoTable : Config.getDataBaseBuilder(proteu).selectTable()) {
            	if (tritaoTable.getInt("group_id") == -2) {
            		continue;
            	}
            	if (proteu.getRequestAll().hasKey("form_rule_read_"+ tritaoTable.getString("id"))
	    			&& proteu.getRequestAll().hasKey("form_rule_write_"+ tritaoTable.getString("id"))
	    			&& proteu.getRequestAll().hasKey("form_rule_delete_"+ tritaoTable.getString("id"))) {
	                Config.getDataBaseBuilder(proteu).setGroupRule(group.getString("id"), tritaoTable.getString("id")
	                        , proteu.getRequestAll().getBoolean("form_rule_active_"+ tritaoTable.getString("id")) ? "1" : "0"
	                        , proteu.getRequestAll().getString("form_rule_read_"+ tritaoTable.getString("id"))
	                        , proteu.getRequestAll().getString("form_rule_write_"+ tritaoTable.getString("id"))
	                        , proteu.getRequestAll().getString("form_rule_delete_"+ tritaoTable.getString("id")));
            	}
            }
			proteu.getRequestAll().set("report", "true");
            for (Values tritaoReport : Config.getDataBaseBuilder(proteu).selectTable()) {
            	if (tritaoReport.getInt("group_id") == -2) {
            		continue;
            	}
            	if (proteu.getRequestAll().hasKey("report_rule_read_"+ tritaoReport.getString("id"))
	    			&& proteu.getRequestAll().hasKey("report_rule_write_"+ tritaoReport.getString("id"))
	    			&& proteu.getRequestAll().hasKey("report_rule_delete_"+ tritaoReport.getString("id"))) {
	                Config.getDataBaseBuilder(proteu).setGroupRule(group.getString("id"), tritaoReport.getString("id")
	                        , proteu.getRequestAll().getBoolean("report_rule_active_"+ tritaoReport.getString("id")) ? "1" : "0"
	                        , proteu.getRequestAll().getString("report_rule_read_"+ tritaoReport.getString("id"))
	                        , proteu.getRequestAll().getString("report_rule_write_"+ tritaoReport.getString("id"))
	                        , proteu.getRequestAll().getString("report_rule_delete_"+ tritaoReport.getString("id")));
            	}
            }
			proteu.getRequestAll().set("report", "false");
        }
    }

    private static String listTablesRules(Proteu proteu, Hili hili, Values group, String type, String baseTableId, int level) throws Exception {
        List<Values> rsTableByParent;
        if (type.equals("report")) {
			proteu.getRequestAll().set("report", "true");
        	rsTableByParent = Config.getDataBaseBuilder(proteu).selectTablesByParent(baseTableId);
			proteu.getRequestAll().set("report", "false");
        } else {
        	rsTableByParent = Config.getDataBaseBuilder(proteu).selectTablesByParent(baseTableId);
        }
        String content = "";
        Values data = new Values();
        for (Values rowTritaoTableByParent : rsTableByParent) {
        	if (rowTritaoTableByParent.getInt("group_id") == -2) {
        		continue;
        	}
            String tableId = rowTritaoTableByParent.getString("id");
            List<Values> rules = Config.getDataBaseBuilder(proteu).selectGroupRule(Integer.toString(group.getInt("id")), tableId);
            Values rule = null;
            if (rules.size() > 0) {
                rule = rules.get(0);
            }
            data.set("rule.displayname", rowTritaoTableByParent.getString("displayname"));
            data.set("field.name", type +"_rule_active_"+ tableId);
            data.set("field.checked", rule != null && rule.getBoolean("active") ? "checked" : "");
        	data.set("rule.active", TemplateBuilder.getOutput(proteu, hili, "group/rule/active_field", data));
            data.set("rule.permission.read", listTablesRuleField(proteu, hili, data, type, tableId, rule, "rule_read"));
            data.set("rule.permission.write", listTablesRuleField(proteu, hili, data, type, tableId, rule, "rule_write"));
            data.set("rule.permission.delete", listTablesRuleField(proteu, hili, data, type, tableId, rule, "rule_delete"));
            data.set("rules.children", listTablesRules(proteu, hili, group, type, rowTritaoTableByParent.getString("id"), level + 1));
            content = content.concat(TemplateBuilder.getOutput(proteu, hili, "group/rule/item", data));
        }
        return content;
    }
    
    private static String listTablesRuleField(Proteu proteu, Hili hili, Values fieldData, String type, String tableId, Values rule, String field) throws Exception {
    	fieldData.set("field.name", type +"_"+ field +"_"+ tableId);
    	fieldData.set("field.option.none.value", Rule.NONE);
    	fieldData.set("field.option.all.value", Rule.ALL);
    	fieldData.set("field.option.group.value", Rule.GROUP);
    	fieldData.set("field.option.own.value", Rule.OWN);
    	fieldData.set("field.option.none.selected", rule != null && rule.getInt(field) == Rule.NONE ? " selected" : "");
    	fieldData.set("field.option.all.selected", rule != null && rule.getInt(field) == Rule.ALL ? " selected" : "");
    	fieldData.set("field.option.group.selected", rule != null && rule.getInt(field) == Rule.GROUP ? " selected" : "");
    	fieldData.set("field.option.own.selected", rule != null && rule.getInt(field) == Rule.OWN ? " selected" : "");
    	return TemplateBuilder.getOutput(proteu, hili, "group/rule/permission_field", fieldData);
    }
}
