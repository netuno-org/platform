package org.netuno.tritao.resource;

import org.apache.commons.io.FilenameUtils;
import org.apache.logging.log4j.LogManager;
import org.netuno.tritao.config.Config;
import org.netuno.proteu.Proteu;
import org.netuno.psamata.Values;
import org.netuno.tritao.hili.Hili;
import org.netuno.tritao.sandbox.ScriptResult;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Stream;
import org.apache.logging.log4j.Logger;

@Resource(name = "mcp")
public class MCP extends ResourceBase {
    private static Logger logger = LogManager.getLogger(MCP.class);

    private List<MCPTool> tools;

    public MCP(Proteu proteu, Hili hili) {
        super(proteu, hili);
        this.tools = new ArrayList<>();
    }

    private static class MCPTool {
        String name;
        String description;
        Values schema;
        Function<Values, Values> execute;

        MCPTool(String name, String description, Values schema,
                Function<Values, Values> execute) {

            this.name = name;
            this.description = description;
            this.schema = schema;
            this.execute = execute;
        }
    }

    private void initialize() {
        Path mcpPath = Paths.get(Config.getPathAppMCP(getProteu()));
        try {
            if (!Files.exists(mcpPath)) {
                Files.createDirectories(mcpPath);
                logger.info("MCP folder created: " + mcpPath);
            }

            try (Stream<Path> files = Files.list(mcpPath)) {
                files.sorted().forEach(f -> {
                    if (!Files.isRegularFile(f)) return;
                    String fileName = FilenameUtils.removeExtension(f.getFileName().toString());
                    ScriptResult result = getHili().sandbox().runScript(mcpPath.toString(), fileName);
                    logger.info("Loaded MCP tool: " + fileName + " | Success: " + result.isSuccess());
                });
            } catch (IOException e) {
                logger.fatal("When looking for mcp tools scripts into the folder: " + mcpPath, e);
            }

        } catch (IOException e) {
            logger.fatal("Error initializing MCP in folder: " + mcpPath.toString(), e);
        }
    }

    public void addTool(String name, String description, Values schema, Function<Values, Values> execute) {
        tools.add(new MCPTool(name, description, schema, execute));
    }

    public Values listTools() {
        Values list = new Values().forceList();

        for (MCPTool tool : tools) {
            Values t = new Values();
            t.set("name", tool.name);
            t.set("description", tool.description);
            t.set("input_schema", tool.schema);

            list.add(t);
        }

        return list;
    }

    private Values callTool(String name, Values input) {
        for (MCPTool tool : tools) {
            if (tool.name.equals(name)) {
                try {
                    return tool.execute.apply(input);
                } catch (Exception e) {
                    logger.error("Error executing tool: " + name, e);
                    Values error = new Values();
                    error.set("success", false);
                    error.set("error", e.getMessage());
                    return error;
                }
            }
        }
        Values notFound = new Values();
        notFound.set("success", false);
        notFound.set("error", "Tool not found: " + name);
        return notFound;
    }

}