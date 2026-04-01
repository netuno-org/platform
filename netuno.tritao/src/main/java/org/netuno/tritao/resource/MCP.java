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

    private boolean isEnabled = true;
    private String title = "Netuno MCP";
    private String version = "1.0";

    public MCP(Proteu proteu, Hili hili) {
        super(proteu, hili);
    }

    public boolean isEnabled() {
        return isEnabled;
    }

    public Values getServerInfo(){
        return new Values()
                .set("name", title)
                .set("version", version);
    }

    public void init() {
        if (!Config.isAppConfigLoaded(getProteu())) {
            logger.warn("MCP server not initialized: application configuration not loaded.");
            return;
        }

        try {
            Values mcpConfig = getProteu().getConfig()
                    .getValues("_app:config")
                    .getValues("mcp")
                    .getValues("server");

            if (mcpConfig == null || !mcpConfig.getBoolean("enabled")) {
                this.isEnabled = false;
                return;
            }
            this.title = mcpConfig.getString("name", "Netuno MCP");
            this.version = mcpConfig.getString("version", "1.0");
        } catch (Exception e) {
            logger.error("Error loading MCP config", e);
        }

        Path mcpPath = Paths.get(Config.getPathAppMCP(getProteu()));

        try {
            if (Files.notExists(mcpPath)) {
                Files.createDirectories(mcpPath);
                logger.info("MCP folder created: {}", mcpPath);
            }

            try (Stream<Path> files = Files.walk(mcpPath)) {
                files
                        .filter(Files::isRegularFile)
                        .sorted()
                        .forEach(file -> loadScript(mcpPath, file));

            } catch (IOException e) {
                logger.fatal("Error walking MCP folder: {}", mcpPath, e);
            }

        } catch (IOException e) {
            logger.fatal("Error initializing MCP folder: {}", mcpPath, e);
        }
    }

    private void loadScript(Path rootPath, Path file) {
        String fileName = FilenameUtils.removeExtension(file.getFileName().toString());
        Path relativePath = rootPath.relativize(file);

        try {
            ScriptResult result = getHili()
                    .sandbox()
                    .runScript(file.getParent().toString(), fileName);

            if (result.isSuccess()) {
                logger.info("Loaded MCP script: {}", relativePath);
            } else {
                logger.error("Failed loading MCP tool script: {}", relativePath);
            }
        } catch (Exception e) {
            logger.error("Error executing MCP script: {}", relativePath, e);
        }
    }

    public void addMiddlewares(MCPMiddleware... middlewares) {
        Collections.addAll(this.middlewares, middlewares);
    }

    public void registerTool(String name, String description, Values schema, Function<Values, Values> execute) {
        tools.put(name, new MCPTool(name, description, schema, execute));
    }

    public boolean containsTool(String name) {
        return tools.containsKey(name);
    }

    public Values listAvailableTools() {
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

    public Values executeTool(String name, Values input) {
        MCPTool tool = tools.get(name);

        if (tool == null) {
            return buildError("Tool not found: " + name);
        }

        for (MCPMiddleware middleware : middlewares) {
            Values result = middleware.intercept(tool);
            if (result == null) {
                continue;
            }
            return buildError(result.toJSON());
        }

        try {
            return tool.execute.apply(input);
        } catch (Exception e) {
            logger.error("Error executing tool: {}", name, e);
            return buildError(e.getMessage());
        }
    }

    private Values buildError(String message) {
        Values error = new Values();
        error.set("success", false);
        error.set("error", message);
        return error;
    }

    @FunctionalInterface
    public interface MCPMiddleware {
        Values intercept(MCPTool tool);
    }

    private record MCPTool(
            String name,
            String description,
            Values schema,
            Function<Values, Values> execute
    ) {}
}