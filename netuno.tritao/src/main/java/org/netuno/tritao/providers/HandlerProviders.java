package org.netuno.tritao.providers;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.netuno.proteu.Proteu;
import org.netuno.tritao.Service;
import org.netuno.tritao.WebMaster;
import org.netuno.tritao.config.Hili;
import org.netuno.tritao.openapi.Schema;
import org.netuno.tritao.providers.google.Google;

public class HandlerProviders extends WebMaster {

    private static Logger logger = LogManager.getLogger(HandlerProviders.class);
    private Proteu proteu;
    private Hili hili;
    public Service service = null;

    public HandlerProviders(Service service, Proteu proteu, Hili hili) {
        super(proteu, hili);
        this.service = service;
        this.proteu = proteu;
        this.hili = hili;
    }
}
