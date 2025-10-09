package org.netuno.cli.migrate.h2;

import java.io.IOException;
import java.nio.file.Path;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.netuno.cli.Config;
import org.netuno.psamata.io.GlobFileVisitor;

public enum H2Version {
    V_1("h2-1.4.*.jar"),
    V_2("h2-2.{0,1}.*.jar"),
    V_2_2("h2-2.{2,3}.*.jar");

    private static Logger logger = LogManager.getLogger(H2Version.class);
    
    private String fileName;

    private H2Version(String fileName) {
        this.fileName = fileName;
    }

    public Path getJAR() {
        try {
            return GlobFileVisitor.find(Path.of(Config.getWebHome(), "WEB-INF", "lib"), "glob:**/"+ fileName);
        } catch (IOException e) {
            logger.debug("Fail to find the H2Database: "+ fileName, e);
        }
        return null;
    }

    public String getJDBCParameters() {
        if (this == V_1) {
            return "MODE=PostgreSQL;DATABASE_TO_UPPER=FALSE;CASE_INSENSITIVE_IDENTIFIERS=TRUE;";
        }
        return "MODE=PostgreSQL;CASE_INSENSITIVE_IDENTIFIERS=TRUE;DATABASE_TO_LOWER=TRUE;DEFAULT_NULL_ORDERING=HIGH;";
    }
}
