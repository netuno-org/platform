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

    protected MCP(Proteu proteu, Hili hili) {
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


    public void initialize() {
        try (Stream<Path> files = Files.list(Paths.get(Config.getPathAppMCP(getProteu())))) {
            files.sorted().forEach(
                    (f) -> {
                        String fileName = FilenameUtils.removeExtension(f.getFileName().toString());
                        ScriptResult scriptSchemaResult = getHili().sandbox().runScript(Config.getPathAppSetup(getProteu()), fileName);
                        System.out.println(f);
                        System.out.println(scriptSchemaResult.isSuccess());
                    }
            );
        } catch (IOException e) {
            logger.fatal("When looking for setup schema scripts into the folder: " + Config.getPathAppSetup(getProteu()), e);
        }

    }


}