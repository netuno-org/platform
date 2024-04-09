package org.netuno.cli.migrate.h2;

import java.io.IOException;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.netuno.cli.Config;
import org.netuno.cli.setup.Constants;
import org.netuno.psamata.Values;
import org.netuno.psamata.io.OutputStream;

public class H2Exportation {
    private static Logger logger = LogManager.getLogger(H2Exportation.class);

    public static void exports(String app, H2Version version) {
        var jar = version.getJAR();
        if (jar == null) {
            return;
        }
        if (Config.getAppConfig().isEmpty()) {
            Config.loadAppConfigs();
        }
        var id = LocalDateTime.now().format(DateTimeFormatter.ofPattern("YYYYMMddHHmmss"));
        var fileIdPath = Path.of(Constants.ROOT_PATH, H2DatabaseMigration.FILE_ID);
        try {
            var ids = H2DatabaseMigration.getIDs();
            var dataIDs = new Values();
            if (ids.isPresent()) {
                dataIDs = new Values(ids.get());
            }
            dataIDs.add(id);
            OutputStream.writeToFile(
                    dataIDs.toJSON(4),
                    fileIdPath,
                    false
            );
        } catch (IOException e) {
            logger.warn("Fail to write the migration file: "+ fileIdPath, e);
        }
        for (var key : Config.getAppConfig().keys()) {
            var appConfig = Config.getAppConfig().getValues(key);
            if (!app.equals("*") && !key.equalsIgnoreCase(app)) {
                continue;
            }
            H2ProcessInfo.create(
                    H2MigrationType.EXPORTATION,
                    id,
                    appConfig
            ).ifPresent((pi) -> {
                H2Process.run(pi, version);
            });
        }
    }
}
