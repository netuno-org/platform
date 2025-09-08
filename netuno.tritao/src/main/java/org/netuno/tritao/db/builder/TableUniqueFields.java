package org.netuno.tritao.db.builder;

import org.netuno.psamata.Values;

import java.util.ArrayList;
import java.util.List;

public interface TableUniqueFields extends BuilderBase, TableSelect, TableDesignSelect {
    default List<String> uniqueFields(String tableName) {
        Values table = selectTableByName(tableName);
        List<Values> rsTritaoDesignXY = selectTableDesignXY(table.getString("id"));
        List<String> uniques = new ArrayList<>();
        for (Values rowTritaoDesignXY : rsTritaoDesignXY) {
            if (rowTritaoDesignXY.getBoolean("unique")) {
                uniques.add(rowTritaoDesignXY.getString("name"));
            }
        }
        return uniques;
    }
}
