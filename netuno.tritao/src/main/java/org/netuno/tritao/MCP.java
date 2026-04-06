package org.netuno.tritao;


import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.netuno.proteu.Path;
import org.netuno.proteu.Proteu;
import org.netuno.psamata.Values;
import org.netuno.tritao.resource.Header;
import org.netuno.tritao.resource.Out;

@Path("/org/netuno/tritao/MCP")
public class MCP extends Web {
    private static Logger logger = LogManager.getLogger(Register.class);

    public void run() throws Exception {
        org.netuno.tritao.resource.MCP tools = resource(org.netuno.tritao.resource.MCP.class);
        tools.init();

        Header header = resource(Header.class);
        Out out = resource(Out.class);

        header.contentType("application/json");
        header.noCache();

        Values query = getProteu().getRequestAll();

        if (!tools.isEnabled()) {
            header.status(400);
            out.json(new Values().set("error", "MCP server is not configured or not enabled"));
            return;
        }

        if (query == null || query.isEmpty()) {
            header.status(400);
            out.json(new Values().set("error", "Invalid JSON-RPC request"));
            return;
        }

        String method = query.getString("method");
        Object id = query.get("id");
        Values result = new Values().set("jsonrpc", "2.0").set("id", id);

        if ("initialize".equals(method)) {
            Values capabilities = new Values()
                    .set("tools", new Values()
                            .set("listChanged", false)
                    );

            result.set("result", new Values()
                    .set("protocolVersion", "2025-11-25")
                    .set("serverInfo", tools.getServerInfo())
                    .set("capabilities", capabilities)
            );
        }
        else if (method.equalsIgnoreCase("tools/list")) {
            result.set("result", new Values().set("tools", tools.listAvailableTools()));
        } else if (method.equalsIgnoreCase("tools/call")) {
            Values params = query.getValues("params");
            String name = params.getString("name");
            Values arguments = params.getValues("arguments");

            if (!tools.containsTool(name)) {
                result.set("error", new Values()
                        .set("code", -32601)
                        .set("message", "Tool not found: " + name)
                );
            } else {
                Values toolResult = tools.executeTool(name, arguments);
                Values content = new Values().forceList();

                content.add(new Values()
                        .set("type", "text")
                        .set("text", toolResult.toJSON())
                );

                result.set("result", new Values()
                        .set("content", content)
                );
            }
        }

        header.status(Proteu.HTTPStatus.OK200);
        out.json(result);
    }
}