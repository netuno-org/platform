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

import org.netuno.psamata.DB;
import org.netuno.psamata.PsamataException;
import org.netuno.psamata.Values;
import java.util.List;
import java.util.Map;
import java.util.ArrayList;
import java.util.HashMap;

import org.netuno.proteu.Proteu;
import org.netuno.tritao.auth.Auth;
import org.netuno.tritao.com.Component;
import org.netuno.tritao.com.ComponentData;
import org.netuno.tritao.com.ComponentData.Type;
import org.netuno.tritao.com.Configuration;
import org.netuno.tritao.com.ParameterType;
import org.netuno.tritao.config.Config;
import org.netuno.tritao.hili.Hili;
import org.netuno.tritao.db.Builder;
import org.netuno.tritao.db.manager.ManagerBase;

/**
 * Link - Forms Relations
 * @author Eduardo Fonseca Velasques - @eduveks
 */
public class Link extends ManagerBase {
    private String link = "";
    private String filter = "";
    private int deep = 0;
    private boolean onlyActives = false;
    private boolean big = false;
    private String rootTableId = "";
    private String rootTableName = "";
    private String tableId = "";
    private String tableName = "";
    private Map<String, Integer> filterIds = new HashMap<String, Integer>();
    private List<String> rootFieldNames = new ArrayList<String>();
    private List<String> tables = new ArrayList<String>();
    private List<String> tablesSeparators = new ArrayList<String>();
    private List<String> fields = new ArrayList<String>();
    private List<String> links = new ArrayList<String>();

    public Link(Proteu proteu, Hili hili, String key, String link) {
        super(proteu, hili, key);
        this.link = link;
    }

    public Link(Proteu proteu, Hili hili, String key, String link, String filter) {
        super(proteu, hili, key);
        this.link = link;
        this.filter = filter;
    }
    
    public Link(Proteu proteu, Hili hili, String key, String link, String filter, int deep) {
        super(proteu, hili, key);
        this.link = link;
        this.filter = filter;
        this.deep = deep;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getFilter() {
        return filter;
    }

    public void setFilter(String filter) {
        this.filter = filter;
    }

    public int getDeep() {
        return deep;
    }

    public void setDeep(int deep) {
        this.deep = deep;
    }

    public boolean isOnlyActives() {
        return onlyActives;
    }

    public void setOnlyActives(boolean onlyActives) {
        this.onlyActives = onlyActives;
    }
    
    public Map<String, Integer> getFilterIds() {
		return filterIds;
	}

	public List<String> getTables() {
        return tables;
    }

    public List<String> getTablesSeparators() {
        return tablesSeparators;
    }
    
    public List<String> getFields() {
        return fields;
    }

    public boolean isBig() {
        return big;
    }

    public String getQuery() {
        return queryBuilder(link, filter, deep);
    }

    public String getQuery(int limit) {
        String query = queryBuilder(link, filter, deep) + (
                isMSSQL() ? " offset 0 rows fetch next "+ limit +" rows only" :
                        " limit "+ limit
        );
        return query;
    }

    public String getRootTableId() {
        return rootTableId;
    }
    
    public String getRootTableName() {
        return rootTableName;
    }
    
    public List<String> getRootFieldNames() {
        return rootFieldNames;
    }
    
    public String getTableId() {
        return tableId;
    }
    
    public String getTableName() {
        return tableName;
    }

    public List<String> getFieldsByTableName(String tableName) {
    	List<String> tableFields = new ArrayList<String>();
    	String prefixTableName = tableName.concat("$");
    	for (String field : fields) {
    		if (field.startsWith(prefixTableName)) {
    			tableFields.add(field);
    		}
    	}
    	return tableFields;
    }
    
    public List<String> getFieldNamesByTableName(String tableName) {
    	List<String> tableFieldNames = new ArrayList<String>();
    	String prefixTableName = tableName.concat("$");
    	for (String field : fields) {
    		if (field.startsWith(prefixTableName)) {
    			tableFieldNames.add(field.substring(field.indexOf("$") + 1));
    		}
    	}
    	return tableFieldNames;
    }

    public List<String> getLinks() {
        return links;
    }

    private String queryBuilder(String link, String filter, int deep) {
        String query = queryBuilder(link, filter, null, deep, 0);
        return query;
    }

    private String queryBuilder(String link, String filter, String separator, int deep, int level) {
        Builder builder = Config.getDataBaseBuilder(getProteu(), getKey());
        StringBuilder select = new StringBuilder();
        StringBuilder from = new StringBuilder();
        StringBuilder where = new StringBuilder();
        StringBuilder order = new StringBuilder();
    	List<String> whereFilterColumns = new ArrayList<String>();
    	queryBuilder(link, filter, separator, deep, level, select, from, where, order, whereFilterColumns);
    	if (filter.length() > 0) {
    		String filterTerm = filter.replaceAll("\\s", "%");
        	String columnsConcatenation = "";
    		for (String columnName : whereFilterColumns) {
    			if (columnsConcatenation.isEmpty()) {
        			columnsConcatenation = columnName;
    			} else {
        			columnsConcatenation = builder.concatenation(columnsConcatenation, "' '");
        			columnsConcatenation = builder.concatenation(columnsConcatenation, columnName);
    			}
    		}
            where = where.append(" and ".concat(builder.searchComparison(columnsConcatenation)).concat(" LIKE ").concat(builder.searchComparison("'%" + DB.sqlInjection(filterTerm) + "%'")));
    	}
    	String query = "select ".concat(select.toString()).concat(" from ").concat(from.toString()).concat(" where ").concat(where.toString()).concat(order.toString().isEmpty() ? " order by 1" : " order by ".concat(order.toString()));
    	return query;
    }
    
    private void queryBuilder(String link, String filter, String separator, int deep, int level, StringBuilder select, StringBuilder from, StringBuilder where, StringBuilder order, List<String> whereFilterColumns) {
        if (links.contains(link)) {
    		return;
    	}
    	links.add(link);
    	String tableName = "";
		try {
			tableName = DB.sqlInjectionRawName(link.substring(0, link.indexOf(":")));
		} catch (PsamataException e) {
			throw new Error(e);
		}
		Builder builder = Config.getDataBaseBuilder(getProteu(), getKey());
		String tableNameAlias = tableName +"_"+ deep +"_"+ level;
        String[] fieldsNames = link.substring(link.indexOf(":") + 1).split("\\,");
        for (int i = 0; i < fieldsNames.length; i++) {
        	try {
        		fieldsNames[i] = DB.sqlInjectionRawName(fieldsNames[i]);
    		} catch (PsamataException e) {
    			throw new Error(e);
    		}
        }
        if (select.length() > 0) {
            select.append(", ");
        }
        select.append(builder.escape(tableNameAlias).concat(".active as ".concat(builder.escape(tableName.concat("$active")))));
        if (level == deep) {
            select.append(", ").append(builder.escape(tableNameAlias).concat(".id as ").concat(builder.escape("id")));
            select.append(", ").append(builder.escape(tableNameAlias).concat(".uid as ").concat(builder.escape("uid")));
            select.append(", ").append(builder.escape(tableNameAlias).concat(".active as ").concat(builder.escape("active")));
            where.append("1 = 1");
        }
        Values rowTableByName = builder.selectTableByName(tableName);
        if (from.length() == 0) {
            from.append(builder.escape(tableName).concat(" as ").concat(builder.escape(tableNameAlias)));
        }
        if (rowTableByName != null) {
            if (level == 0) {
                this.rootTableId = rowTableByName.getString("id");
                this.rootTableName = rowTableByName.getString("name");
            }
            if (level == deep) {
                this.tableId = rowTableByName.getString("id");
                this.tableName = rowTableByName.getString("name");
            }
            if (level >= deep) {
        		tables.add(tableName);
        		tablesSeparators.add(separator);
        	}
            if (rowTableByName.getInt("big") == 1) {
                big = true;
            }
            if (onlyActives) {
                where.append(" and ").append(tableNameAlias).append(".active = "+ getBuilder().booleanTrue());
            }
            for (String filterTableName : filterIds.keySet()) {
            	if (tableName.equalsIgnoreCase(filterTableName)) {
            		where.append(" and ").append(tableNameAlias).append(".id = "+ filterIds.get(filterTableName));
            		break;
            	}
            }
            if (!Rule.getRule(getProteu(), getHili()).isAdmin()) {
	            if (rowTableByName.getBoolean("control_user") && rowTableByName.getBoolean("control_group")) {
                    where.append(" and (").append(builder.escape(tableNameAlias)).append(".group_id = ").append(Auth.getGroup(getProteu(), getHili()).getString("id"))
	                        .append(" or ").append(builder.escape(tableNameAlias)).append(".user_id = ").append(Auth.getUser(getProteu(), getHili()).getString("id")).append(")");
	            } else if (rowTableByName.getBoolean("control_user")) {
                    where.append(" and ").append(builder.escape(tableNameAlias)).append(".user_id = ").append(Auth.getUser(getProteu(), getHili()).getString("id"));
	            } else if (rowTableByName.getBoolean("control_group")) {
                    where.append(" and ").append(builder.escape(tableNameAlias)).append(".group_id = ").append(Auth.getGroup(getProteu(), getHili()).getString("id"));
	            }
            }
            for (int i = 0; i < fieldsNames.length; i++) {
                String fieldName = builder.escape(tableNameAlias) + "." + builder.escape(fieldsNames[i]);
                if (level == 0) {
                    rootFieldNames.add(fieldsNames[i]);
                }
                List<Values> rsDesign = builder.selectTableDesign(rowTableByName.getString("id"), fieldsNames[i]);
                if (rsDesign.size() == 1) {
                    Values _rowTritaoDesign = rsDesign.get(0);
                    Configuration config = new Configuration();
                    config.load(_rowTritaoDesign.getString("properties"));
                    boolean hasLink = false;
                    String tableSeparator = separator;
                    for (String configKey : config.getParameters().keySet()) {
                    	if (configKey.equalsIgnoreCase("COLUMN_SEPARATOR")) {
                    		tableSeparator = config.getParameter(configKey).getValue();
                    	}
                    }
                    for (String configKey : config.getParameters().keySet()) {
                    	if (!hasLink && config.getParameter(configKey).getType() == ParameterType.LINK) {
                    		hasLink = true;
                    		String subLink = config.getParameter(configKey).getValue();
                    		String subLinkTableName = subLink.substring(0, subLink.indexOf(":"));
                    		String subLinkTableNameAlias = subLinkTableName +"_"+ deep +"_"+ (level + 1);
                            from.insert(0, "(");
                            from.append(" left join ");
                            from.append(builder.escape(subLinkTableName));
                            from.append(" as ".concat(builder.escape(subLinkTableNameAlias)).concat(" on ").concat(fieldName).concat(" = ").concat(builder.escape(subLinkTableNameAlias)).concat(".").concat(builder.escape("id")).concat(")"));
                    		queryBuilder(subLink, filter, tableSeparator, deep, level + 1, select, from, where, order, whereFilterColumns);
                            if (level < deep) {
                            	return;
                            }
                    	}
                    }
                    if (!hasLink) {
                    	if (level >= deep) {
                    		fields.add(tableName.concat("$").concat(fieldsNames[i]));
                    	}
                        select.append(", ").append(fieldName).append(" as ").append(builder.escape(tableName.concat("$").concat(fieldsNames[i])));
                        Component com = Config.getNewComponent(getProteu(), getHili(), _rowTritaoDesign.getString("type"));
                        com.setProteu(getProteu());
                        com.setDesignData(_rowTritaoDesign);
                    	for (ComponentData comData : com.getDataStructure()) {
                    		if (comData.getName().equals(fieldsNames[i]) 
                    				&& filter.length() > 0) {
                    			if (comData.getType() == Type.Varchar
                    					|| comData.getType() == Type.Date
                    					|| comData.getType() == Type.DateTime
                    			) {
                    				whereFilterColumns.add(fieldName);
                    			}
                    		}
                    	}
                    	if (order.length() > 0) {
                            order.append(", ");
                    	}
                        order.append(fieldName);
                    }
                }
            }
        }
        if (level < deep) {
        	throw new Error("Link deep ".concat(Integer.toString(deep)).concat(" not reached."));
        }
    }

    public static String getDataShow(Proteu proteu, Values rowData, Link link, String separator) {
    	String label = "";
    	for (int i = 0; i < link.getFields().size(); i++) {
        	String field = link.getFields().get(i);
            String fieldTable = field.substring(0, field.indexOf('$'));
            if (label.length() > 0) {
            	String finalSeparator = separator;
	            for (int z = 0; z < link.getTables().size(); z++) {
	            	if (link.getTables().get(z).equals(fieldTable)) {
		            	String tableSeparator = link.getTablesSeparators().get(z);
		            	if (tableSeparator != null) {
		            		finalSeparator = tableSeparator;
		            	}
	            	}
	            }
        		label = label.concat(finalSeparator);
            }
        	String fieldActive = fieldTable.concat("$active");
        	boolean isActive = true;
        	if (!rowData.getBoolean(fieldActive)) {
        		isActive = false;
            }
        	if (!isActive) {
        		label = label.concat("<s>");
        	}
            label = label.concat(org.apache.commons.lang3.StringEscapeUtils.escapeHtml4(rowData.getString(field)));
        	if (!isActive) {
        		label = label.concat("</s>");
        	}
        }
    	return label;
    }
    
    public static String getDataShow(Proteu proteu, Hili hili, String key, String ids, String link, String separator, int maxLengthPerField, boolean allowHtml) {
    	List<LinkDataShow> datas = getDataShowList(proteu, hili, key, ids, link, separator, maxLengthPerField, allowHtml);
    	String contents = "";
    	for (LinkDataShow data : datas) {
    		if (!contents.isEmpty()) {
    			contents = contents.concat(separator);
    		}
    		contents = contents.concat(data.getContent());
        }
    	return contents;
    }
    
    public static List<LinkDataShow> getDataShowList(Proteu proteu, Hili hili, String key, String ids, String link, String separator, int maxLengthPerField, boolean allowHtml) {
        Builder builder = Config.getDataBaseBuilder(proteu, key);
        String _tableName = getTableName(link);
        String[] fieldNames = null;
        List<LinkDataShow> datas = new ArrayList<LinkDataShow>();
        if (link.indexOf(",") == -1) {
        	fieldNames = new String[1];
        	fieldNames[0] = link.substring(link.indexOf(":") + 1);
        } else {
        	fieldNames = link.substring(link.indexOf(":") + 1).split("\\,");
        }
        Values _rowTritaoTableByName = builder.selectTableByName(_tableName);
        if (_rowTritaoTableByName != null) {
            List<Values> rsTableRow = builder.selectTableRows(_tableName, ids);
            for (Values rowTableRow : rsTableRow) {
            	String content = "";
	            for (String fieldName : fieldNames) {
	                List<Values> rsDesign = builder.selectTableDesign(_rowTritaoTableByName.getString("id"), fieldName);
	                if (rsDesign.size() == 1) {
	                    Values rowDesign = rsDesign.get(0);
                        String subLink = "";
                        String pathLink = "";
                        String nodeLink = "";
                        Configuration config = new Configuration();
                        config.load(rowDesign.getString("properties"));
                        boolean hasLink = false;
                        String linkSeparator = separator;
                        boolean hasPath = false;
                        String pathSeparator = separator;
                        boolean hasNode = false;
                        String nodeSeparator = separator;
                        String contentItem = "";
                        for (String configKey : config.getParameters().keySet()) {
                        	if (!hasLink && config.getParameter(configKey).getType() == ParameterType.LINK) {
                        		hasLink = true;
                        		subLink = config.getParameter(configKey).getValue();
                        	}
                        	if (!hasPath && config.getParameter(configKey).getType() == ParameterType.PATH_PARENT_ID) {
                        		hasPath = true;
                        		pathLink = config.getParameter(configKey).getValue();
                        	}
                        	if (!hasNode && config.getParameter(configKey).getType() == ParameterType.PATH_NODE_DISPLAY) {
                        		hasNode = true;
                        		nodeLink = config.getParameter(configKey).getValue();
                        	}
                        	if (config.getParameter(configKey).getType() == ParameterType.LINK_SEPARATOR || configKey.equalsIgnoreCase("COLUMN_SEPARATOR")) {
                        		linkSeparator = config.getParameter(configKey).getValue();
                        	}
                        	if (config.getParameter(configKey).getType() == ParameterType.PATH_SEPARATOR) {
                        		pathSeparator = config.getParameter(configKey).getValue();
                        	}
                        	if (config.getParameter(configKey).getType() == ParameterType.PATH_NODE_SEPARATOR) {
                        		nodeSeparator = config.getParameter(configKey).getValue();
                        	}
                        }
                        if (hasLink) {
                            if (!rowTableRow.getString(fieldName).equals("") && !rowTableRow.getString(fieldName).equals("0")) {
                            	contentItem = contentItem.concat(getDataShow(proteu, hili, key, rowTableRow.getString(fieldName), subLink, linkSeparator, maxLengthPerField, allowHtml));
                            }
                        } else if (hasPath && hasNode) {
                            if (!rowTableRow.getString(fieldName).equals("") && !rowTableRow.getString(fieldName).equals("0")) {
                            	List<PathDataShow> paths = Path.getDataShowList(proteu, hili, key, rowTableRow.getString(fieldName), pathLink, pathSeparator, nodeLink, nodeSeparator, maxLengthPerField, allowHtml);
                            	contentItem = paths.size() > 0 ? paths.get(0).getContent() : "";
                            }
                        } else {
                        	if (allowHtml && !rowTableRow.getBoolean("active")) {
                        		contentItem = contentItem.concat("<s>");
                            }
                        	String value = rowTableRow.getString(fieldName);
                        	if (maxLengthPerField > 0 && value.length() > maxLengthPerField) {
                        		value = value.substring(0, maxLengthPerField).concat("...");
                        	}
                        	if (allowHtml) {
                        		contentItem = contentItem.concat(org.apache.commons.lang3.StringEscapeUtils.escapeHtml4(value));
                        	} else {
                        		contentItem = contentItem.concat(value);
                        	}
                            if (allowHtml && !rowTableRow.getBoolean("active")) {
                            	contentItem = contentItem.concat("</s>");
                            }
                        }
                        if (!content.isEmpty() && !contentItem.isEmpty()) {
                        	content = content.concat(separator);
                        }
                        content = content.concat(contentItem);
	                }
	            }
                datas.add(new LinkDataShow(rowTableRow.getString("id"), rowTableRow.getString("uid"), content));
            }
        }
        return datas;
    }

    public static String getTableName(String link) {
        if (link.isEmpty()) {
            return "";
        }
        return link.substring(0, link.indexOf(":"));
    }

}
