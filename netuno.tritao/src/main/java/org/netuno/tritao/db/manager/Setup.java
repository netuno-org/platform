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

import com.vdurmont.emoji.EmojiParser;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.netuno.proteu.Proteu;
import org.netuno.psamata.Values;
import org.netuno.tritao.config.Config;
import org.netuno.tritao.db.builder.BuilderBase;
import org.netuno.tritao.hili.Hili;
import org.netuno.tritao.db.DBError;

import java.util.List;

/**
 * Database Setup
 * @author Eduardo Fonseca Velasques - @eduveks
 */
public class Setup extends ManagerBase {
    private static Logger logger = LogManager.getLogger(Setup.class);

    public Setup(BuilderBase base) {
        super(base);
    }

    public Setup(Proteu proteu, Hili hili) {
        super(proteu, hili, "default");
    }

    public Setup(Proteu proteu, Hili hili, String key) {
        super(proteu, hili, key);
    }

    public void run() {
        try {
            CheckExists checkExists = new CheckExists(this);
            Index index = new Index(this);
            Sequence sequence = new Sequence(this)
                    .renameIfExists("tritao_table_id", "netuno_table_id")
                    .renameIfExists("tritao_design_id", "netuno_design_id")
                    .renameIfExists("tritao_group_id", "netuno_group_id")
                    .renameIfExists("tritao_user_id", "netuno_user_id")
                    .renameIfExists("tritao_group_rules_id", "netuno_group_rule_id")
                    .renameIfExists("tritao_user_rules_id", "netuno_user_rule_id")
                    .renameIfExists("tritao_client_id", "netuno_client_id")
                    .renameIfExists("tritao_client_hits_id", "netuno_client_hit_id")
                    .renameIfExists("tritao_log_id", "netuno_log_id")
                    .renameIfExists("tritao_statistic_type_id", "netuno_statistic_type_id")
                    .renameIfExists("tritao_statistic_moment_id", "netuno_statistic_moment_id")
                    .renameIfExists("tritao_statistic_avarage_id", "netuno_statistic_avarage_id");
            Table table = new Table(this)
                    .renameIfExists("tritao_table", "netuno_table")
                    .renameIfExists("tritao_design", "netuno_design")
                    .renameIfExists("tritao_group", "netuno_group")
                    .renameIfExists("tritao_user", "netuno_user")
                    .renameIfExists("tritao_group_rules", "netuno_group_rule")
                    .renameIfExists("tritao_user_rules", "netuno_user_rule")
                    .renameIfExists("tritao_client", "netuno_client")
                    .renameIfExists("tritao_client_hits", "netuno_client_hit")
                    .renameIfExists("tritao_log", "netuno_log")
                    .renameIfExists("tritao_statistic_type", "netuno_statistic_type")
                    .renameIfExists("tritao_statistic_moment", "netuno_statistic_moment")
                    .renameIfExists("tritao_statistic_avarage", "netuno_statistic_avarage");

            table.drop("tritao_user_tokens");
            sequence.drop("tritao_user_tokens_id");

            new Column(this)
                    .renameIfExists("netuno_group", "tritao_group", "netuno_group");

            table.create("netuno_app",
                    table.newColumn().setName("id").setType(Column.Type.INT).setPrimaryKey(true),
                    table.newColumn().setName("uid").setType(Column.Type.UUID).setNotNull(true).setDefault(),
                    table.newColumn().setName("name").setType(Column.Type.VARCHAR).setNotNull(true).setDefault(),
                    table.newColumn().setName("config").setType(Column.Type.TEXT).setNotNull(false).setDefault(),
                    table.newColumn().setName("extra").setType(Column.Type.TEXT).setNotNull(false).setDefault()
            );
            sequence.create("netuno_app_id");
            
            table.create("netuno_app_table",
                    table.newColumn().setName("id").setType(Column.Type.INT).setPrimaryKey(true),
                    table.newColumn().setName("uid").setType(Column.Type.UUID).setNotNull(true).setDefault(),
                    table.newColumn().setName("app_id").setType(Column.Type.INT).setNotNull(true).setDefault(),
                    table.newColumn().setName("table_id").setType(Column.Type.INT).setNotNull(true).setDefault()
            );
            index.create("netuno_app_table", "app_id");
            index.create("netuno_app_table", "table_id");
            sequence.create("netuno_app_table_id");
            
            table.create("netuno_app_meta",
                    table.newColumn().setName("id").setType(Column.Type.INT).setPrimaryKey(true),
                    table.newColumn().setName("uid").setType(Column.Type.UUID).setNotNull(true).setDefault(),
                    table.newColumn().setName("key").setType(Column.Type.VARCHAR).setNotNull(true).setDefault(),
                    table.newColumn().setName("value").setType(Column.Type.TEXT).setNotNull(false).setDefault()
            );
            sequence.create("netuno_app_meta_id");

            if (checkExists.column("netuno_table", "displayname")) {
                new Column(this)
                        .renameIfExists("netuno_table", "displayname", "title");
            }

            table.create("netuno_table",
                    table.newColumn().setName("id").setType(Column.Type.INT).setPrimaryKey(true),
                    table.newColumn().setName("uid").setType(Column.Type.UUID).setNotNull(true).setDefault(),
                    table.newColumn().setName("name").setType(Column.Type.VARCHAR).setNotNull(true).setDefault(),
                    table.newColumn().setName("title").setType(Column.Type.VARCHAR).setNotNull(true).setDefault(),
                    table.newColumn().setName("description").setType(Column.Type.TEXT),
                    table.newColumn().setName("user_id").setType(Column.Type.INT).setNotNull(true).setDefault(),
                    table.newColumn().setName("group_id").setType(Column.Type.INT).setNotNull(true).setDefault(),
                    table.newColumn().setName("js").setType(Column.Type.TEXT),
                    table.newColumn().setName("report").setType(Column.Type.BOOLEAN).setDefault(),
                    table.newColumn().setName("show_id").setType(Column.Type.BOOLEAN).setDefault(true),
                    table.newColumn().setName("control_active").setType(Column.Type.BOOLEAN).setDefault(true),
                    table.newColumn().setName("control_user").setType(Column.Type.BOOLEAN).setDefault(),
                    table.newColumn().setName("control_group").setType(Column.Type.BOOLEAN).setDefault(),
                    table.newColumn().setName("export_xls").setType(Column.Type.BOOLEAN).setDefault(true),
                    table.newColumn().setName("export_xml").setType(Column.Type.BOOLEAN).setDefault(true),
                    table.newColumn().setName("export_json").setType(Column.Type.BOOLEAN).setDefault(true),
                    table.newColumn().setName("export_id").setType(Column.Type.BOOLEAN).setDefault(true),
                    table.newColumn().setName("export_uid").setType(Column.Type.BOOLEAN).setDefault(true),
                    table.newColumn().setName("export_lastchange").setType(Column.Type.BOOLEAN).setDefault(true),
                    table.newColumn().setName("big").setType(Column.Type.BOOLEAN).setDefault(),
                    table.newColumn().setName("parent_id").setType(Column.Type.INT).setDefault(),
                    table.newColumn().setName("reorder").setType(Column.Type.INT).setNotNull(true).setDefault(),
                    table.newColumn().setName("report_behaviour").setType(Column.Type.INT).setNotNull(false).setDefault(),
                    table.newColumn().setName("firebase").setType(Column.Type.VARCHAR).setDefault()
            );
            index.create("netuno_table", "group_id");
            index.create("netuno_table", "parent_id");
            sequence.create("netuno_table_id");

            //if (dbManager.executeQuery("select attname from pg_attribute where attrelid = (select oid from pg_class where relname = 'netuno_table') and attname = 'show_lastchange'").size() == 0) {
            //    dbManager.execute("alter table netuno_table add column show_lastchange boolean default false");
            //}


            if (checkExists.column("netuno_design", "displayname")) {
                new Column(this)
                        .renameIfExists("netuno_design", "displayname", "title");
            }
            if (checkExists.column("netuno_design", "notnull")) {
                new Column(this)
                        .renameIfExists("netuno_design", "notnull", "mandatory");
            }
            if (checkExists.column("netuno_design", "primarykey")) {
                new Column(this)
                        .renameIfExists("netuno_design", "primarykey", "unique");
            }

            table.create("netuno_design",
                    table.newColumn().setName("id").setType(Column.Type.INT).setPrimaryKey(true),
                    table.newColumn().setName("uid").setType(Column.Type.UUID).setNotNull(true).setDefault(),
                    table.newColumn().setName("table_id").setType(Column.Type.INT).setNotNull(true).setDefault(),
                    table.newColumn().setName("name").setType(Column.Type.VARCHAR).setNotNull(true).setDefault(),
                    table.newColumn().setName("title").setType(Column.Type.VARCHAR).setNotNull(true).setDefault(),
                    table.newColumn().setName("description").setType(Column.Type.TEXT),
                    table.newColumn().setName("x").setType(Column.Type.INT).setNotNull(true).setDefault(),
                    table.newColumn().setName("y").setType(Column.Type.INT).setNotNull(true).setDefault(),
                    table.newColumn().setName("type").setType(Column.Type.VARCHAR).setMaxLength(50).setNotNull(true).setDefault(),
                    table.newColumn().setName("unique").setType(Column.Type.BOOLEAN).setNotNull(true).setDefault(false),
                    table.newColumn().setName("mandatory").setType(Column.Type.BOOLEAN).setNotNull(true).setDefault(false),
                    table.newColumn().setName("width").setType(Column.Type.INT).setNotNull(true).setDefault(),
                    table.newColumn().setName("height").setType(Column.Type.INT).setNotNull(true).setDefault(),
                    table.newColumn().setName("max").setType(Column.Type.INT).setNotNull(true).setDefault(),
                    table.newColumn().setName("min").setType(Column.Type.INT).setNotNull(true).setDefault(),
                    table.newColumn().setName("colspan").setType(Column.Type.INT).setNotNull(true).setDefault(),
                    table.newColumn().setName("rowspan").setType(Column.Type.INT).setNotNull(true).setDefault(),
                    table.newColumn().setName("tdwidth").setType(Column.Type.INT).setNotNull(true).setDefault(),
                    table.newColumn().setName("tdheight").setType(Column.Type.INT).setNotNull(true).setDefault(),
                    table.newColumn().setName("whenresult").setType(Column.Type.BOOLEAN).setNotNull(true).setDefault(true),
                    table.newColumn().setName("whenfilter").setType(Column.Type.BOOLEAN).setNotNull(true).setDefault(true),
                    table.newColumn().setName("whenedit").setType(Column.Type.BOOLEAN).setNotNull(true).setDefault(true),
                    table.newColumn().setName("whenview").setType(Column.Type.BOOLEAN).setNotNull(true).setDefault(true),
                    table.newColumn().setName("whennew").setType(Column.Type.BOOLEAN).setNotNull(true).setDefault(true),
                    table.newColumn().setName("whenexport").setType(Column.Type.BOOLEAN).setNotNull(true).setDefault(true),
                    table.newColumn().setName("user_id").setType(Column.Type.INT).setNotNull(true).setDefault(),
                    table.newColumn().setName("group_id").setType(Column.Type.INT).setNotNull(true).setDefault(),
                    table.newColumn().setName("view_user_id").setType(Column.Type.INT).setNotNull(true).setDefault(),
                    table.newColumn().setName("view_group_id").setType(Column.Type.INT).setNotNull(true).setDefault(),
                    table.newColumn().setName("edit_user_id").setType(Column.Type.INT).setNotNull(true).setDefault(),
                    table.newColumn().setName("edit_group_id").setType(Column.Type.INT).setNotNull(true).setDefault(),
                    table.newColumn().setName("properties").setType(Column.Type.TEXT).setNotNull(true).setDefault(),
                    table.newColumn().setName("firebase").setType(Column.Type.VARCHAR).setDefault()
            );
            index.create("netuno_design", "table_id");
            sequence.create("netuno_design_id");

            table.create("netuno_group",
                    table.newColumn().setName("id").setType(Column.Type.INT).setPrimaryKey(true),
                    table.newColumn().setName("uid").setType(Column.Type.UUID).setNotNull(true).setDefault(),
                    table.newColumn().setName("name").setType(Column.Type.VARCHAR).setNotNull(true).setDefault(),
                    table.newColumn().setName("netuno_group").setType(Column.Type.INT).setNotNull(true).setDefault(),
                    table.newColumn().setName("login_allowed").setType(Column.Type.BOOLEAN).setNotNull(true).setDefault(true),
                    table.newColumn().setName("active").setType(Column.Type.BOOLEAN).setNotNull(true).setDefault(true),
                    table.newColumn().setName("report").setType(Column.Type.TEXT).setNotNull(false).setDefault(),
                    table.newColumn().setName("code").setType(Column.Type.VARCHAR).setNotNull(false).setDefault(),
                    table.newColumn().setName("mail").setType(Column.Type.VARCHAR).setNotNull(false).setDefault(),
                    table.newColumn().setName("config").setType(Column.Type.TEXT).setNotNull(false).setDefault(),
                    table.newColumn().setName("extra").setType(Column.Type.TEXT).setNotNull(false).setDefault()
            );
            sequence.create("netuno_group_id");

            if (checkExists.table("netuno_providers")) {
                table.rename("netuno_providers", "netuno_provider");
                sequence.rename("netuno_providers_id", "netuno_provider_id");
            }

            table.create("netuno_auth_provider",
                    table.newColumn().setName("id").setType(Column.Type.INT).setPrimaryKey(true),
                    table.newColumn().setName("name").setType(Column.Type.VARCHAR).setNotNull(true).setDefault(),
                    table.newColumn().setName("code").setType(Column.Type.VARCHAR).setNotNull(true).setDefault()
            );
            sequence.create("netuno_auth_provider_id");

            if (checkExists.table("netuno_provider")) {
                table.drop("netuno_provider");
                sequence.drop("netuno_provider");
            }

            if (getBuilder().getAuthProviderByCode("ldap") == null) {
                getBuilder().insertAuthProvider("LDAP", "ldap");
            }

            if (getBuilder().getAuthProviderByCode("google") == null) {
                getBuilder().insertAuthProvider("Google", "google");
            }

            if (getBuilder().getAuthProviderByCode("github") == null) {
                getBuilder().insertAuthProvider("GitHub", "github");
            }

            if (getBuilder().getAuthProviderByCode("discord") == null) {
                getBuilder().insertAuthProvider("Discord", "discord");
	        }

            table.create("netuno_user",
                    table.newColumn().setName("id").setType(Column.Type.INT).setPrimaryKey(true),
                    table.newColumn().setName("uid").setType(Column.Type.UUID).setNotNull(true).setDefault(),
                    table.newColumn().setName("name").setType(Column.Type.VARCHAR).setNotNull(true).setDefault(),
                    table.newColumn().setName("nonce").setType(Column.Type.VARCHAR).setNotNull(false).setDefault(),
                    table.newColumn().setName("nonce_generator").setType(Column.Type.VARCHAR).setNotNull(false).setDefault(),
                    table.newColumn().setName("group_id").setType(Column.Type.INT).setNotNull(true).setDefault(),
                    table.newColumn().setName("user").setType(Column.Type.VARCHAR).setNotNull(true).setDefault(),
                    table.newColumn().setName("pass").setType(Column.Type.VARCHAR).setNotNull(true).setDefault(),
                    table.newColumn().setName("active").setType(Column.Type.BOOLEAN).setNotNull(true).setDefault(true),
                    table.newColumn().setName("no_pass").setType(Column.Type.BOOLEAN).setNotNull(true).setDefault(false),
                    table.newColumn().setName("report").setType(Column.Type.TEXT).setNotNull(false).setDefault(),
                    table.newColumn().setName("code").setType(Column.Type.VARCHAR).setNotNull(false).setDefault(),
                    table.newColumn().setName("mail").setType(Column.Type.VARCHAR).setNotNull(false).setDefault(),
                    table.newColumn().setName("config").setType(Column.Type.TEXT).setNotNull(false).setDefault(),
                    table.newColumn().setName("extra").setType(Column.Type.TEXT).setNotNull(false).setDefault()
            );
            index.create("netuno_user", "group_id");
            sequence.create("netuno_user_id");

            table.create("netuno_auth_history",
                    table.newColumn().setName("id").setType(Column.Type.INT).setPrimaryKey(true),
                    table.newColumn().setName("uid").setType(Column.Type.UUID).setNotNull(true).setDefault(),
                    table.newColumn().setName("user_id").setType(Column.Type.INT).setNotNull(true).setDefault(),
                    table.newColumn().setName("moment").setType(Column.Type.TIMESTAMP).setNotNull(true).setDefault(),
                    table.newColumn().setName("ip").setType(Column.Type.VARCHAR).setNotNull(false).setDefault(),
                    table.newColumn().setName("success").setType(Column.Type.BOOLEAN).setNotNull(true).setDefault(false),
                    table.newColumn().setName("lock").setType(Column.Type.BOOLEAN).setNotNull(true).setDefault(false),
                    table.newColumn().setName("unlock").setType(Column.Type.BOOLEAN).setNotNull(true).setDefault(false)
            );
            sequence.create("netuno_auth_history_id");
            index.create("netuno_auth_history", "user_id");
            index.create("netuno_auth_history", "moment");
            index.create("netuno_auth_history", "ip");

            table.create("netuno_auth_provider_user",
                    table.newColumn().setName("id").setType(Column.Type.INT).setPrimaryKey(true),
                    table.newColumn().setName("uid").setType(Column.Type.UUID).setNotNull(true).setDefault(),
                    table.newColumn().setName("provider_id").setType(Column.Type.INT).setNotNull(true).setDefault(),
                    table.newColumn().setName("user_id").setType(Column.Type.INT).setNotNull(false).setDefault(),
                    table.newColumn().setName("code").setType(Column.Type.VARCHAR).setNotNull(false).setDefault(),
                    table.newColumn().setName("moment").setType(Column.Type.TIMESTAMP).setNotNull(true).setDefault(),
                    table.newColumn().setName("email").setType(Column.Type.VARCHAR).setNotNull(false).setDefault(),
                    table.newColumn().setName("name").setType(Column.Type.VARCHAR).setNotNull(false).setDefault(),
                    table.newColumn().setName("username").setType(Column.Type.VARCHAR).setNotNull(false).setDefault()
            );
            sequence.create("netuno_auth_provider_user_id");
            index.create("netuno_auth_provider_user", "user_id");
            index.create("netuno_auth_provider_user", "provider_id");

            if (checkExists.table("netuno_provider_data")) {
                table.drop("netuno_provider_data");
                sequence.drop("netuno_provider_data_id");
            }

            if (checkExists.table("netuno_provider_user")) {
                table.drop("netuno_provider_user");
                sequence.drop("netuno_provider_user_id");
            }

            table.create("netuno_group_rule",
                    table.newColumn().setName("id").setType(Column.Type.INT).setPrimaryKey(true),
                    table.newColumn().setName("uid").setType(Column.Type.UUID).setNotNull(true).setDefault(),
                    table.newColumn().setName("group_id").setType(Column.Type.INT).setNotNull(true).setDefault(),
                    table.newColumn().setName("table_id").setType(Column.Type.INT).setNotNull(true).setDefault(),
                    table.newColumn().setName("active").setType(Column.Type.BOOLEAN).setNotNull(true).setDefault(true),
                    table.newColumn().setName("rule_read").setType(Column.Type.INT).setNotNull(true).setDefault(),
                    table.newColumn().setName("rule_write").setType(Column.Type.INT).setNotNull(true).setDefault(),
                    table.newColumn().setName("rule_delete").setType(Column.Type.INT).setNotNull(true).setDefault()
            );
            index.create("netuno_group_rule", "group_id");
            index.create("netuno_group_rule", "table_id");
            sequence.create("netuno_group_rule_id");

            table.create("netuno_user_rule",
                    table.newColumn().setName("id").setType(Column.Type.INT).setPrimaryKey(true),
                    table.newColumn().setName("uid").setType(Column.Type.UUID).setNotNull(true).setDefault(),
                    table.newColumn().setName("user_id").setType(Column.Type.INT).setNotNull(true).setDefault(),
                    table.newColumn().setName("table_id").setType(Column.Type.INT).setNotNull(true).setDefault(),
                    table.newColumn().setName("active").setType(Column.Type.BOOLEAN).setNotNull(true).setDefault(true),
                    table.newColumn().setName("rule_read").setType(Column.Type.INT).setNotNull(true).setDefault(),
                    table.newColumn().setName("rule_write").setType(Column.Type.INT).setNotNull(true).setDefault(),
                    table.newColumn().setName("rule_delete").setType(Column.Type.INT).setNotNull(true).setDefault()
            );
            index.create("netuno_user_rule", "user_id");
            index.create("netuno_user_rule", "table_id");
            sequence.create("netuno_user_rule_id");

            table.create("netuno_client",
                    table.newColumn().setName("id").setType(Column.Type.INT).setPrimaryKey(true),
                    table.newColumn().setName("uid").setType(Column.Type.UUID).setNotNull(true).setDefault(),
                    table.newColumn().setName("name").setType(Column.Type.VARCHAR).setNotNull(true).setDefault(),
                    table.newColumn().setName("token").setType(Column.Type.VARCHAR).setNotNull(true).setDefault(),
                    table.newColumn().setName("secret").setType(Column.Type.VARCHAR).setNotNull(true).setDefault(),
                    table.newColumn().setName("active").setType(Column.Type.BOOLEAN).setNotNull(true).setDefault(true)
            );
            sequence.create("netuno_client_id");

            table.create("netuno_client_hit",
                    table.newColumn().setName("id").setType(Column.Type.INT).setPrimaryKey(true),
                    table.newColumn().setName("uid").setType(Column.Type.UUID).setNotNull(true).setDefault(),
                    table.newColumn().setName("client_id").setType(Column.Type.INT).setNotNull(true).setDefault(),
                    table.newColumn().setName("user_id").setType(Column.Type.INT).setNotNull(true).setDefault(),
                    table.newColumn().setName("moment").setType(Column.Type.TIMESTAMP).setNotNull(true).setDefault()
            );
            index.create("netuno_client_hit", "client_id");
            index.create("netuno_client_hit", "user_id");
            sequence.create("netuno_client_hit_id");

            table.create("netuno_log",
                    table.newColumn().setName("id").setType(Column.Type.INT).setPrimaryKey(true),
                    table.newColumn().setName("uid").setType(Column.Type.UUID).setNotNull(true).setDefault(),
                    table.newColumn().setName("user_id").setType(Column.Type.INT).setNotNull(true).setDefault(),
                    table.newColumn().setName("group_id").setType(Column.Type.INT).setNotNull(true).setDefault(),
                    table.newColumn().setName("moment").setType(Column.Type.TIMESTAMP).setNotNull(true).setDefault(),
                    table.newColumn().setName("action").setType(Column.Type.INT).setNotNull(true).setDefault(),
                    table.newColumn().setName("table_id").setType(Column.Type.INT).setNotNull(true).setDefault(),
                    table.newColumn().setName("item_id").setType(Column.Type.INT).setNotNull(true).setDefault(),
                    table.newColumn().setName("data").setType(Column.Type.TEXT).setNotNull(true).setDefault()
            );
            index.create("netuno_log", "user_id");
            index.create("netuno_log", "group_id");
            index.create("netuno_log", "table_id");
            index.create("netuno_log", "item_id");
            sequence.create("netuno_log_id");

            /*
            table.create("netuno_jwt_app",
                    table.newColumn().setName("id").setType(Column.Type.INT).setPrimaryKey(true),
                    table.newColumn().setName("uid").setType(Column.Type.UUID).setNotNull(true).setDefault(),
                    table.newColumn().setName("name").setType(Column.Type.VARCHAR).setNotNull(true).setDefault(),
                    table.newColumn().setName("key").setType(Column.Type.VARCHAR).setNotNull(true).setDefault(),
                    table.newColumn().setName("secret").setType(Column.Type.VARCHAR).setNotNull(true).setDefault(),
                    table.newColumn().setName("callback_url").setType(Column.Type.VARCHAR).setNotNull(true).setDefault(),
                    table.newColumn().setName("return_url").setType(Column.Type.VARCHAR).setNotNull(true).setDefault(),
                    table.newColumn().setName("created").setType(Column.Type.TIMESTAMP).setNotNull(true).setDefault(true),
                    table.newColumn().setName("active").setType(Column.Type.BOOLEAN).setNotNull(true).setDefault(true)
            );
            sequence.create("netuno_jwt_app_id");

            table.create("netuno_jwt_app_code",
                    table.newColumn().setName("id").setType(Column.Type.INT).setPrimaryKey(true),
                    table.newColumn().setName("uid").setType(Column.Type.UUID).setNotNull(true).setDefault(),
                    table.newColumn().setName("app_id").setType(Column.Type.INT).setNotNull(true).setDefault(),
                    table.newColumn().setName("code").setType(Column.Type.VARCHAR).setNotNull(true).setDefault(),
                    table.newColumn().setName("created").setType(Column.Type.TIMESTAMP).setNotNull(true).setDefault(true),
                    table.newColumn().setName("active").setType(Column.Type.BOOLEAN).setNotNull(true).setDefault(true)
            );
            sequence.create("netuno_jwt_app_code_id");

            table.create("netuno_jwt_web_origin",
                    table.newColumn().setName("id").setType(Column.Type.INT).setPrimaryKey(true),
                    table.newColumn().setName("uid").setType(Column.Type.UUID).setNotNull(true).setDefault(),
                    table.newColumn().setName("app_id").setType(Column.Type.INT).setNotNull(true).setDefault(),
                    table.newColumn().setName("url").setType(Column.Type.VARCHAR).setNotNull(true).setDefault(),
                    table.newColumn().setName("created").setType(Column.Type.TIMESTAMP).setNotNull(true).setDefault(true),
                    table.newColumn().setName("active").setType(Column.Type.BOOLEAN).setNotNull(true).setDefault(true)
            );
            sequence.create("netuno_jwt_web_origin_id");
            */

            table.create("netuno_auth_jwt_token",
                    table.newColumn().setName("id").setType(Column.Type.INT).setPrimaryKey(true),
                    table.newColumn().setName("uid").setType(Column.Type.UUID).setNotNull(true).setDefault(),
                    //table.newColumn().setName("app_id").setType(Column.Type.INT).setNotNull(true).setDefault(),
                    //table.newColumn().setName("web_origin_id").setType(Column.Type.INT).setDefault(),
                    table.newColumn().setName("user_id").setType(Column.Type.INT).setDefault(),
                    table.newColumn().setName("short_token").setType(Column.Type.VARCHAR).setNotNull(true).setMaxLength(250).setDefault(),
                    table.newColumn().setName("access_token").setType(Column.Type.TEXT).setNotNull(true).setDefault(),
                    table.newColumn().setName("refresh_token").setType(Column.Type.TEXT).setDefault(),
                    table.newColumn().setName("created").setType(Column.Type.TIMESTAMP).setNotNull(true).setDefault(true),
                    table.newColumn().setName("access_expires").setType(Column.Type.TIMESTAMP).setNotNull(true).setDefault(true),
                    table.newColumn().setName("refresh_expires").setType(Column.Type.TIMESTAMP).setNotNull(true).setDefault(true),
                    table.newColumn().setName("active").setType(Column.Type.BOOLEAN).setNotNull(true).setDefault(true)
            );
            sequence.create("netuno_auth_jwt_token_id");

            table.create("netuno_query_history",
                    table.newColumn().setName("id").setType(Column.Type.INT).setPrimaryKey(true),
                    table.newColumn().setName("uid").setType(Column.Type.UUID).setNotNull(true).setDefault(),
                    table.newColumn().setName("moment").setType(Column.Type.TIMESTAMP).setNotNull(true).setDefault(),
                    table.newColumn().setName("command").setType(Column.Type.TEXT).setNotNull(true).setDefault(),
                    table.newColumn().setName("count").setType(Column.Type.INT).setNotNull(true).setDefault(),
                    table.newColumn().setName("time").setType(Column.Type.INT).setNotNull(true).setDefault()
            );
            sequence.create("netuno_query_history_id");

            table.create("netuno_query_stored",
                    table.newColumn().setName("id").setType(Column.Type.INT).setPrimaryKey(true),
                    table.newColumn().setName("uid").setType(Column.Type.UUID).setNotNull(true).setDefault(),
                    table.newColumn().setName("name").setType(Column.Type.VARCHAR).setNotNull(true).setDefault(),
                    table.newColumn().setName("moment").setType(Column.Type.TIMESTAMP).setNotNull(true).setDefault(),
                    table.newColumn().setName("command").setType(Column.Type.TEXT).setNotNull(true).setDefault()
            );
            sequence.create("netuno_query_stored_id");

            table.create("netuno_statistic_type",
                    table.newColumn().setName("id").setType(Column.Type.INT).setPrimaryKey(true),
                    table.newColumn().setName("uid").setType(Column.Type.UUID).setNotNull(true).setDefault(),
                    table.newColumn().setName("code").setType(Column.Type.VARCHAR).setNotNull(true).setDefault()
            );
            sequence.create("netuno_statistic_type_id");

            table.create("netuno_statistic_moment",
                    table.newColumn().setName("id").setType(Column.Type.INT).setPrimaryKey(true),
                    table.newColumn().setName("uid").setType(Column.Type.UUID).setNotNull(true).setDefault(),
                    table.newColumn().setName("type_id").setType(Column.Type.INT).setNotNull(true).setDefault(),
                    table.newColumn().setName("moment").setType(Column.Type.TIMESTAMP).setNotNull(true).setDefault(),
                    table.newColumn().setName("count").setType(Column.Type.INT).setNotNull(true).setDefault()
            );
            index.create("netuno_statistic_moment", "type_id");
            sequence.create("netuno_statistic_moment_id");

            table.create("netuno_statistic_average_type",
                    table.newColumn().setName("id").setType(Column.Type.INT).setPrimaryKey(true),
                    table.newColumn().setName("uid").setType(Column.Type.UUID).setNotNull(true).setDefault(),
                    table.newColumn().setName("code").setType(Column.Type.VARCHAR).setNotNull(true).setDefault()
            );
            sequence.create("netuno_statistic_average_type_id");

            table.create("netuno_statistic_average",
                    table.newColumn().setName("id").setType(Column.Type.INT).setPrimaryKey(true),
                    table.newColumn().setName("uid").setType(Column.Type.UUID).setNotNull(true).setDefault(),
                    table.newColumn().setName("type_id").setType(Column.Type.INT).setNotNull(true).setDefault(),
                    table.newColumn().setName("average_type_id").setType(Column.Type.INT).setNotNull(true).setDefault(),
                    table.newColumn().setName("moment").setType(Column.Type.TIMESTAMP).setNotNull(true).setDefault(),
                    table.newColumn().setName("average").setType(Column.Type.INT).setNotNull(true).setDefault()
            );
            index.create("netuno_statistic_average", "type_id");
            sequence.create("netuno_statistic_average_id");

            getBuilder().createApp(
                    new Values()
                            .set("name", getProteu().getConfig().getString("_app"))
            );
            
            Values groupDev = getBuilder().getGroupByNetuno("-2");
            if (groupDev == null) {
                int groupDevId = getBuilder().insertGroup("Developer", "-2", "1", "", "", "1");
                groupDev = new Values().set("id", groupDevId);
            }
            if (getBuilder().getUser("dev") == null) {
                getBuilder().insertUser("Developer", "dev", Config.getPasswordBuilder(getProteu()).getCryptPassword(getProteu(), getHili(), "dev", "dev"), getBuilder().booleanFalse(), "", "", groupDev.getString("id"), getBuilder().booleanTrue());
            }
            Values groupAdmin = getBuilder().getGroupByNetuno("-1");
            if (groupAdmin == null) {
                int groupAdminId = getBuilder().insertGroup("Administrator", "-1", "1", "", "", "1");
                groupAdmin = new Values().set("id", groupAdminId);
            }
            if (getBuilder().getUser("admin") == null) {
                getBuilder().insertUser("Administrator", "admin", Config.getPasswordBuilder(getProteu()).getCryptPassword(getProteu(), getHili(), "admin", "admin"), getBuilder().booleanFalse(), "", "", groupAdmin.getString("id"), getBuilder().booleanTrue());
            }
            if (checkExists.column("netuno_design", "search")) {
                getExecutor().execute(
                        "alter table netuno_design add column whenresult boolean default true;" +
                        "alter table netuno_design add column whenfilter boolean default true;" +
                        "alter table netuno_design add column whenedit boolean default true;" +
                        "alter table netuno_design add column whenview boolean default true;" +
                        "alter table netuno_design add column whennew boolean default true;" +
                        "update netuno_design set whenresult = false, whenfilter = false where search = false;" +
                        "alter table netuno_design drop column search;");
            }
            getExecutor().execute("update netuno_design set type = 'user' where type = 'tritaouser'");
            getExecutor().execute("update netuno_design set type = 'group' where type = 'tritaogroup'");
            if (!checkExists.column("netuno_table", "show_id")) {
                table.create(
                        "netuno_table",
                        table.newColumn().setName("show_id").setType(Column.Type.BOOLEAN).setDefault(true)
                );
            }
            if (!checkExists.column("netuno_table", "reorder")) {
                table.create(
                        "netuno_table",
                        table.newColumn().setName("reorder").setType(Column.Type.INT).setNotNull(true).setDefault()
                );
            }
            if (!checkExists.column("netuno_table", "description")) {
                table.create(
                        "netuno_table",
                        table.newColumn().setName("description").setType(Column.Type.TEXT)
                );
            }
            if (!checkExists.column("netuno_design", "description")) {
                table.create(
                        "netuno_design",
                        table.newColumn().setName("description").setType(Column.Type.TEXT)
                );
            }
            if (!checkExists.column("netuno_group", "login_allowed")) {
                table.create(
                        "netuno_group",
                        table.newColumn().setName("login_allowed").setType(Column.Type.BOOLEAN).setDefault(true)
                );
            }
            List<Values> formTables = getExecutor().query("select * from netuno_table where report = "+ getBuilder().booleanFalse());
            for (Values formTable : formTables) {
                if (!checkExists.column(formTable.getString("name"), "uid")) {
                    table.create(
                            formTable.getString("name"),
                            table.newColumn().setName("uid").setType(Column.Type.UUID).setNotNull(true).setDefault()
                    );
                }
            }
        } catch (Throwable e) {
            if (isPostgreSQL() && e.getMessage() != null && e.getMessage().contains("ERROR: function uuid_generate_v4() does not exist")) {
                throw new DBError("\n"
                        + "# "+ EmojiParser.parseToUnicode(":skull_crossbones:") +" Setting up " + Config.getDBKey(getProteu(), getKey()) +" fails because the uuid_generate_v4() does not exists!\n\n"
                        +"\tPlease execute this command below in your database before start the Netuno Server:\n\n"
                        +"\tCREATE EXTENSION IF NOT EXISTS \"uuid-ossp\";\n")
                        .setLogFatal(true);
            } else {
                throw new DBError(e).setLogFatal("Setting up " + Config.getDBKey(getProteu(), getKey()));
            }
        }
    }
}
