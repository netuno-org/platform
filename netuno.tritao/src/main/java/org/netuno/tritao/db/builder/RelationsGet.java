package org.netuno.tritao.db.builder;

import org.netuno.psamata.Values;
import org.netuno.tritao.com.ComponentData;
import org.netuno.tritao.com.ParameterType;
import org.netuno.tritao.config.Config;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public interface RelationsGet extends BuilderBase, TableSelect, TableDesignSelect {
    default List<Values> getRelations(Values rowTable, List<Values> xrsTritaoDesignXY) {
        String tableName = rowTable.getString("name");
        List<Values> relations = new ArrayList<>();
        for (Values rowTritaoDesignXY : selectTableDesignXY("")) {
            org.netuno.tritao.com.Component com = Config.getNewComponent(getProteu(), getHili(),
                    rowTritaoDesignXY.getString("type"));
            com.setProteu(getProteu());
            com.setDesignData(rowTritaoDesignXY);
            com.setTableData(rowTable);
            com.setValues(getProteu().getRequestAll());
            for (String paramKey : com.getConfiguration().getParameters().keySet()) {
                if (com.getConfiguration().getParameter(paramKey).getType() == ParameterType.LINK
                        && com.getConfiguration().getParameter(paramKey).getValue().startsWith(tableName.concat(":"))) {
                    List<Values> linkedTables = selectTable(rowTritaoDesignXY.getString("table_id"), "", "");
                    if (linkedTables.size() == 0) {
                        continue;
                    }
                    Values rowLinkedTable = linkedTables.get(0);
                    if (rowLinkedTable.getBoolean("report")) {
                        continue;
                    }
                    List<ComponentData> relationComponents = new ArrayList<>();
                    for (ComponentData data : com.getDataStructure()) {
                        if (data.getType() == ComponentData.Type.Integer) {
                            relationComponents.add(data);
                        }
                    }
                    rowLinkedTable.set("relation_components", relationComponents);
                    relations.add(rowLinkedTable);
                }
            }
        }
        relations.sort(Comparator.comparing(
                table -> table.getString("title")
        ));
        relations.sort(Comparator.comparing(
                table -> table.getInt("reorder")
        ));
        return relations;
    }
}
