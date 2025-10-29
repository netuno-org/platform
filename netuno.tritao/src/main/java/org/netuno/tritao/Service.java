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

package org.netuno.tritao;

import com.vdurmont.emoji.EmojiParser;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.netuno.library.doc.*;
import org.netuno.proteu.Proteu;
import org.netuno.proteu.ProteuException;
import org.netuno.psamata.Values;
import org.netuno.psamata.script.ScriptRunner;
import org.netuno.tritao.auth.Auth;
import org.netuno.tritao.config.Config;
import org.netuno.tritao.event.EventId;
import org.netuno.tritao.hili.Hili;
import org.netuno.tritao.hili.HiliError;

import java.io.*;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;

import org.netuno.tritao.openapi.Schema;
import org.netuno.tritao.auth.providers.HandlerProviders;
import org.netuno.tritao.resource.event.AppEventType;
import org.netuno.tritao.resource.event.EventExecutor;
import org.netuno.tritao.util.TemplateBuilder;

/**
 * Execution of the services scripts.
 * @author Eduardo Fonseca Velasques - @eduveks
 */
@LibraryDoc(translations = {
        @LibraryTranslationDoc(
                language= LanguageDoc.PT,
                title = "Service",
                introduction = "Gere a execução dos scripts de serviços em `server/services`.",
                howToUse = { }
        ),
        @LibraryTranslationDoc(
                language= LanguageDoc.EN,
                title = "Service",
                introduction = "Manages the execution of the services scripts in `server/services`.",
                howToUse = { }
        )
})
public class Service {
    
    public static final String[] METHODS = new String[] {
        "delete", "get", "head", "options", "post", "put", 
        "trace", "copy", "link", "unlink", "patch", "purge",
        "lock", "unlock", "propfind", "view"
    }; 

    private static final Logger logger = LogManager.getLogger(Service.class);
    private Proteu proteu = null;
    private Hili hili = null;
    public String method = "";
    public String path = "";
    public Boolean generatingOpenAPIDefinition = false;
    public Boolean allowed = false;
    public Boolean denied = false;
    public Boolean cancelled = false;
    public Values errorData = null;
    
    public List<String> methods = Arrays.asList(METHODS);
    
    public Service(Proteu proteu, Hili hili) {
        this.proteu = proteu;
        this.hili = hili;
        path = proteu.getConfig().getString("_service:path");
        /*if (!name.matches("[A-Za-z0-9-_]*")) {
			logger.fatal("Invalid Service Name: "+ name);
			name = "";
			cancel();
			return;
		}*/

        hili.sandbox().bind("service", this);
    }

    public List<String> getMethods() {
        return methods;
    }

    public String getMethod() {
        return method;
    }

    public String method() {
        return method;
    }

    public boolean isMethod() {
        return method != null && !method.isEmpty();
    }

    public String getPath() {
        return path;
    }

    public String path() {
        return path;
    }

    public boolean isGeneratingOpenAPIDefinition() {
        return this.generatingOpenAPIDefinition;
    }

    public Boolean wasCancelled() {
        return cancelled;
    }

    public void cancel() {
        this.cancelled = true;
    }

    public void allow() {
        this.allowed = true;
        this.denied = false;
    }

    public void deny() {
        this.allowed = false;
        this.denied = true;
    }

    public boolean isAllowed() {
        return this.allowed;
    }

    public boolean isDenied() {
        return this.denied;
    }

    @IgnoreDoc
    public static void _main(Proteu proteu, Hili hili) throws Exception {
        Service service = new Service(proteu, hili);
        proteu.getConfig().set("_service:not-found:default-error", true);
        proteu.getConfig().set("_service:instance", service);
        try {
            String method = proteu.getRequestHeader().getString("Method").toLowerCase();
            if (method.equalsIgnoreCase("options")) {
                service.allow();
            }
            String scriptPathMethod = ScriptRunner.searchScriptFile(Config.getPathAppServices(proteu) + "/" + service.getPath() + "." + method);
            if (scriptPathMethod != null) {
                service.method = method;
                service.path = service.getPath() + "." + method;
            } else {
                scriptPathMethod = ScriptRunner.searchScriptFile(Config.getPathAppServices(proteu) + "/" + service.getPath() + "/" + method);
                if (scriptPathMethod != null) {
                    service.method = method;
                    service.path = service.getPath() + "/" + method;
                }
            }

            Schema schema = new Schema(service, proteu, hili);
            if (service.path.equals("_openapi.json")) {
                service.generatingOpenAPIDefinition = true;
                schema.run();
                return;
            } else if (service.path.equals("_openapi")) {
                TemplateBuilder.outputWidget(proteu, hili, "openapi/build/index", null);
                return;
            }

            if (!schema.validateSchemaIn()) {
                proteu.responseHTTPError(Proteu.HTTPStatus.BadRequest400, hili);
                service.defaultEmptyOutput();
                return;
            }
            if (service.getPath().startsWith("_auth_provider/")
                    || service.getPath().equalsIgnoreCase("_auth")
                    || Auth.isAuthenticated(proteu, hili)) {
                service.allow();
            }
            hili.event().run(EventId.SERVICE_CONFIG_BEFORE);
            EventExecutor.getInstance(proteu).runAppEvent(AppEventType.BeforeServiceConfiguration);
            hili.event().run(EventId.SERVICE_CONFIG);
            hili.event().run(EventId.SERVICE_CONFIG_SCRIPT_BEFORE);
            if (service.core("_service_config")) {
                hili.event().run(EventId.SERVICE_CONFIG_SCRIPT_AFTER);
                EventExecutor.getInstance(proteu).runAppEvent(AppEventType.AfterServiceConfiguration);
                hili.event().run(EventId.SERVICE_CONFIG_AFTER);
                if (service.wasCancelled()) {
                    if (proteu.isResponseHeaderStatusOk()) {
                        proteu.responseHTTPError(Proteu.HTTPStatus.ServiceUnavailable503, hili);
                    }
                    service.defaultEmptyOutput();
                    return;
                } else if (service.isDenied() || !service.isAllowed()) {
                    if (proteu.isResponseHeaderStatusOk()) {
                        proteu.responseHTTPError(Proteu.HTTPStatus.Unauthorized401, hili);
                    }
                    service.defaultEmptyOutput();
                    return;
                }
                if (!service.isMethod() && method.equalsIgnoreCase("options")) {
                    EventExecutor.getInstance(proteu).runAppEvent(AppEventType.ServiceOptionsMethodAutoReply);
                    return;
                }
                org.netuno.tritao.resource.Auth auth = hili.resource().get(org.netuno.tritao.resource.Auth.class);
                if (!service.isAllowed() && !auth.jwtToken().isEmpty() && !auth.jwtTokenCheck()) {
                    proteu.responseHTTPError(Proteu.HTTPStatus.Forbidden403, hili);
                    service.defaultEmptyOutput();
                    return;
                }
                hili.event().run(EventId.SERVICE_START_BEFORE);
                EventExecutor.getInstance(proteu).runAppEvent(AppEventType.BeforeServiceConfiguration);
                hili.event().run(EventId.SERVICE_START);
                hili.event().run(EventId.SERVICE_START_SCRIPT_BEFORE);
                service.core("_service_start");
                hili.event().run(EventId.SERVICE_START_SCRIPT_AFTER);
                EventExecutor.getInstance(proteu).runAppEvent(AppEventType.AfterServiceConfiguration);
                hili.event().run(EventId.SERVICE_START_AFTER);
                ByteArrayOutputStream outStream = null;
                if (schema.schemaOutExists()) {
                    outStream = new ByteArrayOutputStream();
                    proteu.getOutput().getMirrors().add(outStream);
                }
                if (service.getPath().startsWith("_auth_provider/")) {
                    HandlerProviders providers = new HandlerProviders(service, proteu, hili);
                    providers.run();
                } else {
                    service.execute(service.getPath());
                }
                if (outStream != null) {
                    schema.validateSchemaOut(outStream);
                }
                if (proteu.getConfig().getBoolean("_script:_service_end")) {
                    hili.event().run(EventId.SERVICE_END_BEFORE);
                    EventExecutor.getInstance(proteu).runAppEvent(AppEventType.BeforeServiceConfiguration);
                    hili.event().run(EventId.SERVICE_END);
                    hili.event().run(EventId.SERVICE_END_SCRIPT_BEFORE);
                    service.core("_service_end");
                    hili.event().run(EventId.SERVICE_END_SCRIPT_AFTER);
                    EventExecutor.getInstance(proteu).runAppEvent(AppEventType.AfterServiceConfiguration);
                    hili.event().run(EventId.SERVICE_END_AFTER);
                }
            }
        } catch (Throwable e) {
            proteu.responseHTTPError(Proteu.HTTPStatus.InternalServerError500, hili);
            if (e instanceof HiliError) {
                throw e;
            } else {
                throw new HiliError(proteu, hili, e).setLogFatal(true);
            }
            /*
			logger.trace(e);
			logger.fatal("\n\n" +
					"Service "+ service.getPath() +" Error: \n\t"+ e.getMessage() +
					"\n\n ");*/
        } finally {
            proteu.getConfig().unset("_service:instance");
        }
    }

    public boolean core(String file) {
        String scriptPath = ScriptRunner.searchScriptFile(Config.getPathAppCore(proteu) + "/" + file);
        if (scriptPath != null) {
            return hili.sandbox()
                    .runScript(Config.getPathAppCore(proteu), file)
                    .whenError((t) -> {
                        proteu.setResponseHeader(Proteu.HTTPStatus.InternalServerError500);
                        if (!file.equals("_service_error")) {
                            hili.event().run(EventId.SERVICE_ERROR_BEFORE, Values.newMap().set("error", t));
                            hili.event().run(EventId.SERVICE_ERROR);
                            hili.event().run(EventId.SERVICE_ERROR_SCRIPT_BEFORE, Values.newMap().set("error", t));
                            core("_service_error");
                            hili.event().run(EventId.SERVICE_ERROR_SCRIPT_AFTER, Values.newMap().set("error", t));
                            hili.event().run(EventId.SERVICE_ERROR_AFTER, Values.newMap().set("error", t));
                        }
                    })
                    .isSuccess();
        }
        proteu.responseHTTPError(Proteu.HTTPStatus.NotFound404, hili);
        logger.warn("\n"
                + "\n#"
                + "\n# " + EmojiParser.parseToUnicode(":sparkles:") + " "+ Config.getApp(proteu)
                + "\n#"
                + "\n# " + EmojiParser.parseToUnicode(":compass:") + " The core script was not found at: "
                + "\n#"
                + "\n# " + EmojiParser.parseToUnicode(":open_file_folder:") + " " + Config.getPathAppCore(proteu)
                + "\n# " + EmojiParser.parseToUnicode(":stop_sign:") + " " + file
                + "\n#"
                + "\n"
        );
        return false;
    }

    public boolean execute(String file) throws IOException, ProteuException {
        if (file.equalsIgnoreCase("_auth")) {
            new Auth(proteu, hili).run();
            return true;
        }
        String scriptPath = ScriptRunner.searchScriptFile(Config.getPathAppServices(proteu) + "/" + file);
        if (scriptPath != null) {
            return hili.sandbox()
                    .runScript(Config.getPathAppServices(proteu), file)
                    .whenError((t) -> {
                        proteu.setResponseHeader(Proteu.HTTPStatus.InternalServerError500);
                        try {
                            for (Function<Object[], Object> func : proteu.getConfig().getValues("_exec:service:onError", Values.newList()).list(Function.class)) {
                                func.apply(new Object[] {t, file});
                            }
                        } finally {
                            hili.event().run(EventId.SERVICE_ERROR_BEFORE, Values.newMap().set("error", t));
                            hili.event().run(EventId.SERVICE_ERROR);
                            hili.event().run(EventId.SERVICE_ERROR_SCRIPT_BEFORE, Values.newMap().set("error", t));
                            core("_service_error");
                            hili.event().run(EventId.SERVICE_ERROR_SCRIPT_AFTER, Values.newMap().set("error", t));
                            hili.event().run(EventId.SERVICE_ERROR_AFTER, Values.newMap().set("error", t));
                        }
                    })
                    .isSuccess();
        } else if (!file.equals("config")) {
            hili.event().run(EventId.SERVICE_NOT_FOUND_BEFORE);
            EventExecutor.getInstance(proteu).runAppEvent(AppEventType.BeforeServiceNotFound);
            if (isNotFoundDefaultError()) {
                proteu.responseHTTPError(Proteu.HTTPStatus.NotFound404, hili);
                logger.warn("\n"
                        + "\n#"
                        + "\n# " + EmojiParser.parseToUnicode(":sparkles:") + " "+ Config.getApp(proteu)
                        + "\n#"
                        + "\n# " + EmojiParser.parseToUnicode(":compass:") + " Service script was not found for the " + proteu.getRequestHeader().getString("Method").toUpperCase() + " method: "
                        + "\n#"
                        + "\n# " + EmojiParser.parseToUnicode(":open_file_folder:") + " " + Config.getPathAppServices(proteu)
                        + "\n# " + EmojiParser.parseToUnicode(":stop_sign:") + " " + file
                        + "\n#"
                        + "\n"
                );
            }
            hili.event().run(EventId.SERVICE_NOT_FOUND);
            EventExecutor.getInstance(proteu).runAppEvent(AppEventType.AfterServiceNotFound);
            hili.event().run(EventId.SERVICE_NOT_FOUND_AFTER);
            proteu.getConfig().unset("_service:not-found:default-error");
        }
        return false;
    }

    public boolean isNotFoundDefaultError() {
        return proteu.getConfig().getBoolean("_service:not-found:default-error");
    }

    public void setNotFoundDefaultError(boolean value) {
        proteu.getConfig().set("_service:not-found:default-error", value);
    }

    public boolean notFoundDefaultError() {
        return isNotFoundDefaultError();
    }

    public void notFoundDefaultError(boolean value) {
        setNotFoundDefaultError(value);
    }

    public void defaultEmptyOutput() throws IOException, ProteuException {
        if (proteu.isRequestJSON() && proteu.getOutput().isEmpty()) {
            proteu.outputJSON(new Values().forceMap());
        }
    }
    
    public static Service getInstance(Proteu proteu) {
        return (Service)proteu.getConfig().get("_service:instance");
    }

}
