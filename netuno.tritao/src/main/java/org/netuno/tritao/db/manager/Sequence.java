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
import org.netuno.psamata.Values;
import org.netuno.tritao.hili.Hili;
import org.netuno.tritao.db.DBError;

import java.util.List;

/**
 * Database Sequence Management
 * @author Eduardo Fonseca Velasques - @eduveks
 */
@LibraryDoc(translations = {
        @LibraryTranslationDoc(
                language=LanguageDoc.PT,
                title = "Sequence",
                introduction = "Realiza a manipulação de sequências em base de dados.",
                howToUse = {
                        @SourceCodeDoc(
                                type = SourceCodeTypeDoc.JavaScript,
                                code = "if (!_db.checkExists().sequence(\"client\", \"name\")) {\n"
                                + "    _db.index().create(\n"
                                + "        \"client\", // Nome da Tabela\n"
                                + "        \"name\" // Nome da Coluna\n"
                                + "    ); // O index client_name_idx será criado criado.\n"
                                + "}"
                        )
                }
        )
})
public class Sequence extends Base {
    private static Logger logger = LogManager.getLogger(Sequence.class);

    public Sequence(Base base) {
        super(base);
    }

    public Sequence(Proteu proteu, Hili hili, String key) {
        super(proteu, hili, key);
    }

    public boolean supported() {
        return isH2() || isPostgreSQL();
    }

    public Sequence create(String name) {
        return create(name, 1);
    }

    public Sequence create(String name, int startWith) {
        try {
            String rawSQLName = DB.sqlInjectionRawName(name);
            if ((isH2() || isPostgreSQL())
                && !new CheckExists(this).sequence(rawSQLName)
            ) {
                getManager().execute("create sequence "+ getBuilder().appendIfNotExists() +" " + getBuilder().escape(rawSQLName) + " start with " + startWith + ";");
            }
        } catch (Exception e) {
            throw new DBError(e).setLogFatal("Creating sequence "+ name +".");
        }
        return this;
    }

    public Sequence drop(String name) {
        try {
            if ((isH2() || isPostgreSQL())
                && new CheckExists(this).sequence(name)
            ) {
                getManager().execute("drop sequence "+ getBuilder().appendIfExists() +" "+ getBuilder().escape(DB.sqlInjectionRawName(name)) + "");
            }
        } catch (Exception e) {
            throw new DBError(e).setLogFatal("Dropping sequence "+ name +".");
        }
        return this;
    }

    public Sequence rename(String oldName, String newName) {
        try {
            String oldRawSQLName = DB.sqlInjectionRawName(oldName);
            String newRawSQLName = DB.sqlInjectionRawName(newName);
            if (!new CheckExists(this).sequence(newRawSQLName)) {
                if (isH2()) {
                    getManager().execute("create sequence "+ getBuilder().appendIfNotExists() +" " + getBuilder().escape(newRawSQLName) + " start with (next value for "+ getBuilder().escape(oldRawSQLName) +");");
                    drop(oldRawSQLName);
                } else if (isPostgreSQL()) {
                    getManager().execute("alter sequence " + getBuilder().escape(oldRawSQLName) + " rename to " + getBuilder().escape(newRawSQLName) + "");
                }
            }
        } catch (Exception e) {
            throw new DBError(e).setLogFatal("Renaming sequence from "+ oldName +" to "+ newName +".");
        }
        return this;
    }

    public Sequence renameIfExists(String oldName, String newName) {
        try {
            String oldRawSQLName = DB.sqlInjectionRawName(oldName);
            if (new CheckExists(this).sequence(oldRawSQLName)) {
                rename(oldName, newName);
            }
        } catch (Exception e) {
            throw new DBError(e).setLogFatal("Renaming sequence from "+ oldName +" to "+ newName +".");
        }
        return this;
    }

    public int getCurrentValue(String sequenceName) {
        try {
            int value = 0;
            if (isH2()) {
                value = getManager().query("select current_value from information_schema.sequences where sequence_name = " + getBuilder().escape(DB.sqlInjectionRawName(sequenceName)) + "").get(0).getInt("current_value");
            } else if (isPostgreSQL()) {
                value = getManager().query("select last_value from " + getBuilder().escape(DB.sqlInjectionRawName(sequenceName)) + "").get(0).getInt("last_value");
            }
            return value <= 0 ? 1 : value;
        } catch (Exception e) {
            throw new DBError(e).setLogFatal("Current value for sequence "+ sequenceName +".");
        }
    }

    public String commandNextValue(String sequenceName) {
        return "nextval('"+ sequenceName +"')";
    }

    public Sequence restart(String sequenceName, int nextValue) {
        try {
            if (isH2() || isPostgreSQL()) {
                getManager().execute("alter sequence " + getBuilder().escape(sequenceName) + " restart with " + nextValue + ";");
            }
        } catch (Exception e) {
            throw new DBError(e).setLogFatal("Restarting sequence "+ sequenceName +" with "+ nextValue +".");

        }
        return this;
    }

    public Sequence restart(String sequenceName, String tableName, String column) {
        try {
            if (isH2() || isPostgreSQL()) {
                List<Values> result = getManager().query("select max(" + getBuilder().escape(column) + ") from " + getBuilder().escape(tableName));
                if (result.size() == 1) {
                    int total = Integer.parseInt(result.get(0).values().iterator().next().toString());
                    restart(sequenceName, total + 1);
                }
            }
        } catch (Exception e) {
            throw new DBError(e).setLogFatal("Restart sequence "+ sequenceName +" with "+ tableName +"."+ column +".");
        }
        return this;
    }
}
