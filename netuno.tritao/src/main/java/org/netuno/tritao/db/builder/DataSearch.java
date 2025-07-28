package org.netuno.tritao.db.builder;

import org.netuno.psamata.DB;
import org.netuno.psamata.Values;
import org.netuno.tritao.auth.Auth;
import org.netuno.tritao.com.ComponentData;
import org.netuno.tritao.config.Config;
import org.netuno.tritao.db.DataSelected;
import org.netuno.tritao.util.Rule;

import java.util.ArrayList;
import java.util.List;

public interface DataSearch extends BuilderBase, TableSelect, TableDesignSelect {
    default String selectSearchId(String query, String id) {
        return query.replace(DataSelected.SELECT_SEARCH_ID_QUERY_MARK, DB.sqlInjectionInt("" + id));
    }

    default DataSelected selectSearch() {
        return selectSearch("", getProteu().getRequestAll(), getProteu().getRequestAll().getInt("netuno_page"), 10, "", true);
    }

    default DataSelected selectSearch(int offset, int length, String orderBy) {
        return selectSearch("", getProteu().getRequestAll(), offset, length, orderBy, true);
    }

    default DataSelected selectSearch(String tableName, Values data) {
        return selectSearch(tableName, data, 0, 1000, "", true);
    }

    default DataSelected selectSearch(String tableName, Values data, boolean wildcards) {
        return selectSearch(tableName, data, 0, 1000, "", wildcards);
    }

    default DataSelected selectSearch(String tableName, Values data, String orderBy) {
        return selectSearch(tableName, data, 0, 1000, orderBy, true);
    }

    default DataSelected selectSearch(String tableName, Values data, String orderBy, boolean wildcards) {
        return selectSearch(tableName, data, 0, 1000, orderBy, wildcards);
    }

    default DataSelected selectSearch(String tableName, Values data, int length, String orderBy) {
        return selectSearch(tableName, data, 0, length, orderBy, true);
    }

    default DataSelected selectSearch(String tableName, Values data, int length, String orderBy, boolean wildcards) {
        return selectSearch(tableName, data, 0, length, orderBy, wildcards);
    }

    default DataSelected selectSearch(String tableName, Values data, int offset, int length, String orderBy) {
        return selectSearch(tableName, data, offset, length, orderBy, true);
    }

    default DataSelected selectSearch(String tableName, Values data, int offset, int length, String orderBy, boolean wildcards) {
        List<Values> rsTritaoTable = selectTable(
                data.hasKey("netuno_table_id") ? data.getString("netuno_table_id") : "",
                tableName, ""
        );
        if (rsTritaoTable.size() == 0) {
            return null;
        }
        Values rowTritaoTable = rsTritaoTable.get(0);

        tableName = rowTritaoTable.getString("name");
        boolean controlActive = rowTritaoTable.getBoolean("control_active");

        List<Values> rsTritaoDesignXY = selectTableDesignXY(rowTritaoTable.getString("id"));
        String order = "";
        String fields = getBuilder().escape(tableName).concat(".lastchange_time as ").concat(tableName)
                .concat("_lastchange_time, ").concat(getBuilder().escape(tableName)).concat(".lastchange_user_id as ")
                .concat(tableName).concat("_lastchange_user_id, ")
                .concat("netuno_user." + getBuilder().escape("user") + " as ").concat(tableName)
                .concat("_lastchange_user");
        String tables = getBuilder().escape(tableName);
        tables = tables.concat(" left join netuno_user on ").concat(getBuilder().escape(tableName))
                .concat(".lastchange_user_id = netuno_user.id");
        String filters = "";
        List<String> designFieldsNames = new ArrayList();
        for (int _x = 0; _x < rsTritaoDesignXY.size(); _x++) {
            Values rowTritaoDesignXY = rsTritaoDesignXY.get(_x);
            if (!Rule.hasDesignFieldViewAccess(getProteu(), getHili(), rowTritaoDesignXY)) {
                continue;
            }
            designFieldsNames.add(rowTritaoDesignXY.getString("name"));
            org.netuno.tritao.com.Component com = Config.getNewComponent(getProteu(), getHili(),
                    rowTritaoDesignXY.getString("type"));
            com.setProteu(getProteu());
            com.setDesignData(rowTritaoDesignXY);
            com.setTableData(rowTritaoTable);
            com.setValues(data);
            for (ComponentData componentData : com.getDataStructure()) {
                String sqlTableColumnFullName = getBuilder().escape(tableName).concat(".")
                        .concat(getBuilder().escape(componentData.getName()));
                String sqlTableColumnFullNameOrderBy = "[" + tableName + "].[" + componentData.getName() + "]";
                fields = fields.concat(", ").concat(sqlTableColumnFullName);
                fields = fields.concat(" as ").concat(tableName).concat("_").concat(componentData.getName());
                if (rowTritaoDesignXY.getBoolean("whenfilter") || rowTritaoDesignXY.getBoolean("whenresult")) {
                    switch (componentData.getFilter()) {
                        case Default:
                            String value = getDataValue(componentData);
                            if (componentData.getType() == ComponentData.Type.Varchar) {
                                if (rowTritaoDesignXY.getBoolean("whenfilter")) {
                                    if (!value.equalsIgnoreCase("''")) {
                                        filters = filters.concat(" and ")
                                                .concat(getBuilder().searchComparison(sqlTableColumnFullName))
                                                .concat("");
                                        filters = filters.concat(" like ").concat(getBuilder()
                                                        .searchComparison(wildcards ? concatenation(concatenation("'%'", value), "'%'") : value))
                                                .concat("");
                                    }
                                }
                                if (rowTritaoDesignXY.getBoolean("whenresult")) {
                                    orderBy = orderBy.replace(sqlTableColumnFullNameOrderBy,
                                            getBuilder().unaccent(sqlTableColumnFullNameOrderBy));
                                }
                            } else if (componentData.getType() == ComponentData.Type.Uid) {
                                if (rowTritaoDesignXY.getBoolean("whenfilter")) {
                                    if (!value.equalsIgnoreCase("''")) {
                                        filters = filters.concat(" and lower(")
                                                .concat(concatenation(sqlTableColumnFullName, "''")).concat(")");
                                        filters = filters.concat(" like ").concat(getBuilder()
                                                        .searchComparison(wildcards ? concatenation(concatenation("'%'", value), "'%'") : value))
                                                .concat("");
                                    }
                                }
                            } else {
                                if (rowTritaoDesignXY.getBoolean("whenfilter")) {
                                    if (componentData.getType() != ComponentData.Type.Text
                                            && !value.equalsIgnoreCase("null") && !value.equalsIgnoreCase("''")
                                            && !value.isEmpty()
                                            && ((componentData.getType() == ComponentData.Type.Boolean)
                                            || (componentData.getType() != ComponentData.Type.Boolean
                                            && !value.equalsIgnoreCase("0")))) {
                                        filters = filters.concat(" and ").concat(sqlTableColumnFullName);
                                        filters = filters.concat(" = ").concat(value);
                                    }
                                }
                            }
                            break;
                        case Between:
                            String valueFrom = getDataValue(componentData, componentData.getValueFrom());
                            String valueUntil = getDataValue(componentData, componentData.getValueUntil());
                            if (componentData.getType() == ComponentData.Type.Date
                                    || componentData.getType() == ComponentData.Type.DateTime) {
                                if (rowTritaoDesignXY.getBoolean("whenfilter")) {
                                    if (!valueFrom.equalsIgnoreCase("''") && !valueFrom.equalsIgnoreCase("null")) {
                                        filters = filters.concat(" and ").concat(sqlTableColumnFullName);
                                        filters = filters.concat(" >= ").concat(valueFrom);
                                    }
                                    if (!valueUntil.equalsIgnoreCase("''") && !valueUntil.equalsIgnoreCase("null")) {
                                        filters = filters.concat(" and ").concat(sqlTableColumnFullName);
                                        filters = filters.concat(" <= ").concat(valueUntil);
                                    }
                                }
                                if (rowTritaoDesignXY.getBoolean("whenresult")) {
                                    orderBy = orderBy.replace(sqlTableColumnFullNameOrderBy,
                                            getBuilder().unaccent(sqlTableColumnFullNameOrderBy));
                                }
                            }
                            if (componentData.getType() == ComponentData.Type.Integer
                                    || componentData.getType() == ComponentData.Type.BigInteger
                                    || componentData.getType() == ComponentData.Type.Float
                                    || componentData.getType() == ComponentData.Type.Decimal) {
                                if (rowTritaoDesignXY.getBoolean("whenfilter")) {
                                    if (!valueFrom.equalsIgnoreCase("0")) {
                                        filters = filters.concat(" and ").concat(sqlTableColumnFullName);
                                        filters = filters.concat(" >= ").concat(valueFrom);
                                    }
                                    if (!valueUntil.equalsIgnoreCase("0")) {
                                        filters = filters.concat(" and ").concat(sqlTableColumnFullName);
                                        filters = filters.concat(" <= ").concat(valueUntil);
                                    }
                                }
                                if (rowTritaoDesignXY.getBoolean("whenresult")) {
                                    orderBy = orderBy.replace(sqlTableColumnFullNameOrderBy,
                                            getBuilder().unaccent(sqlTableColumnFullNameOrderBy));
                                }
                            }
                            break;
                        default:
                            break;
                    }
                }
            }
        }
        for (String defaltFieldName : new String [] { "id", "uid", "lock", "active", "user_id", "group_id" }) {
            if (!designFieldsNames.contains(defaltFieldName)) {
                fields = getBuilder().escape(tableName)
                        .concat("."+ defaltFieldName +" as ")
                        .concat(tableName)
                        .concat("_"+ defaltFieldName +", ")
                        .concat(fields);
            }
        }
        Rule rule = Rule.getRule(getProteu(), getHili(), data.getString("netuno_table_id"));
        if (rule.getRead() == Rule.OWN) {
            filters += " and " + getBuilder().escape(tableName) + ".user_id="
                    + Auth.getUser(getProteu(), getHili()).getString("id");
        }
        if (rule.getRead() == Rule.GROUP) {
            filters += " and " + getBuilder().escape(tableName) + ".group_id="
                    + Auth.getGroup(getProteu(), getHili()).getString("id");
        }
        if (rowTritaoTable.getBoolean("show_id") && data.hasKey("id") && data.getInt("id") > 0) {
            filters += " and ".concat(getBuilder().escape(tableName)).concat(".id = ")
                    .concat(DB.sqlInjectionInt(data.getString("id")));
        }
        if (controlActive) {
            if (data.hasKey("active")) {
                filters += " and ".concat(getBuilder().escape(tableName))
                        .concat(".active = ".concat(getBuilder().booleanValue(data.getBoolean("active"))));
            }
        }
        if (!filters.isEmpty()) {
            filters = " where 1 = 1" + filters;
        }
        if (offset < 0) {
            offset = 0;
        }
        if (!orderBy.equals("")) {
            order += "," + orderBy.replace("[", getBuilder().escapeStart()).replace("]", getBuilder().escapeEnd());
        }
        if (!order.equals("")) {
            order = " order by " + order.substring(1);
        }
        String search = "select ".concat(fields).concat(" from ").concat(tables).concat(filters).concat(order);
        if (isMSSQL()) {
            if (order.isEmpty() && (offset > 0 || length > 0)) {
                search += " order by 1";
            }
            if (offset > 0) {
                search = search.concat(" offset ").concat(Integer.toString(offset)).concat(" rows");
            } else if (length > 0) {
                search = search.concat(" offset 0 rows");
            }
            if (length > 0) {
                search = search.concat(" fetch next ").concat(Integer.toString(length)).concat(" rows only");
            }
        } else {
            if (length > 0) {
                search = search.concat(" limit ").concat(Integer.toString(length));
            }
            if (offset > 0) {
                search = search.concat(" offset ").concat(Integer.toString(offset));
            }
        }
        String queryID = "select ".concat(fields).concat(" from ").concat(tables).concat(" where ")
                .concat(getBuilder().escape(tableName)).concat(".id = ")
                .concat(DataSelected.SELECT_SEARCH_ID_QUERY_MARK);
        String count = "select count(".concat(getBuilder().escape(tableName)).concat(".id) as TOTAL from ")
                .concat(tables).concat(filters);
        String fullCount = "select count(".concat(getBuilder().escape(tableName)).concat(".id) as TOTAL from ")
                .concat(getBuilder().escape(tableName)).concat("");
        DataSelected dataSelected = new DataSelected();
        dataSelected.setResults(getExecutor().query(search));
        dataSelected.setTableName(tableName);
        dataSelected.setQueryId(queryID);
        dataSelected.setTotal(getExecutor().query(count).get(0).getInt("TOTAL"));
        dataSelected.setOffset(offset);
        dataSelected.setLength(length);
        dataSelected.setFullTotal(getExecutor().query(fullCount).get(0).getInt("TOTAL"));
        return dataSelected;
    }


}
