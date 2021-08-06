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
import org.apache.commons.io.FilenameUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.leadpony.justify.api.JsonSchema;
import org.leadpony.justify.api.JsonValidationService;
import org.leadpony.justify.api.ProblemHandler;
import org.netuno.proteu.Proteu;
import org.netuno.proteu.ProteuException;
import org.netuno.psamata.Values;
import org.netuno.psamata.io.InputStream;
import org.netuno.psamata.mail.SMTPTransport;
import org.netuno.psamata.script.ScriptRunner;
import org.netuno.tritao.config.Config;
import org.netuno.tritao.config.Hili;
import org.netuno.tritao.config.HiliError;

import jakarta.json.JsonReader;
import jakarta.json.JsonValue;
import java.io.*;
import java.nio.file.*;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.netuno.tritao.resource.event.AppEventType;
import org.netuno.tritao.resource.event.EventExecutor;

/**
 * Script Services
 * @author Eduardo Fonseca Velasques - @eduveks
 */
public class Service {
    
    private static String[] METHODS = new String[] {
        "delete", "get", "head", "options", "post", "put", 
        "trace", "copy", "link", "unlink", "patch", "purge",
        "lock", "unlock", "propfind", "view"
    }; 

    private static Logger logger = LogManager.getLogger(Service.class);
    private Proteu proteu = null;
    private Hili hili = null;
    public String method = "";
    public String path = "";
    public Boolean allowed = false;
    public Boolean cancelled = false;
    public Boolean outValidation = true;
    public Values errorData = null;
    private String validateSchemaInProblems = "";
    private String validateSchemaOutProblems = "";
    
    private List<String> methods = Arrays.asList(METHODS);
    
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

    public SMTPTransport newSMTPTransport() {
        return new SMTPTransport();
    }

    public void print(String output) {
        System.out.println(output);
    }

    public void println(String output) {
        System.out.println(Config.getApp(proteu) + " # " + output);
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

    public Boolean isOutValidation() {
        return outValidation;
    }

    public Service setOutValidation(Boolean outValidation) {
        this.outValidation = outValidation;
        return this;
    }

    private void validateSchemaInProblem(String problem) {
        if (!validateSchemaInProblems.isEmpty()) {
            validateSchemaInProblems += "\n";
        }
        validateSchemaInProblems += "#     " + problem;
    }

    public Path schemaInPath() {
        return Paths.get(Config.getPathAppServices(proteu) + "/" + path + ".in.json");
    }

    public boolean schemaInExists() {
        return Files.exists(schemaInPath());
    }

    public boolean validateSchemaIn() {
        JsonValidationService service = JsonValidationService.newInstance();
        Path pathSchema = schemaInPath();
        if (Files.exists(pathSchema)) {
            try {
                JsonSchema schema = service.readSchema(loadSchemaAsInputStream(pathSchema));
                ProblemHandler handler = service.createProblemPrinter(this::validateSchemaInProblem);
                Values data = new Values();
                data.merge(proteu.getRequestGet());
                data.merge(proteu.getRequestPost());
                try (JsonReader reader = service.createReader(new StringReader(data.toJSON()), schema, handler)) {
                    JsonValue value = reader.readValue();
                    if (validateSchemaInProblems.isEmpty()) {
                        return true;
                    }
                    logger.warn("\n\n#\n# "+ EmojiParser.parseToUnicode(":crossed_swords:") +" Invalid request to service " + path + "\n#\n"
                            + validateSchemaInProblems + "\n#\n");
                    return false;
                }
            } catch (Exception e) {
                validationSchemaError(pathSchema, e);
                return false;
            }
        } else {
            return true;
        }
    }

    private void validateSchemaOutProblem(String problem) {
        if (!validateSchemaOutProblems.isEmpty()) {
            validateSchemaOutProblems += "\n";
        }
        validateSchemaOutProblems += "#     " + problem;
    }

    public Path schemaOutPath() {
        Path outWithCode = schemaOutPath(proteu.getResponseHeaderStatus().getCode());
        if (Files.exists(outWithCode)) {
            return outWithCode;
        }
        return Paths.get(Config.getPathAppServices(proteu) + "/" + path + ".out.json");
    }

    public Path schemaOutPath(int httpCode) {
        return Paths.get(Config.getPathAppServices(proteu) + "/" + path + ".out." + httpCode + ".json");
    }

    public boolean schemaOutExists() {
        if (Files.exists(schemaOutPath())) {
            return true;
        }
        for (Proteu.HTTPStatus httpStatus : Proteu.HTTPStatus.values()) {
            if (Files.exists(schemaOutPath(httpStatus.getCode()))) {
                return true;
            }
        }
        return false;
    }

    public boolean validateSchemaOut(ByteArrayOutputStream outStream) {
        if (outValidation == null || outValidation == false) {
            return true;
        }
        JsonValidationService service = JsonValidationService.newInstance();
        Path pathSchema = schemaOutPath();
        if (Files.exists(pathSchema)) {
            try {
                Values values = Values.fromJSON(InputStream.readAll(loadSchemaAsInputStream(pathSchema)));
                if (!values.hasKey("type")) {
                    return true;
                }
                JsonSchema schema = service.readSchema(loadSchemaAsInputStream(pathSchema));
                ProblemHandler handler = service.createProblemPrinter(this::validateSchemaOutProblem);
                String outContent = new String(outStream.toByteArray());
                try ( JsonReader reader = service.createReader(new StringReader(outContent), schema, handler)) {
                    JsonValue value = reader.readValue();
                    if (validateSchemaOutProblems.isEmpty()) {
                        return true;
                    }
                    logger.warn("\n\n#\n# Invalid output " + proteu.getResponseHeaderStatus().getCode() + " to service " + path + "\n#\n"
                            + validateSchemaOutProblems + "\n#\n");
                    return false;
                }
            } catch (Exception e) {
                validationSchemaError(pathSchema, e);
                return false;
            }
        } else {
            return true;
        }
    }

    private void validationSchemaError(Path pathSchema, Exception e) {
        if (org.netuno.proteu.Config.isReduceErrors()) {
            logger.trace("Error in service schema validation.", e);
        } else {
            logger.warn("Error in service schema validation.", e);
        }
        logger.warn(
                "\n\n#\n# Request " + proteu.getResponseHeaderStatus().getCode() + " to service " + path
                + "\n#\n# Error in schema "
                + pathSchema.toString().substring(Config.getPathAppServices(proteu).length())
                + "\n#\n#     "
                + e.getMessage().replace("\n", "\n#     ")
                + "\n#\n"
        );
    }

    private ByteArrayInputStream loadSchemaAsInputStream(Path pathSchema) throws IOException {
        String schemaContent = InputStream.readFromFile(pathSchema);
        Values schemaValues = Values.fromJSON(schemaContent);
        loadSchema(schemaValues);
        return new ByteArrayInputStream(schemaValues.toJSON().getBytes());
    }

    private void loadSchema(Values data) {
        for (String parentKey : data.keys()) {
            Values parentValues = data.getValues(parentKey);
            if (parentValues != null && parentValues.isMap()) {
                for (String childKey : parentValues.keys()) {
                    if (childKey.equalsIgnoreCase("_schema")) {
                        String pathSchema = parentValues.getString(childKey);
                        loadDataWithSchema(parentValues, pathSchema);
                    } else if (childKey.equalsIgnoreCase("type")) {
                        String type = parentValues.getString(childKey);
                        if (type.equalsIgnoreCase("string-not-empty")) {
                            parentValues.set("type", "string");
                            parentValues.set("minLength", 1);
                        } else if (type.equalsIgnoreCase("array-not-empty")) {
                            parentValues.set("type", "array");
                            parentValues.set("minLength", 1);
                        } else if (type.equalsIgnoreCase("id")) {
                            parentValues.set("type", "number");
                        } else if (type.equalsIgnoreCase("uid") || type.equalsIgnoreCase("uuid") || type.equalsIgnoreCase("guid")) {
                            parentValues.set("type", "string");
                            parentValues.set("pattern", "[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}");
                        }
                    }
                }
                loadSchema(parentValues);
            } else {
                if (parentKey.equalsIgnoreCase("_schema")) {
                    String pathSchema = data.getString(parentKey);
                    loadDataWithSchema(data, pathSchema);
                }
            }
        }
    }

    private void loadDataWithSchema(Values data, String path) {
        path = org.netuno.psamata.io.Path.safeFileSystemPath(path);
        String scriptPath = "/_schema/" + org.netuno.psamata.io.Path.safeFileSystemPath(path);
        if (ScriptRunner.searchScriptFile(Config.getPathAppServices(proteu) + scriptPath) != null) {
            data.unset("_schema");
            try {
                hili.bind("dataSchema", data);
                hili.runScriptSandbox(Config.getPathAppServices(proteu), scriptPath);
            } finally {
                hili.unbind("dataSchema");
            }
            return;
        }
        Path pathSchema = Paths.get(Config.getPathAppServices(proteu) + "/_schema/" + path + ".json");
        try {
            if (Files.exists(pathSchema)) {
                String schemaContent = InputStream.readFromFile(pathSchema);
                Values schemaValues = Values.fromJSON(schemaContent);
                loadSchema(schemaValues);
                data.unset("_schema");
                data.merge(schemaValues);
            } else {
                throw new FileNotFoundException("Schema not found: " + path);
            }
        } catch (Exception e) {
            validationSchemaError(pathSchema, e);
        }
    }

    public Values pathsOpenAPI() {
        return pathsOpenAPI(this.path);
    }

    public Values pathsOpenAPI(String path) {
        Values servicePaths = new Values();
        pathsOpenAPI(path, servicePaths);
        return servicePaths;
    }

    public void pathsOpenAPI(String path, Values pathsOpenAPI) {
        if (path.endsWith("_openapi")) {
            path = path.substring(0, path.length() - "_openapi".length());
        }
        try {
            final String servicesPath = path;
            Path folder = Paths.get(Config.getPathAppServices(proteu) + "/" + path);
            if (Files.isDirectory(folder)) {
                try (Stream<Path> directories = Files.list(folder)) {
                    List<Path> listFiles = directories.collect(Collectors.toList());
                    for (Path p : listFiles) {
                        if (Files.isDirectory(p)) {
                            pathsOpenAPI(servicesPath + "/" + p.getFileName(), pathsOpenAPI);
                        } else if (Files.isRegularFile(p)) {
                            final String scriptFileName = FilenameUtils.getBaseName(p.toString());
                            final String folderPath = p.getParent().toString();
                            final String scriptPath = ScriptRunner.searchScriptFile(Paths.get(folderPath, scriptFileName).toString());
                            if (scriptPath != null) {
                                final String serviceFullName = FilenameUtils.getBaseName(p.getFileName().toString());
                                final String serviceName = FilenameUtils.getBaseName(serviceFullName);
                                String serviceMethod = FilenameUtils.getExtension(serviceFullName);
                                if (serviceMethod.isEmpty() && methods.contains(serviceName)) {
                                    serviceMethod = serviceName;
                                }
                                if (serviceName.isEmpty() || serviceMethod.isEmpty()) {
                                    continue;
                                }
                                String servicePath = servicesPath;
                                if (!methods.contains(serviceName)) {
                                    servicePath += "/" + serviceName;
                                }
                                try {
                                    Path fileSchemaIn = Paths.get(folderPath, serviceFullName + ".in.json");
                                    if (Files.exists(fileSchemaIn)) {
                                        Values endpoint = new Values();
                                        String jsonIn = InputStream.readAll(loadSchemaAsInputStream(fileSchemaIn));
                                        Values schemaIn = Values.fromJSON(jsonIn);
                                        if (schemaIn.hasKey("summary")) {
                                            endpoint.set("summary", schemaIn.getString("summary"));
                                            schemaIn.unset("summary");
                                        }
                                        if (schemaIn.hasKey("description")) {
                                            endpoint.set("description", schemaIn.getString("description"));
                                            schemaIn.unset("description");
                                        }
                                        if (schemaIn.hasKey("security")) {
                                            endpoint.set("security", schemaIn.getValues("security"));
                                            schemaIn.unset("security");
                                        }
                                        if (serviceMethod.equalsIgnoreCase("get")
                                                || serviceMethod.equalsIgnoreCase("delete")) {
                                            if (schemaIn.hasKey("properties")) {
                                                Values parameters = new Values();
                                                Values schemaInProperties = schemaIn.getValues("properties");
                                                for (String key : schemaInProperties.keys()) {
                                                    Values schemaInProperty = schemaInProperties.getValues(key);
                                                    Values parameter = new Values();
                                                    parameter.set("in", "query");
                                                    parameter.set("name", key);
                                                    parameter.set("schema", schemaInProperty);
                                                    if (schemaInProperty.hasKey("description")) {
                                                        parameter.set("description", schemaIn.getString("description"));
                                                        schemaInProperty.unset("description");
                                                    }
                                                    parameters.add(parameter);
                                                }
                                                endpoint.set("parameters", parameters);
                                            }
                                        } else {
                                            Values requestContents = new Values();
                                            Values requestContent = new Values();
                                            if (schemaIn.hasKey("description")) {
                                                requestContent.set("description", schemaIn.getString("description"));
                                                schemaIn.unset("description");
                                            }
                                            requestContent.set("schema", schemaIn);
                                            requestContents.set("application/json", requestContent);
                                            endpoint.set("requestBody", new Values().set("content", requestContents));
                                        }
                                        final Values responses = new Values();
                                        try (Stream<Path> filesJSON = Files.list(folder)) {
                                            List<Path> listFilesJSON = filesJSON.collect(Collectors.toList());
                                            for (Path pathOutJSON : listFilesJSON) {
                                                if (Files.isRegularFile(pathOutJSON)) {
                                                    String fileNameOutJSON = pathOutJSON.getFileName().toString();
                                                    if ((fileNameOutJSON.startsWith(serviceName + "." + serviceMethod + ".out")
                                                            || (serviceName.equals(serviceMethod)
                                                                && fileNameOutJSON.startsWith(serviceMethod + ".out"))
                                                            ) && fileNameOutJSON.endsWith(".json")) {
                                                        String statusCode = FilenameUtils.getExtension(FilenameUtils.getBaseName(pathOutJSON.getFileName().toString()));
                                                        if (statusCode.equals("out")) {
                                                            statusCode = "200";
                                                        }
                                                        try {
                                                            Values response = new Values();
                                                            Values responseContent = new Values();
                                                            String jsonOut = InputStream.readAll(loadSchemaAsInputStream(pathOutJSON));
                                                            Values schemaOut = Values.fromJSON(jsonOut);
                                                            if (schemaOut.hasKey("description")) {
                                                                response.set("description", schemaOut.getString("description"));
                                                                schemaOut.unset("description");
                                                            } else {
                                                                response.set("description", Proteu.HTTPStatus.fromCode(Integer.parseInt(statusCode)).toString());
                                                            }
                                                            responseContent.set("schema", schemaOut);
                                                            Values responseContents = new Values();
                                                            responseContents.set("application/json", responseContent);
                                                            response.set("content", responseContents);
                                                            responses.set(statusCode, response);
                                                        } catch (Throwable e) {
                                                            if (e.getMessage() == null) {
                                                                logger.fatal("OpenAPI " + servicesPath + "/" + pathOutJSON.getFileName() + " failed.", e);
                                                            } else {
                                                                logger.trace("OpenAPI " + servicesPath + "/" + pathOutJSON.getFileName() + " failed.", e);
                                                                logger.warn(
                                                                        "\n\n#\n# OpenAPI " + servicesPath + "/" + pathOutJSON.getFileName() + " failed: "
                                                                        + "\n#\n#     "
                                                                        + e.getMessage().replace("\n", "\n#     ")
                                                                        + "\n#\n"
                                                                );
                                                            }
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                        endpoint.set("responses", responses);
                                        Values serviceEndpoints = pathsOpenAPI.getValues(servicePath, new Values());
                                        serviceEndpoints.set(serviceMethod, endpoint);
                                        pathsOpenAPI.set(servicePath, serviceEndpoints);
                                    }
                                } catch (Throwable e) {
                                    if (e.getMessage() == null) {
                                        logger.fatal("OpenAPI " + servicesPath + "/" + serviceFullName + " failed.", e);
                                    } else {
                                        logger.trace("OpenAPI " + servicesPath + "/" + serviceFullName + " failed.", e);
                                        logger.warn(
                                                "\n\n#\n# OpenAPI " + servicesPath + "/" + serviceFullName + " failed: "
                                                + "\n#\n#     "
                                                + e.getMessage().replace("\n", "\n#     ")
                                                + "\n#\n"
                                        );
                                    }
                                }
                            }
                        }
                    }
                }
            }
        } catch (Throwable e) {
            if (e.getMessage() == null) {
                logger.fatal("OpenAPI " + path + " failed.", e);
            } else {
                logger.trace("OpenAPI " + path + " failed.", e);
                logger.warn(
                        "\n\n#\n# OpenAPI " + path + " failed: "
                        + "\n#\n#     "
                        + e.getMessage().replace("\n", "\n#     ")
                        + "\n#\n"
                );
            }
        }
    }

    public static void _main(Proteu proteu, Hili hili) throws IOException, ProteuException {
        Service service = new Service(proteu, hili);
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
            if (service.path.equals("_openapi")) {
                Values openapi = new Values();
                Path fileInfo = Paths.get(Config.getPathAppServices(proteu), "_openapi.json");
                if (Files.exists(fileInfo)) {
                    openapi = Values.fromJSON(InputStream.readFromFile(fileInfo));
                } else {
                    throw new HiliError("OpenAPI information file does not exist in:\n" + fileInfo.toString()).setLogFatal(true);
                }
                Values appConfigOpenAPI = proteu.getConfig().getValues("_app:config").getValues("openapi", new Values());
                Values base = new Values().set("openapi", "3.0.0");
                if (openapi.hasKey("info")) {
                    base.set("info", openapi.getValues("info"));
                }
                if (openapi.hasKey("components")) {
                    base.set("components", openapi.getValues("components"));
                }
                if (appConfigOpenAPI.has("servers")) {
                    base.set("servers", appConfigOpenAPI.getValues("servers"));
                }
                base.set("paths", service.pathsOpenAPI());
                proteu.outputJSON(base);
                return;
            }
            if (!service.validateSchemaIn()) {
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
                    proteu.responseHTTPError(Proteu.HTTPStatus.Forbidden403, hili);
                    return;
                }
                if (service.wasCancelled()) {
                    proteu.responseHTTPError(Proteu.HTTPStatus.ServiceUnavailable503, hili);
                    return;
                }
                EventExecutor.getInstance(proteu).runAppEvent(AppEventType.BeforeServiceConfiguration);
                service.core("_service_start");
                EventExecutor.getInstance(proteu).runAppEvent(AppEventType.AfterServiceConfiguration);
                ByteArrayOutputStream outStream = null;
                if (service.outValidation != null && service.outValidation) {
                    if (service.schemaOutExists()) {
                        outStream = new ByteArrayOutputStream();
                        proteu.getOutput().getMirrors().add(outStream);
                    }
                }
                service.execute(service.getPath());
                if (outStream != null) {
                    service.validateSchemaOut(outStream);
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
            proteu.responseHTTPError(Proteu.HTTPStatus.NotFound404, hili);
            logger.warn("\n"
                    + "\n#"
                    + "\n# "+ EmojiParser.parseToUnicode(":compass:") +" Service not found for "+ proteu.getRequestHeader().getString("Method").toUpperCase() +" method: "
                    + "\n#"
                    + "\n# " + Config.getPathAppServices(proteu)
                    + "\n# " + file
                    + "\n#"
                    + "\n"
            );
        }
        return false;
    }
    
    public static Service getInstance(Proteu proteu) {
        return (Service)proteu.getConfig().get("_service:instance");
    }
    
    @Override
    protected void finalize() throws Throwable {
        hili.unbind("service");
    }
}
