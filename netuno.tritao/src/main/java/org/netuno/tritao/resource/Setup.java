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

package org.netuno.tritao.resource;

import groovy.json.StringEscapeUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.netuno.library.doc.LanguageDoc;
import org.netuno.library.doc.LibraryDoc;
import org.netuno.library.doc.LibraryTranslationDoc;
import org.netuno.proteu.Proteu;
import org.netuno.psamata.Values;
import org.netuno.psamata.io.OutputStream;
import org.netuno.tritao.config.Config;
import org.netuno.tritao.hili.Hili;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;
import org.netuno.tritao.resource.event.AppEvent;
import org.netuno.tritao.resource.event.AppEventType;

/**
 * Setup - Resource
 * @author Eduardo Fonseca Velasques - @eduveks
 */
@Resource(name = "setup")
@LibraryDoc(translations = {
    @LibraryTranslationDoc(
            language = LanguageDoc.PT,
            title = "Setup",
            introduction = "Recurso de configuração dos componentes da aplicação.",
            howToUse = {}
    )
})
public class Setup extends ResourceBase {

    private static org.apache.logging.log4j.Logger logger = LogManager.getLogger(Setup.class);

    private static boolean running = false;

    public Setup(Proteu proteu, Hili hili) {
        super(proteu, hili);
    }
    
    @AppEvent(type=AppEventType.BeforeEnvironment)
    private void beforeEnvironment() {
        getProteu().getConfig().set("_setup", getProteu().getConfig().getValues("_app:config").getValues("setup"));
    }

    public boolean running() {
        return running;
    }

    public boolean isRunning() {
        return running;
    }

    public void run() {
        if (running) {
            return;
        }
        running = true;
        getProteu().getConfig().set("_setup:running", true);
        try {
            Values setupConfig = getProteu().getConfig().asValues("_setup");
            if (setupConfig != null && !setupConfig.getBoolean("enabled", true)) {
                return;
            }
            getHili().sandbox().runScript(Config.getPathAppSetup(getProteu()), "_start");
            Config.getDataBaseBuilder(getProteu(), "default").setup();
            if (setupConfig == null
                    || setupConfig.getValues("schema") == null
                    || setupConfig.getValues("schema").getBoolean("execution", true)) {
                try (Stream<Path> files = Files.list(Paths.get(Config.getPathAppSetup(getProteu())))) {
                    files.sorted().forEach(
                            (f) -> {
                                String fileName = FilenameUtils.removeExtension(f.getFileName().toString());
                                if (fileName.startsWith("_schema-")) {
                                    getHili().sandbox().runScript(Config.getPathAppSetup(getProteu()), fileName);
                                }
                            }
                    );
                } catch (IOException e) {
                    logger.fatal("When looking for setup schema scripts into the folder: " + Config.getPathAppSetup(getProteu()), e);
                }
                try (Stream<Path> files = Files.list(Paths.get(Config.getPathAppSetup(getProteu())))) {
                    files.sorted().forEach(
                            (f) -> {
                                String fileName = FilenameUtils.removeExtension(f.getFileName().toString());
                                if (fileName.startsWith("_data-")) {
                                    getHili().sandbox().runScript(Config.getPathAppSetup(getProteu()), fileName);
                                }
                            }
                    );
                } catch (IOException e) {
                    logger.fatal("When looking for setup data scripts into the folder: " + Config.getPathAppSetup(getProteu()), e);
                }
            }
            if (setupConfig == null
                    || setupConfig.getValues("scripts") == null
                    || setupConfig.getValues("scripts").getBoolean("execution", true)) {
                try (Stream<Path> files = Files.list(Paths.get(Config.getPathAppSetup(getProteu())))) {
                    files.sorted().forEach(
                            (f) -> {
                                String fileName = FilenameUtils.removeExtension(f.getFileName().toString());
                                if (!fileName.startsWith("_")) {
                                    getHili().sandbox().runScript(Config.getPathAppSetup(getProteu()), fileName);
                                }
                            }
                    );
                } catch (IOException e) {
                    logger.fatal("When looking for setup scripts into the folder: " + Config.getPathAppSetup(getProteu()), e);
                }
            }
            getHili().sandbox().runScript(Config.getPathAppSetup(getProteu()), "_end");
        } finally {
            getProteu().getConfig().set("_setup:running", false);
            running = false;
        }
    }

    public boolean autoCreateSchema() {
        if (running) {
            return false;
        }
        Values setupConfig = getProteu().getConfig().asValues("_setup");
        if (setupConfig == null) {
            return false;
        }
        Values schema = setupConfig.getValues("schema");
        if (schema == null || schema.getBoolean("auto_create", true)) {
            createSchema();
            return true;
        }
        return false;
    }

    public boolean createSchema() {
        if (running) {
            return false;
        }
        try (Stream<Path> files = Files.list(Paths.get(Config.getPathAppSetup(getProteu())))) {
            files.sorted().forEach(
                (f) -> {
                    String fileName = FilenameUtils.removeExtension(f.getFileName().toString());
                    if (fileName.startsWith("_schema-")) {
                        try {
                            Files.delete(f);
                        } catch (IOException e) {
                            logger.fatal("When trying to delete the file: " + f.toString(), e);
                        }
                    }
                }
            );
        } catch (IOException e) {
            logger.fatal("When looking for setup scripts into the folder: " + Config.getPathAppSetup(getProteu()), e);
        }
        Group group = resource(Group.class);
        User user = resource(User.class);
        Form form = new Form(getProteu(), getHili());
        List<Values> forms = form.all();
        for (Values _fParent : forms) {
            for (Values _f : forms) {
                if (_f.getInt("parent_id") == _fParent.getInt("id")) {
                    _f.set("parent_uid", _fParent.getString("uid"));
                }
            }
        }
        List<String> formUids = new ArrayList<>();
        int formsPadSize = Integer.toString(forms.size()).length();
        for (int i = 0; i < forms.size(); i++) {
            for (Values _f : forms) {
                if ((!_f.getString("parent_uid").isEmpty() && !formUids.contains(_f.getString("parent_uid")))
                        || formUids.contains(_f.getString("uid"))) {
                    continue;
                }
                formUids.add(_f.getString("uid"));
                int id = _f.getInt("id");
                Values f = new Values(_f);
                Values formUser = user.get(f.getInt("user_id"));
                if (formUser != null) {
                    f.set("user_uid", formUser.getString("uid"));
                }
                Values formGroup = group.get(f.getInt("group_id"));
                if (formGroup != null) {
                    f.set("group_uid", formGroup.getString("uid"));
                }
                f.unset("id");
                f.unset("parent_id");
                f.unset("user_id");
                f.unset("group_id");
                StringBuilder code = new StringBuilder();
                commentCodeWithAutoGenerated(code);
                code.append("_form.createIfNotExists(\n");
                code.append(valuesToCode(f));
                code.append(")\n");
                for (Values c : form.getAllComponents(id)) {
                    Values fieldViewUser = user.get(c.getInt("view_user_id"));
                    if (fieldViewUser != null) {
                        c.set("view_user_uid", fieldViewUser.getString("uid"));
                    }
                    Values fieldViewGroup = group.get(c.getInt("view_group_id"));
                    if (fieldViewGroup != null) {
                        c.set("view_group_uid", fieldViewGroup.getString("uid"));
                    }
                    Values fieldEditUser = user.get(c.getInt("edit_user_id"));
                    if (fieldEditUser != null) {
                        c.set("edit_user_uid", fieldEditUser.getString("uid"));
                    }
                    Values fieldEditGroup = group.get(c.getInt("edit_group_id"));
                    if (fieldEditGroup != null) {
                        c.set("edit_group_uid", fieldEditGroup.getString("uid"));
                    }
                    c.unset("id");
                    c.unset("table_id");
                    c.unset("view_user_id");
                    c.unset("view_group_id");
                    c.unset("edit_user_id");
                    c.unset("edit_group_id");
                    code.append("_form.createComponentIfNotExists(\n");
                    code.append("\t" + escapeToCodeString(f.getString("uid")) + ",\n");
                    code.append(valuesToCode(c));
                    code.append(")\n");
                }
                String file = Config.getPathAppSetup(getProteu()) + File.separator
                        + "_schema-form-" + StringUtils.leftPad(Integer.toString(formUids.size()), formsPadSize, "0") + "-" + f.getString("name")
                        + ".js";
                try {
                    OutputStream.writeToFile(
                            code.toString(),
                            file,
                            false);
                } catch (IOException e) {
                    logger.fatal("Generating the schema into " + file, e);
                }
            }
            if (formUids.size() == forms.size()) {
                break;
            }
        }
        Report report = new Report(getProteu(), getHili());
        List<Values> reports = report.all();
        for (Values _rParent : reports) {
            for (Values _r : reports) {
                if (_r.getInt("parent_id") == _rParent.getInt("id")) {
                    _r.set("parent_uid", _rParent.getString("uid"));
                }
            }
        }
        List<String> reportUids = new ArrayList<>();
        int reportsPadSize = Integer.toString(reports.size()).length();
        for (int i = 0; i < reports.size(); i++) {
            for (Values _r : reports) {
                if ((!_r.getString("parent_uid").isEmpty() && !reportUids.contains(_r.getString("parent_uid")))
                        || reportUids.contains(_r.getString("uid"))) {
                    continue;
                }
                reportUids.add(_r.getString("uid"));
                int id = _r.getInt("id");
                Values r = new Values(_r);
                r.unset("id");
                r.unset("parent_id");
                StringBuilder code = new StringBuilder();
                commentCodeWithAutoGenerated(code);
                code.append("_report.createIfNotExists(\n");
                code.append(valuesToCode(r));
                code.append(")\n");
                for (Values c : report.getAllComponents(id)) {
                    c.unset("id");
                    c.unset("table_id");
                    code.append("_report.createComponentIfNotExists(\n");
                    code.append("\t" + escapeToCodeString(r.getString("uid")) + ",\n");
                    code.append(valuesToCode(c));
                    code.append(")\n");
                }
                String file = Config.getPathAppSetup(getProteu()) + File.separator
                        + "_schema-report-" + StringUtils.leftPad(Integer.toString(reportUids.size()), reportsPadSize, "0") + "-" + r.getString("name")
                        + ".js";
                try {
                    OutputStream.writeToFile(
                            code.toString(),
                            file,
                            false);
                } catch (IOException e) {
                    logger.fatal("Generating the schema into " + file, e);
                }
            }
        }
        return false;
    }

    private void commentCodeWithAutoGenerated(StringBuilder code) {
        code.append("/**\n");
        code.append("  *\n");
        code.append("  *  CODE GENERATED AUTOMATICALLY\n");
        code.append("  *\n");
        code.append("  *  THIS FILE SHOULD NOT BE EDITED BY HAND\n");
        code.append("  *\n");
        code.append("  */\n\n");
    }

    private String valuesToCode(Values data) {
        String code = "\t_val.init()\n";
        for (String key : data.keys()) {
            Object value = data.get(key);
            code += "\t.set(\"" + StringEscapeUtils.escapeJava(key) + "\", " + escapeToCodeString(value) + ")\n";
        }
        return code;
    }

    private String escapeToCodeString(Object value) {
        if (value instanceof Boolean) {
            return value.toString();
        } else if (value instanceof Integer) {
            return value.toString();
        } else if (value instanceof Short) {
            return value.toString();
        } else if (value instanceof Long) {
            return value.toString();
        } else if (value instanceof Float) {
            return value.toString();
        } else if (value instanceof Double) {
            return value.toString();
        } else {
            return "\"" + StringEscapeUtils.escapeJava(value.toString()) + "\"";
        }
    }
}
