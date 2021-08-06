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

package org.netuno.tritao.db.manager;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.netuno.library.doc.LanguageDoc;
import org.netuno.library.doc.LibraryDoc;
import org.netuno.library.doc.LibraryTranslationDoc;
import org.netuno.library.doc.SourceCodeDoc;
import org.netuno.library.doc.SourceCodeTypeDoc;
import org.netuno.proteu.Proteu;
import org.netuno.psamata.DB;
import org.netuno.tritao.config.Hili;
import org.netuno.tritao.db.Builder;
import org.netuno.tritao.db.Manager;

/**
 * Check Exists Operations
 * @author Eduardo Fonseca Velasques - @eduveks
 */
@LibraryDoc(translations = {
        @LibraryTranslationDoc(
                language=LanguageDoc.PT,
                title = "CheckExists",
                introduction = "Verifica se sequências, tabelas, colunas e indexes existem na base de dados.",
                howToUse = {
                        @SourceCodeDoc(
                                type = SourceCodeTypeDoc.JavaScript,
                                code = "if (!_db.checkExists().table(\"client\")) {\n"
                                + "    _db.table().create(\n"
                                + "        \"client\",\n"
                                + "        _db.column().setName(\"id\").setType(\"int\").setPrimaryKey(true),\n"
                                + "        _db.column().setName(\"name\").setType(\"varchar\").setNotNull(true).setDefault()\n"
                                + "    );\n"
                                + "}"
                        )
                }
        )
})
public class CheckExists extends Base {
    private static Logger logger = LogManager.getLogger(CheckExists.class);

    public CheckExists(Base base) {
        super(base);
    }

    public CheckExists(Proteu proteu, Hili hili, String key) {
        super(proteu, hili, key);
    }
    
    public boolean sequence(String tableName, String columnName) {
    	return index(tableName +"_"+ columnName +"_idx");
    }

    public boolean sequence(String name) {
        if (isH2()) {
            return getManager().query("select sequence_name from INFORMATION_SCHEMA.sequences where sequence_name = '"+ DB.sqlInjection(name) +"'").size() > 0;
        } else if (isPostgreSQL()) {
            return getManager().query("select * from pg_class where relname = '" + DB.sqlInjection(name) + "' and relkind = 'S'").size() == 1;
        }
        return false;
    }

    public boolean table(String table) {
        if (isH2()) {
            return getManager().query("select column_name from INFORMATION_SCHEMA.columns where table_name = '"+ DB.sqlInjection(table) +"'").size() > 0;
        } else if (isPostgreSQL()) {
            return getManager().query("select * from pg_class where relname = '" + DB.sqlInjection(table) + "' and relkind = 'r'").size() == 1;
        } else if (isMariaDB()) {
            return getManager().query("select * from INFORMATION_SCHEMA.tables where table_schema = DATABASE() and table_name = '" + DB.sqlInjection(table) + "'").size() == 1;
        } else if (isMSSQL()) {
            return getManager().query("select * from INFORMATION_SCHEMA.tables where table_schema = 'dbo' and table_name = '" + DB.sqlInjection(table) + "'").size() == 1;
        }
        return false;
    }

    public boolean column(String table, String column) {
        if (isH2()) {
            return getManager().query("select column_name from INFORMATION_SCHEMA.columns where table_name = '"+ DB.sqlInjection(table) +"' and column_name = '"+ DB.sqlInjection(column) +"'").size() == 1;
        } else if (isPostgreSQL()) {
            return getManager().query("select attname from pg_attribute where attrelid = (select oid from pg_class where relname = '" + DB.sqlInjection(table) + "') and attname = '" + DB.sqlInjection(column) + "'").size() == 1;
        } else if (isMariaDB()) {
            return getManager().query("select * from INFORMATION_SCHEMA.columns where table_schema = DATABASE() and table_name = '" + DB.sqlInjection(table) + "' and column_name = '"+ DB.sqlInjection(column) +"'").size() == 1;
        } else if (isMSSQL()) {
            return getManager().query("select * from INFORMATION_SCHEMA.columns where table_schema = 'dbo' and table_name = '" + DB.sqlInjection(table) + "' and column_name = '"+ DB.sqlInjection(column) +"'").size() == 1;
        }
        return false;
    }
    
    public boolean index(String tableName, String columnName) {
    	return index(tableName +"_"+ columnName +"_idx");
    }

    public boolean index(String index) {
        if (isH2()) {
            return getManager().query("select index_name from INFORMATION_SCHEMA.indexes where index_name = '"+ DB.sqlInjection(index) +"'").size() == 1;
        } else if (isPostgreSQL()) {
            return getManager().query("select * from pg_class where relname = '" + DB.sqlInjection(index) + "' and relkind = 'i'").size() == 1;
        } else if (isMariaDB()) {
            return getManager().query("select * from INFORMATION_SCHEMA.statistics where table_schema = DATABASE() and index_name = '"+ DB.sqlInjection(index) +"'").size() == 1;
        } else if (isMSSQL()) {
            return getManager().query("select * from sys.indexes where name = '"+ DB.sqlInjection(index) +"'").size() == 1;
        }
        return false;
    }
}
