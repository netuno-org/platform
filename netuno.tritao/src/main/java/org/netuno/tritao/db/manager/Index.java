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
import org.netuno.psamata.PsamataException;
import org.netuno.tritao.config.Hili;
import org.netuno.tritao.db.DBError;

/**
 * Database Index Management
 * @author Eduardo Fonseca Velasques - @eduveks
 */
@LibraryDoc(translations = {
        @LibraryTranslationDoc(
                language=LanguageDoc.PT,
                title = "Index",
                introduction = "Realiza a manipulação de indexes em base de dados.",
                howToUse = {
                        @SourceCodeDoc(
                                type = SourceCodeTypeDoc.JavaScript,
                                code = "if (!_db.checkExists().index(\"client\", \"name\")) {\n"
                                + "    _db.index().create(\n"
                                + "        \"client\", // Nome da Tabela\n"
                                + "        \"name\" // Nome da Coluna\n"
                                + "    ); // O index client_name_idx será criado criado.\n"
                                + "}"
                        )
                }
        )
})
public class Index extends Base {
    private static Logger logger = LogManager.getLogger(Index.class);

    public Index(Base base) {
        super(base);
    }

    public Index(Proteu proteu, Hili hili, String key) {
        super(proteu, hili, key);
    }

    public Index create(String table, String column) {
        try {
            String indexName = DB.sqlInjectionRawName(table + "_" + column + "_idx");
            if ((isH2() || isPostgreSQL())
                    && !new CheckExists(this).index(indexName)) {
                getManager().execute("create index "+ getBuilder().appendIfNotExists() +" " + getBuilder().escape(indexName) + " on " + getBuilder().escape(DB.sqlInjectionRawName(table)) + "(" + getBuilder().escape(DB.sqlInjectionRawName(column)) + ")");
            }
        } catch (PsamataException e) {
            logger.fatal(e);
            throw new DBError(e).setLogFatal("Creating index on "+ table +"."+ column +".");
        }
        return this;
    }
}
