package org.netuno.tritao.resource;

import org.apache.commons.io.FilenameUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.netuno.proteu.Proteu;
import org.netuno.psamata.Values;
import org.netuno.tritao.config.Config;
import org.netuno.tritao.hili.Hili;
import org.netuno.tritao.sandbox.ScriptResult;

import java.io.IOException;
import java.nio.file.*;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Stream;

@Resource(name = "mcp")
public class MCP extends ResourceBase {
    private static final Logger logger = LogManager.getLogger(MCP.class);

    private final Map<String, MCPTool> tools = new HashMap<>();
    private final List<MCPMiddleware> middlewares = new ArrayList<>();

    public MCP(Proteu proteu, Hili hili) {
        super(proteu, hili);
    }

    public void initialize() {
        Path mcpPath = Paths.get(Config.getPathAppMCP(getProteu()));

        try {
            if (Files.notExists(mcpPath)) {
                Files.createDirectories(mcpPath);
                logger.info("MCP folder created: {}", mcpPath);
            }

            try (Stream<Path> files = Files.list(mcpPath)) {
                files
                        .filter(Files::isRegularFile)
                        .sorted()
                        .forEach(this::loadScript);

            } catch (IOException e) {
                logger.fatal("Error listing MCP folder: {}", mcpPath, e);
            }

        } catch (IOException e) {
            logger.fatal("Error initializing MCP folder: {}", mcpPath, e);
        }
    }

    private void loadScript(Path file) {
        String fileName = FilenameUtils.removeExtension(file.getFileName().toString());

        try {
            ScriptResult result = getHili()
                    .sandbox()
                    .runScript(file.getParent().toString(), fileName);

            if (!result.isSuccess()) {
                logger.error("Failed loading MCP tool script: {}", fileName);
            } else {
                logger.info("Loaded MCP script: {}", fileName);
            }

        } catch (Exception e) {
            logger.error("Error executing MCP script: {}", fileName, e);
        }
    }

    public void addMiddleware(MCPMiddleware middleware) {
        middlewares.add(middleware);
    }

    public void addTool(String name, String description, Values schema, Function<Values, Values> execute) {
        tools.put(name, new MCPTool(name, description, schema, execute));
    }

    public boolean hasTool(String name) {
        return tools.containsKey(name);
    }

    public Values listTools() {
        Values list = new Values().forceList();

        tools.values().forEach(tool -> {
            Values t = new Values();
            t.set("name", tool.name);
            t.set("description", tool.description);
            t.set("inputSchema", tool.schema);
            list.add(t);
        });

        return list;
    }

    public Values callTool(String name, Values input) {
        MCPTool tool = tools.get(name);

        if (tool == null) {
            return error("Tool not found: " + name);
        }

        for (MCPMiddleware middleware : middlewares) {
            Values result = middleware.handle(tool);
            if (result == null) {
                continue;
            }
            return error(result.toJSON());
        }

        try {
            return tool.execute.apply(input);
        } catch (Exception e) {
            logger.error("Error executing tool: {}", name, e);
            return error(e.getMessage());
        }
    }

    private Values error(String message) {
        Values error = new Values();
        error.set("success", false);
        error.set("error", message);
        return error;
    }

    @FunctionalInterface
    public interface MCPMiddleware {
        Values handle(MCPTool tool);
    }

    private record MCPTool(
            String name,
            String description,
            Values schema,
            Function<Values, Values> execute
    ) {}
}