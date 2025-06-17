package org.netuno.tritao.db.builder;

import org.netuno.psamata.Values;

import java.util.ArrayList;
import java.util.List;

public interface TablePrimaryKeys extends BuilderBase, TableSelect, TableDesignSelect {
    default List<String> primaryKeys(String tableName) {
        Values table = selectTableByName(tableName);
        List<Values> rsTritaoDesignXY = selectTableDesignXY(table.getString("id"));
        List<String> primaryKeys = new ArrayList<>();
        for (Values rowTritaoDesignXY : rsTritaoDesignXY) {
            if (rowTritaoDesignXY.getBoolean("primarykey")) {
                primaryKeys.add(rowTritaoDesignXY.getString("name"));
            }
        }
        return primaryKeys;
    }
}
