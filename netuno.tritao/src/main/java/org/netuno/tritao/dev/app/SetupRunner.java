package org.netuno.tritao.dev.app;

import org.netuno.proteu.Path;
import org.netuno.tritao.auth.Auth;
import org.netuno.tritao.WebMaster;
import org.netuno.tritao.resource.Header;
import org.netuno.tritao.resource.Out;
import org.netuno.tritao.resource.Req;
import org.netuno.tritao.resource.Setup;
import org.netuno.tritao.resource.Val;

/**
 * Setup Runner Service
 * @author Eduardo Fonseca Velasques - @eduveks
 */
@Path("/org/netuno/tritao/dev/app/SetupRunner")
public class SetupRunner extends WebMaster {
    public void run() throws Exception {
        Header header = resource(Header.class);
        Setup setup = resource(Setup.class);
        Req req = resource(Req.class);
        if (!req.getString("secret").equalsIgnoreCase(setup.getSecret())
            && !Auth.isDevAuthenticated(getProteu(), getHili(), Auth.Type.SESSION, false)) {
            header.status(401);
            return;
        }
        Setup.RunResult setupResult = setup.run();
        if (setupResult == Setup.RunResult.Error) {
            header.status(500);
        }
        resource(Out.class).json(
            resource(Val.class)
                .map()
                .set("result", setup.run().getCode())
        );
    }
}
