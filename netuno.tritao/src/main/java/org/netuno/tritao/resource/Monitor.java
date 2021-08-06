/*
 * Licensed to the Netuno.org under one or more
 * contributor license agreements. See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The Netuno.org licenses this file to You under the Apache License, Version
 * 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.netuno.tritao.resource;

import org.apache.logging.log4j.LogManager;
import org.netuno.library.doc.LanguageDoc;
import org.netuno.library.doc.LibraryDoc;
import org.netuno.library.doc.LibraryTranslationDoc;
import org.netuno.library.doc.MethodDoc;
import org.netuno.library.doc.MethodTranslationDoc;
import org.netuno.library.doc.ReturnTranslationDoc;
import org.netuno.library.doc.SourceCodeDoc;
import org.netuno.library.doc.SourceCodeTypeDoc;
import org.netuno.proteu.Proteu;
import org.netuno.psamata.Values;
import org.netuno.tritao.Service;
import org.netuno.tritao.config.Hili;
import org.netuno.tritao.resource.event.AppEvent;
import org.netuno.tritao.resource.event.AppEventType;
import org.netuno.tritao.resource.util.ResourceException;

/**
 * Monitor Performance Statistics - Resource
 * @author Eduardo Fonseca Velasques - @eduveks
 */
@Resource(name = "monitor")
@LibraryDoc(translations = {
        @LibraryTranslationDoc(
                language = LanguageDoc.PT,
                title = "Monitor",
                introduction = "Permite obter os dados de performance.",
                howToUse = { }
        ),
        @LibraryTranslationDoc(
                language = LanguageDoc.EN,
                title = "Monitor",
                introduction = "It allows to obtain the performance data.",
                howToUse = { }
        )
})
public class Monitor extends ResourceBase {
    private static org.apache.logging.log4j.Logger logger = LogManager.getLogger(Monitor.class);

    public Monitor(Proteu proteu, Hili hili) {
        super(proteu, hili);
    }
    
    @AppEvent(type=AppEventType.BeforeServiceConfiguration)
    private void beforeServiceConfiguration() {
        /*
        if (service.path.startsWith("jobs/") && !proteu.getRequestAll().getString("job").isEmpty()) {
            Values job = proteu.getConfig().getValues("_cron:jobs").find("name", proteu.getRequestAll().getString("job"));
            if (job != null) {
                if (job.getValues("params").has("secret", proteu.getRequestAll().getString("secret"))) {
                    service.allow();
                }
            } else {
                logger.warn("Job not found: "+ proteu.getRequestAll().getString("job"));
            }
        }
        */
        if (!getProteu().getConfig().getValues("_app:config").getString("name").equals(getProteu().getRequestAll().getString("app"))) {
            return;
        }
        if (!getProteu().getRequestAll().getString("alert").isEmpty() && !getProteu().getRequestAll().getString("secret").isEmpty()) {
            Values monitor = getProteu().getConfig().getValues("_app:config").getValues("monitor", new Values());
            Values alerts = monitor.getValues("alerts", new Values());
            String secret = monitor.getString("secret");
            alerts = getProteu().getConfig().getValues("_monitor:alerts", alerts);
            Values alert = alerts.find("name", getProteu().getRequestAll().getString("alert"));
            Service service = Service.getInstance(getProteu());
            if (alert != null && alert.getBoolean("enabled", true) && alert.getString("url").contains(org.netuno.tritao.config.Config.getUrlServices(getProteu()) + service.path)) {
                String alertSecret = alert.getString("secret");
                if ((alertSecret.isEmpty() && secret.equals(getProteu().getRequestAll().getString("secret")))
                        || (!alertSecret.isEmpty() && alert.has("secret", getProteu().getRequestAll().getString("secret")))) {
                    service.allow();
                }
            }
        }
    }
    
    @MethodDoc(translations = {
        @MethodTranslationDoc(
                language = LanguageDoc.PT,
                description = "Obtém os dados de performance como utilização do "
                        + "CPU, Memória e Disco do processo do servidor do Netuno como do computador no geral.",
                howToUse = {
                    @SourceCodeDoc(
                            type = SourceCodeTypeDoc.JavaScript,
                            code = "// Verifica o consumo atual, quanto tem livre e o máximo de memória:\n"
                                    + "_log.info(\n"
                                    + "    'Estado da Memória',\n"
                                    + "    _monitor.performanceData()\n"
                                    + "        .getValues('memory')\n"
                                    + "        .getValues('process')\n"
                                    + ")"
                    )
                }),
        @MethodTranslationDoc(
                language = LanguageDoc.EN,
                description = "Obtains performance data such as CPU, Memory and Disk utilization "
                        + "of the Neptune server process as well as the computer in general.",
                howToUse = { })
    }, parameters = { }, returns = {
        @ReturnTranslationDoc(
                language = LanguageDoc.PT,
                description = "Dados de performance como CPU, Memória e Disco."
        ),
        @ReturnTranslationDoc(
                language = LanguageDoc.EN,
                description = "Performance data such as CPU, Memory and Disk."
        )
    })
    public Values performanceData() throws ResourceException {
        try {
            return (Values)Class.forName("org.netuno.cli.monitoring.Stats")
                    .getMethod("performanceData")
                    .invoke(null);
        } catch (Exception e) {
            throw new ResourceException("monitor.performanceData()", e);
        }
    }
    
    @MethodDoc(translations = {
        @MethodTranslationDoc(
                language = LanguageDoc.PT,
                description = "Apresenta as informações estatísticas de performance, como os dados do CPU, Memória e Disco, no terminal e nos logs.",
                howToUse = {
                    @SourceCodeDoc(
                            type = SourceCodeTypeDoc.JavaScript,
                            code = "// Imprime no terminal e nos logs os dados de performance:\n"
                                    + "_monitor.stats()"
                    )
                }),
        @MethodTranslationDoc(
                language = LanguageDoc.EN,
                description = "Presents statistical information on performance, such as CPU, Memory and Disk data, in the terminal and in the logs.",
                howToUse = {
                    @SourceCodeDoc(
                            type = SourceCodeTypeDoc.JavaScript,
                            code = "// Prints the performance data in the terminal and in the logs:\n"
                                    + "_monitor.stats()"
                    )
                })
    }, parameters = { }, returns = { })
    public void stats() throws ResourceException {
        try {
            Class.forName("org.netuno.cli.monitoring.Stats")
                    .getMethod("execute")
                    .invoke(null);
        } catch (Exception e) {
            throw new ResourceException("monitor.log()", e);
        }
    }
}
