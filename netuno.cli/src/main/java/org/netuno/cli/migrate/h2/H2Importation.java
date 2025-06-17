package org.netuno.cli.migrate.h2;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Optional;
import java.util.function.Function;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.netuno.cli.Config;

public class H2Importation {
    private static Logger logger = LogManager.getLogger(H2Importation.class);

    public static void imports(String app, H2Version version) {
        Path jar = version.getJAR();
        if (jar == null) {
            return;
        }
        Optional<String[]> ids = H2DatabaseMigration.getIDs();
        if (!ids.isPresent()) {
            return;
        }
        if (Config.getAppConfig().isEmpty()) {
            Config.loadAppConfigs();
        }
        boolean notFound = true;
        for (String key : Config.getAppConfig().keys()) {
            var appConfig = Config.getAppConfig().getValues(key);
            if (!app.equals("*") && !key.equalsIgnoreCase(app)) {
                continue;
            }
            notFound = false;
            H2ProcessInfo.create(
                    H2MigrationType.IMPORTATION,
                    ids.get()[ids.get().length - 1],
                    appConfig
            ).ifPresent((pi) -> {
                Function<String, Path> dbFilePath = (fileName) -> pi.dbPath().getParent().resolve(Path.of(fileName));
                var dbBackupPath = dbFilePath.apply(pi.dbName() +"-"+ pi.id() +".mv.db");
                try {
                    Files.move(
                            pi.dbPath(),
                            dbBackupPath,
                            StandardCopyOption.ATOMIC_MOVE
                    );
                } catch (IOException e) {
                    logger.warn("Fail to backup file "+ pi.dbPath().getFileName() +" to: "+ dbBackupPath, e);
                }
                var tracePath = dbFilePath.apply(pi.dbName() +".trace.db");
                if (Files.exists(tracePath)) {
                    var traceBackupPath = dbFilePath.apply(pi.dbName() + "-" + pi.id() + ".trace.db");
                    try {
                        Files.move(
                                tracePath,
                                traceBackupPath,
                                StandardCopyOption.ATOMIC_MOVE
                        );
                    } catch (IOException e) {
                        logger.warn("Fail to backup file "+ pi.dbPath().getFileName() +" to: "+ dbBackupPath, e);
                    }
                }
                H2Process.run(pi, version);
            });
        }
        if (!app.equals("*") && notFound) {
            logger.warn("Cannot import to application "+ app +" because the configuration was not found.");
        }
    }
}
