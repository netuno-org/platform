package org.netuno.tritao.ai.mcp;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.netuno.proteu.Proteu;
import org.netuno.psamata.Values;
import org.netuno.tritao.Service;
import org.netuno.tritao.Web;

import org.netuno.tritao.hili.Hili;
import org.netuno.tritao.resource.Header;
import org.netuno.tritao.resource.MCP;
import org.netuno.tritao.resource.Out;

public class HandlerMCP extends Web {
    private static final Logger LOGGER = LogManager.getLogger(HandlerMCP.class);

    private final Proteu proteu;
    private final Hili hili;
    private final Service service;

    public HandlerMCP(Service service, Proteu proteu, Hili hili) {
        super(proteu, hili);

        this.service = service;
        this.proteu = proteu;
        this.hili = hili;
    }

    public void run() throws Exception {
        MCP tools = resource(MCP.class);
        tools.initialize();

        Header header = resource(Header.class);
        Out out = resource(Out.class);

        header.contentType("application/json");
        header.noCache();

        header.status(Proteu.HTTPStatus.OK200);
        out.json(tools.listTools().toJSON());
        tools.runTool("hello", new Values());
    }

}
