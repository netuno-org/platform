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
import org.netuno.psamata.mail.SMTPTransport;
import org.netuno.psamata.script.ScriptRunner;
import org.netuno.tritao.config.Config;
import org.netuno.tritao.config.Hili;
import org.netuno.tritao.config.HiliError;

import java.io.*;
import java.util.Arrays;
import java.util.List;

import org.netuno.tritao.openapi.Schema;
import org.netuno.tritao.resource.Resource;
import org.netuno.tritao.resource.event.AppEventType;
import org.netuno.tritao.resource.event.EventExecutor;

/**
 * Execution of the services scripts.
 * @author Eduardo Fonseca Velasques - @eduveks
 */
@Resource(name = "service")
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

    private static Logger logger = LogManager.getLogger(Service.class);
    private Proteu proteu = null;
    private Hili hili = null;
    public String method = "";
    public String path = "";
    public Boolean generatingOpenAPIDefinition = false;
    public Boolean allowed = false;
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

        hili.bind("service", this);
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
    }

    public void deny() {
        this.allowed = false;
    }

    public boolean isAllowed() {
        return this.allowed;
    }

    public boolean isDenied() {
        return this.allowed == false;
    }

    @IgnoreDoc
    public static void _main(Proteu proteu, Hili hili) throws Exception {
        Service service = new Service(proteu, hili);
        proteu.getConfig().set("_service:not-found:default-error", true);
        proteu.getConfig().set("_service:instance", service);
        try {
            String method = proteu.getRequestHeader().getString("Method").toLowerCase();
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
            if (service.path.equals("_openapi")) {
                service.generatingOpenAPIDefinition = true;
                schema.run();
                return;
            }
            if (!schema.validateSchemaIn()) {
                proteu.responseHTTPError(Proteu.HTTPStatus.BadRequest400, hili);
                return;
            }
            if (service.getPath().equalsIgnoreCase("_auth")) {
                service.allow();
            }
            EventExecutor.getInstance(proteu).runAppEvent(AppEventType.BeforeServiceConfiguration);
            if (service.core("_service_config")) {
                EventExecutor.getInstance(proteu).runAppEvent(AppEventType.AfterServiceConfiguration);
                if (!service.wasCancelled()
                        && !service.isAllowed()
                        && !Auth.isAuthenticated(proteu, hili)) {
                    if (proteu.getResponseHeaderStatus() == Proteu.HTTPStatus.OK200) {
                        proteu.responseHTTPError(Proteu.HTTPStatus.Forbidden403, hili);
                    }
                    return;
                }
                if (service.wasCancelled()) {
                    if (proteu.getResponseHeaderStatus() == Proteu.HTTPStatus.OK200) {
                        proteu.responseHTTPError(Proteu.HTTPStatus.ServiceUnavailable503, hili);
                    }
                    return;
                }
                EventExecutor.getInstance(proteu).runAppEvent(AppEventType.BeforeServiceConfiguration);
                service.core("_service_start");
                EventExecutor.getInstance(proteu).runAppEvent(AppEventType.AfterServiceConfiguration);
                ByteArrayOutputStream outStream = null;
                if (schema.schemaOutExists()) {
                    outStream = new ByteArrayOutputStream();
                    proteu.getOutput().getMirrors().add(outStream);
                }
                service.execute(service.getPath());
                if (outStream != null) {
                    schema.validateSchemaOut(outStream);
                }
                if (proteu.getConfig().getBoolean("_script:_service_end")) {
                    EventExecutor.getInstance(proteu).runAppEvent(AppEventType.BeforeServiceConfiguration);
                    service.core("_service_end");
                    EventExecutor.getInstance(proteu).runAppEvent(AppEventType.AfterServiceConfiguration);
                }
            }
        } catch (Throwable e) {
            proteu.responseHTTPError(Proteu.HTTPStatus.InternalServerError500, hili);
            if (e instanceof HiliError) {
                throw e;
            } else {
                throw new HiliError(e).setLogFatal(true);
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
            if (hili.runScriptSandbox(Config.getPathAppCore(proteu), file) == null) {
                if (!file.equals("_service_error")) {
                    core("_service_error");
                }
                return false;
            }
            return true;
        } else {
            proteu.responseHTTPError(Proteu.HTTPStatus.NotFound404, hili);
            logger.warn("\n"
                    + "\n#"
                    + "\n# Core script not found: "
                    + "\n#"
                    + "\n# " + Config.getPathAppCore(proteu)
                    + "\n# " + file
                    + "\n#"
                    + "\n"
            );
        }
        return false;
    }

    public boolean execute(String file) throws IOException, ProteuException {
        if (file.equalsIgnoreCase("_auth")) {
            new Auth(proteu, hili).run();
            return true;
        }
        String scriptPath = ScriptRunner.searchScriptFile(Config.getPathAppServices(proteu) + "/" + file);
        if (scriptPath != null) {
            if (hili.runScriptSandbox(Config.getPathAppServices(proteu), file) == null) {
                core("_service_error");
                return false;
            }
            return true;
        } else if (!file.equals("config")) {
            EventExecutor.getInstance(proteu).runAppEvent(AppEventType.BeforeServiceNotFound);
            if (isNotFoundDefaultError()) {
                proteu.responseHTTPError(Proteu.HTTPStatus.NotFound404, hili);
                logger.warn("\n"
                        + "\n#"
                        + "\n# " + EmojiParser.parseToUnicode(":compass:") + " Service not found for " + proteu.getRequestHeader().getString("Method").toUpperCase() + " method: "
                        + "\n#"
                        + "\n# " + Config.getPathAppServices(proteu)
                        + "\n# " + file
                        + "\n#"
                        + "\n"
                );
            }
            EventExecutor.getInstance(proteu).runAppEvent(AppEventType.AfterServiceNotFound);
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
    
    public static Service getInstance(Proteu proteu) {
        return (Service)proteu.getConfig().get("_service:instance");
    }
    
    @Override
    protected void finalize() throws Throwable {
        hili.unbind("service");
    }
}
