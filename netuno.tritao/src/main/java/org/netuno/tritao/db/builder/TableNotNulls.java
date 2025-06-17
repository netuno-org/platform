package org.netuno.tritao.db.builder;

import org.netuno.psamata.Values;

import java.util.ArrayList;
import java.util.List;

public interface TableNotNulls extends BuilderBase, TableSelect, TableDesignSelect {
    default List<String> notNulls(String tableName) {
        Values table = selectTableByName(tableName);
        List<Values> rsTritaoDesignXY = selectTableDesignXY(table.getString("id"));
        List<String> notNulls = new ArrayList<>();
        for (Values rowTritaoDesignXY : rsTritaoDesignXY) {
            if (rowTritaoDesignXY.getBoolean("notnull")) {
                notNulls.add(rowTritaoDesignXY.getString("name"));
            }
        }
        return notNulls;
    }
}
