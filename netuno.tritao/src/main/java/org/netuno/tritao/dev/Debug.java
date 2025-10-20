package org.netuno.tritao.dev;

import org.netuno.proteu.Path;
import org.netuno.tritao.Web;
import org.netuno.tritao.auth.Auth;
import org.netuno.tritao.util.TemplateBuilder;

import javax.script.ScriptException;
import java.io.IOException;

@Path("/org/netuno/tritao/dev/Debug")
public class Debug extends Web {
    public void run() throws ScriptException, IOException {
        if (!Auth.isDevAuthenticated(getProteu(), getHili(), Auth.Type.SESSION, true)) {
            return;
        }
        TemplateBuilder.output(getProteu(), getHili(), "dev/debug/main");
    }
}
