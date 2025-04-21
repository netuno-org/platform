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
import org.netuno.tritao.hili.Hili;
import org.netuno.tritao.db.DBError;

/**
 * Database Table Management
 * @author Eduardo Fonseca Velasques - @eduveks
 */
@LibraryDoc(translations = {
        @LibraryTranslationDoc(
                language=LanguageDoc.PT,
                title = "Table",
                introduction = "Realiza a manipulação de tabelas em base de dados.",
                howToUse = {
                        @SourceCodeDoc(
                                type = SourceCodeTypeDoc.JavaScript,
                                code = "if (_db.checkExists().table(\"clients\")) {\n"
                                + "    _db.table().rename(\n"
                                + "        \"clients\", // Nome Antigo\n"
                                + "        \"client\" // Novo Nome\n"
                                + "    );\n"
                                + "}"
                        )
                }
        )
})
public class Table extends ManagerBase {
    private static Logger logger = LogManager.getLogger(Table.class);

    public Table(ManagerBase base) {
        super(base);
    }

    public Table(Proteu proteu, Hili hili, String key) {
        super(proteu, hili, key);
    }

    public Table rename(String oldName, String newName) {
        try {
            String newRawSQLName = DB.sqlInjectionRawName(newName);
            if (!new CheckExists(this).table(newRawSQLName)) {
                if (isH2() || isPostgreSQL()) {
                    getExecutor().execute("alter table " + getBuilder().escape(DB.sqlInjectionRawName(oldName)) + " rename to " + getBuilder().escape(newRawSQLName) + "");
                } else if (isMariaDB()) {
                    getExecutor().execute("rename table " + getBuilder().escape(DB.sqlInjectionRawName(oldName)) + " to " + getBuilder().escape(newRawSQLName) + "");
                }
            }
        } catch (Exception e) {
            throw new DBError(e).setLogFatal("Renaming table from "+ oldName +" to "+ newName +".");
        }
        return this;
    }

    public Table renameIfExists(String oldName, String newName) {
        try {
            String oldRawSQLName = DB.sqlInjectionRawName(oldName);
            String newRawSQLName = DB.sqlInjectionRawName(newName);
            if (new CheckExists(this).table(oldRawSQLName)) {
                rename(oldRawSQLName, newRawSQLName);
            }
        } catch (Exception e) {
            throw new DBError(e).setLogFatal("Renaming table from "+ oldName +" to "+ newName +".");
        }
        return this;
    }

    public Table drop(String name) {
        try {
            String rawSQLName = DB.sqlInjectionRawName(name);
            if (new CheckExists(this).table(rawSQLName)) {
                getExecutor().execute("drop table "+ getBuilder().escape(rawSQLName) + "");
            }
        } catch (Exception e) {
            throw new DBError(e).setLogFatal("Dropping table "+ name +".");
        }
        return this;
    }

    public Column newColumn() {
        return new Column(this);
    }

    public Table create(String name, Column... columns) {
        try {
            CheckExists checkExists = new CheckExists(this);
            if (!checkExists.table(name)) {
                if ((isH2() || isPostgreSQL() || isMariaDB() || isMSSQL())) {
                    String columnsDefinitions = "";
                    String extraDefinitions = "";
                    for (Column column : columns) {
                        if (columnsDefinitions.isEmpty()) {
                            columnsDefinitions += column;
                        } else {
                            columnsDefinitions += ", " + column;
                        }
                        if (isMariaDB()) {
                            if (column.isPrimaryKey()) {
                                if (extraDefinitions.isEmpty()) {
                                    extraDefinitions = "primary key(";
                                } else {
                                    extraDefinitions += ", ";
                                }
                                extraDefinitions += getBuilder().escape(column.getName());
                            }
                        }
                    }
                    if (!extraDefinitions.isEmpty()) {
                        extraDefinitions += ")";
                    }
                    getExecutor().execute("create table " + getBuilder().appendIfNotExists() + " " + getBuilder().escape(DB.sqlInjectionRawName(name)) + "(" +
                            columnsDefinitions
                            + (extraDefinitions.isEmpty() ? "" : ", " + extraDefinitions)
                            + ")");
                } else {
                    throw new Exception("Not supported!");
                }
            } else {
                String alterTableCommands = "";
                for (Column column : columns) {
                    if (!checkExists.column(name, column.getName())) {
                        String command = "alter table "
                                + getBuilder().escape(DB.sqlInjectionRawName(name))
                                + " add " + (isH2() || isPostgreSQL() ? "column " : "") +
                                column +";";
                        if (isPostgreSQL() || isH2()) {
                            alterTableCommands += command;
                        } else if (isMariaDB()) {
                            getExecutor().execute(command);
                        } else if (isMSSQL()) {
                            getExecutor().execute(command);
                        } else {
                            throw new Exception("Not supported!");
                        }
                    }
                }
                if (!alterTableCommands.isEmpty()) {
                    getExecutor().execute(alterTableCommands);
                }
            }
        } catch (Exception e) {
            throw new DBError(e).setLogFatal("Creating table "+ name +".");
        }
        return this;
    }
}
