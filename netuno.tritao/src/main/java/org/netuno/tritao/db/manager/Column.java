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
import org.netuno.tritao.db.Builder;
import org.netuno.tritao.db.DBError;

/**
 * Database Column Management
 * @author Eduardo Fonseca Velasques - @eduveks
 */
@LibraryDoc(translations = {
        @LibraryTranslationDoc(
                language=LanguageDoc.PT,
                title = "Column",
                introduction = "Realiza a manipulação de colunas em base de dados.",
                howToUse = {
                        @SourceCodeDoc(
                                type = SourceCodeTypeDoc.JavaScript,
                                code = "if (!_db.checkExists().column(\"client\", \"description\")) {\n"
                                + "    _db.column().rename(\n"
                                + "        \"client\", // Tabela\n"
                                + "        \"description\", // Nome Antigo\n"
                                + "        \"name\" // Novo Nome\n"
                                + "    );\n"
                                + "}"
                        )
                }
        )
})
public class Column extends Base {

    private static Logger logger = LogManager.getLogger(Column.class);

    public enum Type {
        INT,
        DECIMAL,
        UUID,
        VARCHAR,
        BOOLEAN,
        TEXT,
        TIMESTAMP,
        DATE,
        TIME;

        private Builder builder;

        public Builder getBuilder() {
            return builder;
        }

        public Type setBuilder(Builder builder) {
            this.builder = builder;
            return this;
        }
        
        public static Type valueFrom(String name) {
        	for(Type t : Type.values()) { 
                if (t.name().equalsIgnoreCase(name)) {
                	return t;
                }
            }
        	return null;
        }

        @Override
        public String toString() {
            if (isH2(builder)) {
                if (this == Type.DECIMAL) {
                    return "double";
                } else if (this == Type.VARCHAR) {
                    return "varchar";
                }
            } else if (isMSSQL(builder)) {
                if (this == Type.UUID) {
                    return "uniqueidentifier";
                }
                if (this == Type.BOOLEAN) {
                    return "bit";
                }
                if (this == Type.TIMESTAMP) {
                    return "datetime";
                }
            } else if (isMariaDB(builder)) {
                if (this == Type.UUID) {
                    return "char";
                }
            }
            return super.toString().toLowerCase();
        }
    }

    protected String name;
    protected Type type;
    protected boolean primaryKey;
    protected boolean notNull;
    protected int maxLength = 0;
    protected String _default;

    public Column(Base base) {
        super(base);
    }

    public Column(Proteu proteu, Hili hili, String key) {
        super(proteu, hili, key);
    }

    public String getName() {
        return name;
    }

    public Column setName(String name) {
        try {
            this.name = DB.sqlInjectionRawName(name);
        } catch (Exception e) {
            throw new DBError(e).setLogFatal("Setting column name "+ name +".");
        }
        return this;
    }

    public Type getType() {
        return type;
    }
    
    public Column setType(String typeName) {
    	return setType(Type.valueFrom(typeName));
    }

    public Column setType(Type type) {
        this.type = type;
        this.type.setBuilder(this.getBuilder());
        if (getType() == Type.VARCHAR) {
            maxLength = 250;
        }
        if (isMariaDB() && getType() == Type.UUID) {
            maxLength = 36;
        }
        return this;
    }

    public boolean isPrimaryKey() {
        return primaryKey;
    }

    public Column setPrimaryKey(boolean primaryKey) {
        this.primaryKey = primaryKey;
        return this;
    }

    public boolean isNotNull() {
        return notNull;
    }

    public Column setNotNull(boolean notNull) {
        this.notNull = notNull;
        return this;
    }

    public int getMaxLength() {
        return maxLength;
    }

    public Column setMaxLength(int maxLength) {
        this.maxLength = maxLength;
        return this;
    }

    public String getDefault() {
        return _default;
    }

    public Column setDefault() {
        if (getType() == Type.INT) {
            this.setDefault(0);
        } else if (getType() == Type.DECIMAL) {
            this.setDefault(0);
        } else if (getType() == Type.VARCHAR || getType() == Type.TEXT) {
            this.setDefault("");
        } else if (getType() == Type.BOOLEAN) {
            this.setDefault(false);
        } else if (getType() == Type.UUID) {
            this.setDefaultAsNewUUID();
        } else if (getType() == Type.TIMESTAMP) {
            this.setDefaultAsCurrentTimeStamp();
        } else if (getType() == Type.DATE) {
            this.setDefaultAsCurrentDate();
        } else if (getType() == Type.TIME) {
            this.setDefaultAsCurrentTime();
        } else {
            throw new DBError("Default is not supported or type was not defined yet.")
                    .setLogFatal(true);
        }
        return this;
    }

    public Column setDefault(int _default) {
        if (getType() == Type.INT) {
            this._default = Integer.toString(_default);
        } else if (getType() == Type.DECIMAL) {
            this._default = Double.toString(_default);
        } else {
            throw new DBError("Invalid type.").setLogFatal("Setting column default.");
        }
        return this;
    }

    public Column setDefault(float _default) {
        if (getType() == Type.INT) {
            this._default = Integer.toString((int)_default);
        } else if (getType() == Type.DECIMAL) {
            this._default = Double.toString(_default);
        } else {
            throw new DBError("Invalid type.").setLogFatal("Setting column default.");
        }
        return this;
    }

    public Column setDefault(String _default) {
        if (getType() == Type.VARCHAR || getType() == Type.TEXT) {
            if (isH2() || isPostgreSQL()) {
                this._default = "'"+ DB.sqlInjection(_default) +"'";
            }
        } else {
            throw new DBError("Invalid type.").setLogFatal("Setting column default.");
        }
        return this;
    }

    public Column setDefault(boolean _default) {
        if (getType() == Type.BOOLEAN) {
            this._default = _default ? getBuilder().booleanTrue() : getBuilder().booleanFalse();
        } else if (getType() == Type.TIMESTAMP) {
            this._default = _default ? getBuilder().getCurrentTimeStampFunction() : "";
        } else {
            throw new DBError("Invalid type.").setLogFatal("Setting column default.");
        }
        return this;
    }

    public Column setDefaultAsNewUUID() {
        if (getType() == Type.UUID) {
            if (isMariaDB()) {
                this._default = "null";
            } else {
                this._default = getBuilder().getUUIDFunction();
            }
        } else {
            throw new DBError("Invalid type.").setLogFatal("Setting column default as UUID.");
        }
        return this;
    }

    public Column setDefaultAsCurrentTimeStamp() {
        if (getType() == Type.TIMESTAMP) {
            this._default = getBuilder().getCurrentTimeStampFunction();
        } else {
            throw new DBError("Invalid type.").setLogFatal("Setting current timestamp.");
        }
        return this;
    }

    public Column setDefaultAsCurrentDate() {
        if (getType() == Type.DATE) {
            if (isMariaDB()) {
                this._default = "";
            } else {
                this._default = getBuilder().getCurrentDateFunction();
            }
        } else {
            throw new DBError("Invalid type.").setLogFatal("Setting current date.");
        }
        return this;
    }

    public Column setDefaultAsCurrentTime() {
        if (getType() == Type.TIME) {
            if (isMariaDB()) {
                this._default = "";
            } else {
                this._default = getBuilder().getCurrentTimeFunction();
            }
        } else {
            throw new DBError("Invalid type.").setLogFatal("Setting current time.");
        }
        return this;
    }

    public String toString() {
        if (isH2() || isPostgreSQL() || isMSSQL()) {
            return ""+ getBuilder().escape(getName()) +" "+ getType()
                    + (getMaxLength() > 0 ? "("+ getMaxLength() +")" : "")
                    + (isPrimaryKey() ? " primary key"+ (isMSSQL() && getType() == Type.INT ? " identity" : "") : "")
                    + (getDefault() != null && !getDefault().isEmpty() ? " default "+ getDefault() : "");
        } else if (isMariaDB()) {
            return ""+ getBuilder().escape(getName()) +" "+ getType()
                    + (getMaxLength() > 0 ? "("+ getMaxLength() +")" : "")
                    + (isPrimaryKey() && getType() == Type.INT ? " auto_increment" : "")
                    + (getDefault() != null && !getDefault().isEmpty() ? " default "+ getDefault() : "");
        }
        return "";
    }

    public String toDefinitionString() {
    	String defaultLength = "";
    	String extraLength = "";
    	if (getType() == Type.DECIMAL) {
    		defaultLength = "(10)"; // , 2
    		extraLength = ""; // , 2
    	}
        if (isH2() || isPostgreSQL() || isMSSQL()) {
            return getType()
                    + (getMaxLength() > 0 ? "("+ getMaxLength() + extraLength +")" : defaultLength)
                    + (getDefault() != null && !getDefault().isEmpty() ? " default "+ getDefault() : "");
        } else if (isMariaDB()) {
            return getType()
                    + (getMaxLength() > 0 ? "("+ getMaxLength() + extraLength +")" : defaultLength)
                    + (getDefault() != null && !getDefault().isEmpty() ? " default "+ getDefault() : "");
        }
        return "";
    }

    public Column changeType(String table) {
        try {
            if (isMariaDB()) {
                getManager().execute("alter table " + getBuilder().escape(DB.sqlInjectionRawName(table)) + " modify column " + getBuilder().escape(DB.sqlInjectionRawName(getName())) + " " + toDefinitionString() + ";");
            } else if (isMSSQL()) {
                getManager().execute("alter table " + getBuilder().escape(DB.sqlInjectionRawName(table)) + " alter column " + getBuilder().escape(DB.sqlInjectionRawName(getName())) + " " + toDefinitionString() + ";");
            } else {
                getManager().execute("alter table " + getBuilder().escape(DB.sqlInjectionRawName(table)) + " alter column " + getBuilder().escape(DB.sqlInjectionRawName(getName())) + " type " + toDefinitionString() + ";");
            }
        } catch (Exception e) {
            throw new DBError(e).setLogFatal("Changing column type on "+ table +"."+ getName());
        }
        return this;
    }

    public Column rename(String table, String oldName, String newName) {
        try {
            String oldRawSQLName = DB.sqlInjectionRawName(oldName);
            String newRawSQLName = DB.sqlInjectionRawName(newName);
            if (!new CheckExists(this).column(table, newRawSQLName)) {
                if (isH2()) {
                    getManager().execute("alter table " + getBuilder().escape(DB.sqlInjectionRawName(table)) + " alter column " + getBuilder().escape(oldRawSQLName) + " rename to " + getBuilder().escape(newRawSQLName) + "");
                } else if (isPostgreSQL()) {
                    getManager().execute("alter table " + getBuilder().escape(DB.sqlInjectionRawName(table)) + " rename column " + getBuilder().escape(oldRawSQLName) + " to " + getBuilder().escape(newRawSQLName) + "");
                } else if (isMariaDB()) {
                	getManager().execute("alter table " + getBuilder().escape(DB.sqlInjectionRawName(table)) + " change column " + getBuilder().escape(oldRawSQLName) + " " + getBuilder().escape(newRawSQLName) + " "+ toDefinitionString());
                } else if (isMSSQL()) {
                	getManager().execute("exec sp_rename '" + DB.sqlInjectionRawName(table) + "." + getBuilder().escape(oldRawSQLName) + "', '" + newRawSQLName + "', 'COLUMN'");
                }
            }
        } catch (Exception e) {
            throw new DBError(e).setLogFatal("Renaming column name from "+ oldName +" to "+ newName +" on table "+ table +".");
        }
        return this;
    }

    public Column renameIfExists(String table, String oldName, String newName) {
        try {
            if (new CheckExists(this).column(table, oldName)) {
                rename(table, oldName, newName);
            }
        } catch (Exception e) {
            throw new DBError(e).setLogFatal("Renaming column name from "+ oldName +" to "+ newName +" on table "+ table +".");
        }
        return this;
    }



    public Column drop(String table, String column) {
        try {
            if (new CheckExists(this).column(table, column)) {
                getManager().execute("alter table "+ getBuilder().escape(DB.sqlInjectionRawName(table)) +" drop column "+ getBuilder().escape(DB.sqlInjectionRawName(column)) + ";");
            }
        } catch (Exception e) {
            throw new DBError(e).setLogFatal("Dropping column "+ table +"."+ column +".");
        }
        return this;
    }
}
