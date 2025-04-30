package org.netuno.tritao.db.builder;

import org.netuno.psamata.DB;
import org.netuno.psamata.Values;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public interface TableDesignSelect extends BuilderBase {
    default List<Values> selectDesign(Values data) {
        String select = " * ";
        String from = " netuno_design ";
        String where = "where 1 = 1 ";
        if (data.hasKey("id") && data.getInt("id") > 0) {
            where += " and id = " + data.getInt("id");
        }
        if (data.hasKey("table_id") && data.getInt("table_id") > 0) {
            where += " and table_id = " + data.getInt("table_id");
        }
        if (data.hasKey("name") && !data.getString("name").isEmpty()) {
            where += " and name = '" + DB.sqlInjection(data.getString("name")) + "'";
        }
        if (data.hasKey("uid") && !data.getString("uid").isEmpty()) {
            where += " and uid = '" + DB.sqlInjection(data.getString("uid")) + "'";
        }
        String order = " order by name ";
        String sql = "select " + select + " from " + from + where + order;
        return getExecutor().query(sql);
    }

    default List<Values> selectTableDesign() {
        return selectTableDesign("", "", "");
    }

    default List<Values> selectTableDesign(String id) {
        return selectTableDesign(id, "", "");
    }

    default List<Values> selectTableDesign(String table_id, String name) {
        return selectTableDesign("", table_id, name);
    }

    default List<Values> selectTableDesign(String id, String table_id, String name) {
        return selectTableDesign(id, table_id, name, "");
    }

    default List<Values> selectTableDesign(String id, String table_id, String name, String uid) {
        String select = " * ";
        String from = " netuno_design ";
        String where = "where 1 = 1 ";
        if (!id.equals("") && !id.equals("0")) {
            where += " and id = " + DB.sqlInjectionInt(id);
        }
        if (!table_id.equals("") && !table_id.equals("0")) {
            where += " and table_id = " + DB.sqlInjectionInt(table_id);
        }
        if (!name.equals("")) {
            where += " and name = '" + DB.sqlInjection(name) + "'";
        }
        if (!uid.equals("")) {
            where += " and uid = '" + DB.sqlInjection(uid) + "'";
        }
        String order = " order by name ";
        String sql = "select " + select + " from " + from + where + order;
        return getExecutor().query(sql);
    }

    default List<Values> selectTableDesign(Values data) {
        String select = " * ";
        String from = " netuno_design ";
        String where = "where 1 = 1 ";
        if (data.hasKey("id") && data.getInt("id") > 0) {
            where += " and id = " + DB.sqlInjectionInt(data.getString("id"));
        }
        if (data.hasKey("table_id") && data.getInt("table_id") > 0) {
            where += " and table_id = " + DB.sqlInjectionInt(data.getString("table_id"));
        }
        if (data.hasKey("name") && !data.getString("name").isEmpty()) {
            where += " and name = '" + DB.sqlInjection(data.getString("name")) + "'";
        }
        if (data.hasKey("uid") && !data.getString("uid").isEmpty()) {
            where += " and uid = '" + DB.sqlInjection(data.getString("uid")) + "'";
        }
        String order = " order by name ";
        String sql = "select " + select + " from " + from + where + order;
        return getExecutor().query(sql);
    }

    default List<Values> selectTableDesignXY(String table_id) {
        String select = " * ";
        String from = " netuno_design ";
        String where = "where 1 = 1 ";
        if (!table_id.equals("") && !table_id.equals("0")) {
            where += " and table_id = " + DB.sqlInjectionInt(table_id);
        }
        String order = " order by y, x ";
        String sql = "select " + select + " from " + from + where + order;
        List<Values> rsDesignXY = getExecutor().query(sql);
        List<List<Map<String, Integer>>> allWidths = new ArrayList<>();
        int y = 0;
        List<Map<String, Integer>> rowWidths = new ArrayList<>();
        for (int j = 0; j < rsDesignXY.size(); j++) {
            Values rowTritaoDesignXY = rsDesignXY.get(j);
            if (rowTritaoDesignXY.getInt("y") != y) {
                if (rowWidths.size() > 0) {
                    allWidths.add(rowWidths);
                    rowWidths = new ArrayList<>();
                }
                y = rowTritaoDesignXY.getInt("y");
            }
            int width = rowTritaoDesignXY.getInt("tdwidth");
            if (width <= 0) {
                width = 0;
            }
            Map<String, Integer> cellInfo = new HashMap<>();
            cellInfo.put("id", rowTritaoDesignXY.getInt("id"));
            cellInfo.put("width", width);
            rowWidths.add(cellInfo);
        }
        if (rowWidths.size() > 0) {
            allWidths.add(rowWidths);
        }
        for (List<Map<String, Integer>> row : allWidths) {
            int restWidth = 100;
            int colsWithZero = 0;
            for (Map<String, Integer> cellInfo : row) {
                if (cellInfo.get("width") > 0) {
                    restWidth -= cellInfo.get("width");
                } else {
                    colsWithZero++;
                }
            }
            if (restWidth > 0 && colsWithZero > 0) {
                int colsWithZeroWidth = restWidth / colsWithZero;
                for (Map<String, Integer> cellInfo : row) {
                    if (cellInfo.get("width") <= 0) {
                        cellInfo.put("width", colsWithZeroWidth);
                    }
                }
            }
        }
        for (Values rowTritaoDesignXY : rsDesignXY) {
            for (List<Map<String, Integer>> row : allWidths) {
                for (Map<String, Integer> cellInfo : row) {
                    if (rowTritaoDesignXY.getInt("id") == cellInfo.get("id")) {
                        rowTritaoDesignXY.set("colwidth12", Math.round((cellInfo.get("width") * 12.0f) / 100.0f));
                    }
                }
            }
        }
        return rsDesignXY;
    }

    default List<Values> selectTableDesignMaxX(String table_id) {
        String select = " max(x) as MaxX ";
        String from = " netuno_design ";
        String where = "where 1 = 1 ";
        if (!table_id.equals("") && !table_id.equals("0")) {
            where += " and table_id = " + DB.sqlInjectionInt(table_id);
        }
        String order = " ";
        String sql = "select " + select + " from " + from + where + order;
        return getExecutor().query(sql);
    }
}
