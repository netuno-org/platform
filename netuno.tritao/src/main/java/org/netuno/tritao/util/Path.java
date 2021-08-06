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

import java.util.ArrayList;
import java.util.List;

import org.netuno.proteu.Proteu;
import org.netuno.psamata.Values;
import org.netuno.tritao.config.Config;
import org.netuno.tritao.config.Hili;

/**
 * Path - Deep Recursive Link - Forms Relations
 * @author Eduardo Fonseca Velasques - @eduveks
 */
public class Path {
    private static String queryBuilder(Proteu proteu, Hili hili, String key, Link linkPath, String ids) {
        String parentField = linkPath.getRootFieldNames().get(0);
        String query = "WITH RECURSIVE LINK(ID, IDS, LEVEL, ACTIVE) AS (";
        query = query.concat("  SELECT ID, ").concat(Config.getDataBaseBuilder(proteu, key).concatenation("ID", "''")).concat(", 0, ACTIVE FROM ").concat(linkPath.getTableName()).concat(" WHERE ").concat(parentField).concat(" IS NULL OR ").concat(parentField).concat(" = 0");
        query = query.concat("    UNION ALL");
        query = query.concat("      SELECT ").concat(linkPath.getTableName()).concat(".ID, ").concat(Config.getDataBaseBuilder(proteu, key).concatenation(Config.getDataBaseBuilder(proteu, key).coalesce(Config.getDataBaseBuilder(proteu, key).concatenation("LINK.IDS", "','"), "''"), linkPath.getTableName().concat(".ID")));
        query = query.concat("        , LEVEL + 1");
        query = query.concat("	      , ").concat(linkPath.getTableName()).concat(".ACTIVE");
        query = query.concat("	    FROM LINK INNER JOIN ").concat(linkPath.getTableName()).concat(" ON LINK.ID = ").concat(linkPath.getTableName()).concat(".").concat(parentField);
        query = query.concat(") ");
        query = query.concat(" SELECT ID, IDS, LEVEL, ACTIVE FROM LINK WHERE IDS IS NOT NULL ").concat(ids.isEmpty() ? "" : "AND ".concat("ID IN (").concat(ids).concat(")")).concat(" ORDER BY LEVEL, ID;");
        return query;
    }

    public static List<PathDataShow> getDataShowList(Proteu proteu, Hili hili, String key, String ids, String linkPath, String separatorPath, String linkNode, String separatorNode, int maxLengthPerField, boolean allowHtml) {
        List<PathDataShow> pathDataShowNodes = new ArrayList<>();
        List<Values> pathRows = Config.getDataBaseManager(proteu, key).query(queryBuilder(proteu, hili, key, new Link(proteu, hili, key, linkPath), ids));
        for (Values pathRow : pathRows) {
            String[] nodesIds = pathRow.getString("ids").split("\\,");
            String content = "";
            Values dataNode = new Values();
            if (allowHtml && pathRow.getString("active").length() == 0 || pathRow.getString("active").equals("false") || pathRow.getString("active").equals("0")) {
                content = content.concat("<s>");
            }
            for (String id : nodesIds) {
                if (!content.isEmpty()) {
                    content = content.concat(separatorPath);
                }
                dataNode.set("id", id);
                content = content.concat(Link.getDataShow(proteu, hili, key, id, linkNode, separatorNode, maxLengthPerField, allowHtml));
            }
            if (allowHtml && pathRow.getString("active").length() == 0 || pathRow.getString("active").equals("false") || pathRow.getString("active").equals("0")) {
                content = content.concat("</s>");
            }
            pathDataShowNodes.add(new PathDataShow(pathRow.getString("id"), pathRow.getString("ids"), nodesIds, content, pathRow.getString("active")));
        }
        return pathDataShowNodes;
    }
}