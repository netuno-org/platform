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

package org.netuno.tritao.db;

import java.util.List;
import org.netuno.psamata.Values;

/**
 * Data Selected loaded with the result of a query search.
 * @author Eduardo Fonseca Velasques - @eduveks
 */
public class DataSelected {
    public static final String SELECT_SEARCH_ID_QUERY_MARK = "<!--NETUNO_TRITAO_TABLE_ID-->";
    private List<Values> results = null;
    private String tableName = null;
    private String queryId = "";
    private int total = -1;
    private int offset = -1;
    private int length = -1;
    private int fullTotal = -1;

    public DataSelected() {
        
    }

    public DataSelected(List<Values> results, String tableName, String queryId, int total, int offset, int length, int fullTotal) {
        this.results = results;
        this.tableName = tableName;
        this.queryId = queryId;
        this.total = total;
        this.offset = offset;
        this.length = length;
        this.fullTotal = fullTotal;
    }

    public int getOffset() {
        return offset;
    }

    public DataSelected setOffset(int offset) {
        this.offset = offset;
        return this;
    }

    public int getLength() {
        return length;
    }

    public void setLength(int length) {
        this.length = length;
    }

    public String getQueryId() {
        return queryId;
    }

    public void setQueryId(String queryId) {
        this.queryId = queryId;
    }

    public List<Values> getResults() {
        return results;
    }

    public void setResults(List<Values> results) {
        this.results = results;
    }

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

	public int getFullTotal() {
		return fullTotal;
	}

	public void setFullTotal(int fullTotal) {
		this.fullTotal = fullTotal;
	}
}
