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

package org.netuno.tritao.openapi;

import com.vdurmont.emoji.EmojiParser;
import jakarta.json.JsonReader;
import jakarta.json.JsonValue;
import org.apache.commons.io.FilenameUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.leadpony.justify.api.JsonSchema;
import org.leadpony.justify.api.JsonValidationService;
import org.leadpony.justify.api.ProblemHandler;
import org.netuno.proteu.Proteu;
import org.netuno.psamata.Values;
import org.netuno.psamata.io.InputStream;
import org.netuno.psamata.script.ScriptRunner;
import org.netuno.tritao.Service;
import org.netuno.tritao.WebMaster;
import org.netuno.tritao.config.Config;
import org.netuno.tritao.config.Hili;
import org.netuno.tritao.config.HiliError;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Builds the OpenAPI Definition.
 * @author Eduardo Fonseca Velasques - @eduveks
 */
public class Schema extends WebMaster {
    private static Logger logger = LogManager.getLogger(Schema.class);

    public Service service = null;

    private DataSchema dataSchemaProcessing = new DataSchema();

    private String validateSchemaInProblems = "";
    private String validateSchemaOutProblems = "";

    public Schema(Service service, Proteu proteu, Hili hili) {
        super(proteu, hili);
        this.service = service;
    }

    private void validateSchemaInProblem(String problem) {
        if (!validateSchemaInProblems.isEmpty()) {
            validateSchemaInProblems += "\n";
        }
        validateSchemaInProblems += "#     " + problem;
    }

    public Path schemaInPath() {
        return Paths.get(Config.getPathAppServices(getProteu()) + "/" + service.path + ".in.json");
    }

    public boolean schemaInExists() {
        return Files.exists(schemaInPath());
    }

    public boolean validateSchemaIn() {
        dataSchemaProcessing.setMethod(getProteu().getRequestHeader().getString("Method"));
        dataSchemaProcessing.setService(service.getPath());
        dataSchemaProcessing.setIn(true);
        dataSchemaProcessing.setOut(false);
        JsonValidationService jsonValidationService = JsonValidationService.newInstance();
        Path pathSchema = schemaInPath();
        if (Files.exists(pathSchema)) {
            try {
                JsonSchema schema = jsonValidationService.readSchema(loadSchemaAsInputStream(pathSchema));
                ProblemHandler handler = jsonValidationService.createProblemPrinter(this::validateSchemaInProblem);
                Values data = new Values();
                data.merge(getProteu().getRequestGet());
                data.merge(getProteu().getRequestPost());
                try (JsonReader reader = jsonValidationService.createReader(new StringReader(data.toJSON()), schema, handler)) {
                    JsonValue value = reader.readValue();
                    if (validateSchemaInProblems.isEmpty()) {
                        return true;
                    }
                    logger.warn("\n\n#\n# "+ EmojiParser.parseToUnicode(":crossed_swords:") +" Invalid request to service " + service.getPath() + "\n#\n"
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
        Path outWithCode = schemaOutPath(getProteu().getResponseHeaderStatus().getCode());
        if (Files.exists(outWithCode)) {
            return outWithCode;
        }
        return Paths.get(Config.getPathAppServices(getProteu()) + "/" + service.getPath() + ".out.json");
    }

    public Path schemaOutPath(int httpCode) {
        return Paths.get(Config.getPathAppServices(getProteu()) + "/" + service.getPath() + ".out." + httpCode + ".json");
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
        dataSchemaProcessing.setMethod(getProteu().getRequestHeader().getString("Method"));
        dataSchemaProcessing.setService(service.getPath());
        dataSchemaProcessing.setIn(false);
        dataSchemaProcessing.setOut(true);
        dataSchemaProcessing.setStatusCode(getProteu().getResponseHeaderStatus().getCode());
        JsonValidationService jsonValidationService = JsonValidationService.newInstance();
        Path pathSchema = schemaOutPath();
        if (Files.exists(pathSchema)) {
            try {
                Values values = Values.fromJSON(InputStream.readAll(loadSchemaAsInputStream(pathSchema)));
                if (!values.hasKey("type")) {
                    return true;
                }
                JsonSchema schema = jsonValidationService.readSchema(loadSchemaAsInputStream(pathSchema));
                ProblemHandler handler = jsonValidationService.createProblemPrinter(this::validateSchemaOutProblem);
                String outContent = new String(outStream.toByteArray());
                try ( JsonReader reader = jsonValidationService.createReader(new StringReader(outContent), schema, handler)) {
                    JsonValue value = reader.readValue();
                    if (validateSchemaOutProblems.isEmpty()) {
                        return true;
                    }
                    logger.warn("\n\n#\n# Invalid output " + getProteu().getResponseHeaderStatus().getCode() + " to service " + service.getPath() + "\n#\n"
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
                "\n\n#\n# Request " + getProteu().getResponseHeaderStatus().getCode() + " to service " + service.getPath()
                        + "\n#\n# Error in schema "
                        + pathSchema.toString().substring(Config.getPathAppServices(getProteu()).length())
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
                        } else if (type.equalsIgnoreCase("ipv4")) {
                            parentValues.set("type", "string");
                            parentValues.set("pattern", "^(\\b25[0-5]|\\b2[0-4][0-9]|\\b[01]?[0-9][0-9]?)(\\.(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)){3}$");
                        } else if (type.equalsIgnoreCase("ipv6")) {
                            parentValues.set("type", "string");
                            parentValues.set("pattern", "^(([0-9a-fA-F]{1,4}:){7,7}[0-9a-fA-F]{1,4}|([0-9a-fA-F]{1,4}:){1,7}:|([0-9a-fA-F]{1,4}:){1,6}:[0-9a-fA-F]{1,4}|([0-9a-fA-F]{1,4}:){1,5}(:[0-9a-fA-F]{1,4}){1,2}|([0-9a-fA-F]{1,4}:){1,4}(:[0-9a-fA-F]{1,4}){1,3}|([0-9a-fA-F]{1,4}:){1,3}(:[0-9a-fA-F]{1,4}){1,4}|([0-9a-fA-F]{1,4}:){1,2}(:[0-9a-fA-F]{1,4}){1,5}|[0-9a-fA-F]{1,4}:((:[0-9a-fA-F]{1,4}){1,6})|:((:[0-9a-fA-F]{1,4}){1,7}|:)|fe80:(:[0-9a-fA-F]{0,4}){0,4}%[0-9a-zA-Z]{1,}|::(ffff(:0{1,4}){0,1}:){0,1}((25[0-5]|(2[0-4]|1{0,1}[0-9]){0,1}[0-9])\\.){3,3}(25[0-5]|(2[0-4]|1{0,1}[0-9]){0,1}[0-9])|([0-9a-fA-F]{1,4}:){1,4}:((25[0-5]|(2[0-4]|1{0,1}[0-9]){0,1}[0-9])\\.){3,3}(25[0-5]|(2[0-4]|1{0,1}[0-9]){0,1}[0-9]))$");
                        } else if (type.equalsIgnoreCase("timestamp")) {
                            parentValues.set("type", "string");
                            parentValues.set("pattern", "^(?:[0-9]{4}-[0-9]{2}-[0-9]{2})(?:([ T][0-9]{2}:[0-9]{2}:[0-9]{2}(?:([.,][0-9]{1,3})?Z?))?)$");
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

    private DataSchema createDataSchema(Values data) {
        DataSchema dataSchema = new DataSchema(dataSchemaProcessing, data);
        return dataSchema;
    }

    private void loadDataWithSchema(Values data, String path) {
        path = org.netuno.psamata.io.Path.safeFileSystemPath(path);
        String scriptPath = "/_schema/" + org.netuno.psamata.io.Path.safeFileSystemPath(path);
        if (ScriptRunner.searchScriptFile(Config.getPathAppServices(getProteu()) + scriptPath) != null) {
            data.unset("_schema");
            try {
                getHili().bind("dataSchema", createDataSchema(data));
                getHili().runScriptSandbox(Config.getPathAppServices(getProteu()), scriptPath);
            } finally {
                getHili().unbind("dataSchema");
            }
            return;
        }
        Path pathSchema = Paths.get(Config.getPathAppServices(getProteu()) + "/_schema/" + path + ".json");
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
        return pathsOpenAPI(service.getPath());
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
            Path folder = Paths.get(Config.getPathAppServices(getProteu()) + "/" + path);
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
                                if (serviceMethod.isEmpty() && service.getMethods().contains(serviceName)) {
                                    serviceMethod = serviceName;
                                }
                                if (serviceName.isEmpty() || serviceMethod.isEmpty()) {
                                    continue;
                                }
                                String servicePath = servicesPath;
                                if (!service.getMethods().contains(serviceName)) {
                                    servicePath += "/" + serviceName;
                                }
                                try {
                                    dataSchemaProcessing.setMethod(serviceMethod);
                                    dataSchemaProcessing.setService(servicePath);
                                    Path fileSchemaIn = Paths.get(folderPath, serviceFullName + ".in.json");
                                    if (Files.exists(fileSchemaIn)) {
                                        dataSchemaProcessing.setIn(true);
                                        dataSchemaProcessing.setOut(false);
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
                                        } else {
                                            endpoint.set(
                                                    "security",
                                                    new Values().add(
                                                            new Values().set("BearerAuth", new Values().forceList())
                                                    )
                                            );
                                        }
                                        if (serviceMethod.equalsIgnoreCase("get")
                                                || serviceMethod.equalsIgnoreCase("delete")) {
                                            if (schemaIn.hasKey("properties")) {
                                                Values parameters = new Values().forceList();
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
                                            dataSchemaProcessing.setIn(false);
                                            dataSchemaProcessing.setOut(true);
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
                                                            dataSchemaProcessing.setStatusCode(Integer.parseInt(statusCode));
                                                            Values response = new Values();
                                                            Values responseContent = new Values();
                                                            String jsonOut = InputStream.readAll(loadSchemaAsInputStream(pathOutJSON));
                                                            Values schemaOut = Values.fromJSON(jsonOut);
                                                            if (schemaOut.hasKey("description")) {
                                                                response.set("description", schemaOut.getString("description"));
                                                                schemaOut.unset("description");
                                                            } else {
                                                                response.set("description", Proteu.HTTPStatus.fromCode(dataSchemaProcessing.getStatusCode()).toString());
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

    public void run() throws Exception {
        Values openapi = new Values();
        Path fileInfo = Paths.get(Config.getPathAppServices(getProteu()), "_openapi.json");
        if (Files.exists(fileInfo)) {
            openapi = Values.fromJSON(InputStream.readFromFile(fileInfo));
        } else {
            throw new HiliError("OpenAPI information file does not exist in:\n" + fileInfo.toString()).setLogFatal(true);
        }
        Values appConfigOpenAPI = getProteu().getConfig().getValues("_app:config").getValues("openapi", new Values());
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
        base.set("paths", pathsOpenAPI());
        getProteu().outputJSON(base);
    }
}
