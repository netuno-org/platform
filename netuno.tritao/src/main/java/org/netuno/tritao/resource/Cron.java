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

package org.netuno.tritao.resource;

import org.apache.logging.log4j.LogManager;
import org.netuno.library.doc.*;
import org.netuno.proteu.Proteu;
import org.netuno.psamata.Values;
import org.netuno.tritao.Service;
import org.netuno.tritao.config.Config;
import org.netuno.tritao.hili.Hili;
import org.netuno.tritao.resource.event.AppEventType;
import org.netuno.tritao.resource.util.ResourceException;
import org.netuno.tritao.resource.event.AppEvent;

/**
 * CRON - Resource
 * @author Eduardo Fonseca Velasques - @eduveks
 */
@Resource(name = "cron")
@LibraryDoc(translations = {
        @LibraryTranslationDoc(
                language = LanguageDoc.PT,
                title = "Cron",
                introduction = "Recurso de agendamento de execuções periódicas. \n" +
                        "Este recurso utiliza a biblioteca do <a href=\"http://www.quartz-scheduler.org/\" target=\"_blank\">Quartz Scheduler</a> para o agendamento da execução." +
                        "\n" +
                        "Na utilização do Cron é preciso realizar a configuração da expressão que define quando o job será executado.\n" +
                        "Abaixo segue indicações de como a configuração deve ser realizada.\n" +
                        "\n" +
                        "#### Formato suportados das expressões Cron\n" +
                        "\n" +
                        "|NOME DO CAMPO |\tOBRIGATÓRIO |\tVALORES ACEITES | CARACTERES ESPECIAIS PERMITIDOS |\n" +
                        "| ------- |:------:|:-----------:|--------------|\n" +
                        "| Segundos |\tSIM |\t0-59\t| , - * / |\n" +
                        "| Minutos | SIM\t| 0-59\t| , - * / |\n" +
                        "| Horas\t| SIM\t| 0-23\t| , - * / |\n" +
                        "| Dia do Mês |\tSIM\t| 1-31\t| , - * ? / L W |\n" +
                        "| Mês\t| SIM\t| 1-12 or JAN-DEC\t| , - * / |\n" +
                        "| Dia da semana\t| SIM\t| 1-7 or SUN-SAT\t| , - * ? / L # |\n" +
                        "| Ano\t| NÃO\t| vazio, 1970-2099\t| , - * / |\n" +
                        "\n" +
                        "#### Exemplos de expressões Cron\n" +
                        "\n" +
                        "|EXPRESSÃO | SIGNIFICADO |\n" +
                        "| ------- |------|\n" +
                        "| 1 * * * * ?\t| Executa no segundo número 1 de cada minuto. |\n"+
                        "| 0 0 12 * * ?\t| Inicia às 12h00 todos os dias |\n" +
                        "| 0 15 10 ? * *\t| Inicia às 10h15 todos os dias |\n" +
                        "| 0 15 10 * * ?\t| Inicia às 10h15 todos os dias |\n" +
                        "| 0 15 10 * * ? *\t| Inicia às 10h15 todos os dias |\n" +
                        "| 0 15 10 * * ? 2005\t| Inicia às 10h15 todos os dias durante o ano de 2005 |\n" +
                        "| 0 * 14 * * ?\t| Inicia a cada minuto a começar às 14h00 e finaliza às 14h59, todos os dias |\n" +
                        "| 0 0/5 14 * * ?\t| Inicia a cada 5 minutos a começar às 14h00 e finaliza às 14h55, todos os dias |\n" +
                        "| 0 0/5 14,18 * * ?\t| Inicia a cada 5 minutos a começar às 14h00 e finaliza às 14h55. Posteriormente, inicia a cada 5 minutos a começar às 18h00 e finaliza às 18h55, todos os dias |\n" +
                        "| 0 0-5 14 * * ?\t| Inicia a cada minuto a começar às 14h00 e finaliza às 14h05, todos os dias |\n" +
                        "| 0 10,44 14 ? 3 WED | Inicia às 14h10 e às 14h44 a cada quarta-feira do mês de Março. |\n" +
                        "| 0 15 10 ? * MON-FRI\t| Inicia às 10h15 a cada dia útil da semana (de segunda a sexta-feira) |\n" +
                        "| 0 15 10 15 * ?\t| Inicia às 10h15 no dia 15 de cada mês |\n" +
                        "| 0 15 10 L * ?\t| Inicia às 10h15 no último dia de cada mês |\n" +
                        "| 0 15 10 L-2 * ?\t| Inicia às 10h15 no penúltimo dia de cada mês |\n" +
                        "| 0 15 10 ? * 6L\t| Inicia às 10h15 na última sexta-feira de cada mês |\n" +
                        "| 0 15 10 ? * 6L\t| Inicia às 10h15 na última sexta-feira de cada mês |\n" +
                        "| 0 15 10 ? * 6L 2002-2005\t| Inicia às 10h15 a cada última sexta-feira de cada mês durante o ano de 2002, 2003, 2004 e 2005 |\n" +
                        "| 0 15 10 ? * 6#3\t| Inicia às 10h15 na terceira sexta-feira de cada mês |\n" +
                        "| 0 0 12 1/5 * ?\t| Inicia às 12h00 a cada 5 dias de cada mês, a começar no primeiro dia de cada mês. |\n" +
                        "| 0 11 11 11 11 ?\t| Inicia a cada 11 de Novembro às 11h11. |",
                howToUse = { }
        ),
        @LibraryTranslationDoc(
                language = LanguageDoc.EN,
                title = "Cron",
                introduction = "Recourse to scheduling of periodic executions. \n" +
                        "This resource uses the library <a href=\"http://www.quartz-scheduler.org/\" target=\"_blank\">Quartz Scheduler</a> for execution scheduling." +
                        "\n" +
                        "When using Cron, you must configure the expression that defines when the job will run.\n" +
                        "Below are indications of how the configuration should be performed.\n" +
                        "\n" +
                        "#### Cron expressions supported formats\n" +
                        "\n" +
                        "|FIELD NAME |\tMANDATORY |\tACCEPTED VALUES | ALLOWED SPECIAL CHARACTERS |\n" +
                        "| ------- |:------:|:-----------:|--------------|\n" +
                        "| Seconds |\tYES |\t0-59\t| , - * / |\n" +
                        "| Minutes | YES\t| 0-59\t| , - * / |\n" +
                        "| Hours\t| YES\t| 0-23\t| , - * / |\n" +
                        "| Month's day |\tYES\t| 1-31\t| , - * ? / L W |\n" +
                        "| Month\t| YES\t| 1-12 or JAN-DEC\t| , - * / |\n" +
                        "| Weekday\t| YES\t| 1-7 or SUN-SAT\t| , - * ? / L # |\n" +
                        "| Year\t| NO\t| empty, 1970-2099\t| , - * / |\n" +
                        "\n" +
                        "#### Examples of Cron expressions\n" +
                        "\n" +
                        "|EXPRESSION | MEANING |\n" +
                        "| ------- |------|\n" +
                        "| 1 * * * * ?\t| It runs on the second number 1 of every minute. |\n"+
                        "| 0 0 12 * * ?\t| Starts at 12:00 a.m. every day |\n" +
                        "| 0 15 10 ? * *\t| Starts at 10:15 a.m. every day |\n" +
                        "| 0 15 10 * * ?\t| Starts at 10:15 a.m. every day |\n" +
                        "| 0 15 10 * * ? *\t| Starts at 10:15 a.m. every day |\n" +
                        "| 0 15 10 * * ? 2005\t| Starts at 10:15 a.m. every day during 2005 |\n" +
                        "| 0 * 14 * * ?\t| Starts every minute starting at 2:00 p.m. and ends at 2:59 p.m., every day |\n" +
                        "| 0 0/5 14 * * ?\t| Starts every 5 minutes starting at 2:00 p.m. and ends at 2:55 p.m. every day |\n" +
                        "| 0 0/5 14,18 * * ?\t| It starts every 5 minutes starting at 2:00 p.m. and ends at 2:55 p.m. Afterwards, it starts every 5 minutes starting at 6:00 p.m. and ends at 6:55 p.m., every day |\n" +
                        "| 0 0-5 14 * * ?\t| Starts every minute starting at 2:00 p.m. and ends at 2:05 p.m. every day |\n" +
                        "| 0 10,44 14 ? 3 WED | It starts at 2:10 p.m. and at 2:44 p.m. every Wednesday in March. |\n" +
                        "| 0 15 10 ? * MON-FRI\t| Starts at 10:15 a.m. every working day of the week (Monday to Friday) |\n" +
                        "| 0 15 10 15 * ?\t| Starts at 10:15 a.m. on the 15th of each month |\n" +
                        "| 0 15 10 L * ?\t| Starts at 10:15 a.m. on the last day of each month |\n" +
                        "| 0 15 10 L-2 * ?\t| Starts at 10:15 a.m. on the penultimate day of each month |\n" +
                        "| 0 15 10 ? * 6L\t| Starts at 10:15 a.m. on the last Friday of every month |\n" +
                        "| 0 15 10 ? * 6L\t| Starts at 10:15 a.m. on the last Friday of every month |\n" +
                        "| 0 15 10 ? * 6L 2002-2005\t| Starts at 10:15 a.m. every last Friday of every month during 2002, 2003, 2004 and 2005 |\n" +
                        "| 0 15 10 ? * 6#3\t| Starts at 10:15 a.m. on the third Friday of every month |\n" +
                        "| 0 0 12 1/5 * ?\t| It starts at 12:00 a.m. every 5 days of each month, starting on the first day of each month. |\n" +
                        "| 0 11 11 11 11 ?\t| It starts every 11th of November at 11:11 a.m. |",
                howToUse = { }
        )
})
public class Cron extends ResourceBase {
    private static String GLOBAL_SECRET = null;
    
    private static org.apache.logging.log4j.Logger logger = LogManager.getLogger(Cron.class);

    public Cron(Proteu proteu, Hili hili) {
        super(proteu, hili);
    }
    
    @AppEvent(type=AppEventType.BeforeEnvironment)
    private void beforeEnvironment() {
        Values cronConfig = getProteu().getConfig().getValues("_app:config").getValues("cron");
        if (cronConfig != null) {
            if (GLOBAL_SECRET == null) {
                GLOBAL_SECRET = resource(Random.class).initString().nextString();
            }
            getProteu().getConfig().set("_cron:jobs", cronConfig.getValues("jobs"));
            getProteu().getConfig().set("_cron:secret", cronConfig.getString("secret", GLOBAL_SECRET));
        }
    }
    
    @AppEvent(type=AppEventType.BeforeConfiguration)
    private void beforeConfiguration() {
        Values jobs = getProteu().getConfig().getValues("_cron:jobs");
        String secret = getProteu().getConfig().getString("_cron:secret");
        App app = resource(App.class);
        Values configCron = app.config.getValues("cron");
        if (configCron != null && configCron.isMap()) {
            if (jobs == null || !jobs.isList()) {
                Values configJobs = configCron.getValues("jobs");
                if (configJobs != null && configJobs.isList()) {
                    jobs = configJobs;
                    getProteu().getConfig().set("_cron:jobs", configJobs);
                }
            }
            if (!configCron.getString("secret").isEmpty()) {
                secret = configCron.getString("secret");
                getProteu().getConfig().set("_cron:secret", secret);
            }
        }
    }
    
    @AppEvent(type=AppEventType.BeforeInitialization)
    private void beforeInitialization() {
        config();
    }
    
    @AppEvent(type=AppEventType.BeforeServiceConfiguration)
    private void beforeServiceConfiguration() {
        if (!getProteu().getRequestAll().getString("job").isEmpty() && !getProteu().getRequestAll().getString("secret").isEmpty()) {
            Service service = Service.getInstance(getProteu());
            for (Values schedule : schedules().listOfValues()) {
                if (schedule.has("key", getProteu().getRequestAll().getString("job"))
                        && schedule.getString("url").endsWith(Config.getUrlServices(getProteu()) + service.path)
                        && schedule.getValues("params").has("secret", getProteu().getRequestAll().getString("secret"))) {
                    service.allow();
                }
            }
        }
    }

    @MethodDoc(
    		translations = {
		            @MethodTranslationDoc(
		                    language = LanguageDoc.PT,
		                    description = "Configura o agendamento periódico pela importação do ficheiro de configuração.",
		                    howToUse = {  }),
		            @MethodTranslationDoc(
		                    language = LanguageDoc.EN,
		                    description = "Configures periodic scheduling by importing the configuration file.",
		                    howToUse = {  })
		    },
    		parameters = {},
    		returns = {}
    )
    public void config() throws ResourceException {
        Values jobs = getProteu().getConfig().getValues("_cron:jobs");
        String secret = getProteu().getConfig().getString("_cron:secret");
        if (jobs != null) {
            if (jobs.size() > 0 && secret.isEmpty()) {
                logger.error("Cron jobs without the main secret.");
            }
            for (Object j : jobs) {
                if (j instanceof Values) {
                    Values job = (Values)j;
                    if (!job.getBoolean("enabled", true)) {
                        continue;
                    }
                    String url = job.getString("url");
                    Values params = job.getValues("params");
                    if (params == null) {
                        params = new Values().forceMap();
                    }
                    if (params.getString("secret").isEmpty()) {
                        params.set("secret", secret);
                    }
                    schedule(job.getString("name"), job.getString("config"), url, params);
                }
            }
        }
    }

    @MethodDoc(
    		translations = {
		            @MethodTranslationDoc(
		                    language = LanguageDoc.PT,
		                    description = "Cria um agendamento de execução de serviço.",
		                    howToUse = {
		                            @SourceCodeDoc(
		                            type = SourceCodeTypeDoc.JavaScript,
		                            code = "_cron.schedule(\"atualizaPrecos\", \"1 * * * * ?\", \"/services/jobs/atualiza-precos\",\n" +
		                                    "    _val.map()\n" +
		                                    "        .set(\"categoriaId\", 1)\n" +
		                                    "        .set(\"produtoId\", 1)\n" +
		                                    ")"
		                    ) }),
		            @MethodTranslationDoc(
		                    language = LanguageDoc.EN,
		                    description = "Creates a service execution schedule.",
		                    howToUse = {
		                            @SourceCodeDoc(
		                            type = SourceCodeTypeDoc.JavaScript,
		                            code = "_cron.schedule(\"updatePrices\", \"1 * * * * ?\", \"/services/jobs/update-prices\",\n" +
		                                    "    _val.map()\n" +
		                                    "        .set(\"categoryId\", 1)\n" +
		                                    "        .set(\"productId\", 1)\n" +
		                                    ")"
		                    ) })
		    },
    		parameters = {
    				@ParameterDoc(name = "key", translations = {
							@ParameterTranslationDoc(
    	                            language=LanguageDoc.PT,
    	                            name = "chave",
    	                            description = "Nome chave de identificação do job."
    	                    ),
    	                    @ParameterTranslationDoc(
    	                            language=LanguageDoc.EN,
    	                            description = "Job ID key name."
    	                    )
    				}),
    				@ParameterDoc(name = "config", translations = {
    	                    @ParameterTranslationDoc(
    	                            language=LanguageDoc.PT,
    	                            description = "Expressão horária de agendamento do Cron, ver tabela acima."
    	                    ),
    	                    @ParameterTranslationDoc(
    	                            language=LanguageDoc.EN,
    	                            description = "Cron Scheduling Time Expression, see table above."
    	                    )
    	            }),
    				@ParameterDoc(name = "url", translations = {
    	                    @ParameterTranslationDoc(
    	                            language=LanguageDoc.PT,
    	                            description = "URL a ser executada pelo job."
    	                    ),
    	                    @ParameterTranslationDoc(
    	                            language=LanguageDoc.EN,
    	                            description = "URL to be run by the job."
    	                    )
    	            }),
    				@ParameterDoc(name = "params", translations = {
    	                    @ParameterTranslationDoc(
    	                            language=LanguageDoc.PT,
    	                            description = "Dados a passar como parametro ao serviço."
    	                    ),
    	                    @ParameterTranslationDoc(
    	                            language=LanguageDoc.EN,
    	                            description = "Data to pass as a parameter to service."
    	                    )
    	            }) 
    		},
            returns = {
                    @ReturnTranslationDoc(
                            language = LanguageDoc.PT,
                            description = "A instância atual do Cron."
                    ),
                    @ReturnTranslationDoc(
                            language = LanguageDoc.EN,
                            description = "The current Cron instance."
                    )
            }
    )
    public Cron schedule(String key, String config, String url, Values params) throws ResourceException {
        try {
            if (params == null) {
                params = new Values()
                        .set("job", key)
                        .set("secret", getProteu().getConfig().getString("_cron:secret"));
            }
            if (!params.hasKey("job")) {
                params.set("job", key);
            }
            if (!params.hasKey("secret")) {
                params.set("secret", getProteu().getConfig().getString("_cron:secret"));
            }
            url = Config.getFullOrLocalURL(getProteu(), url);
            params.set("app", Config.getApp(getProteu()));
            org.netuno.cli.Cron.schedule(
                    Config.getApp(getProteu()),
                    key,
                    config,
                    url,
                    params.toJSON()
            );
        } catch (Exception e) {
            throw new ResourceException("cron.schedule("+ key +")", e);
        }
        return this;
    }

    public Cron schedule(String key, String config, String url) throws ResourceException {
        return schedule(key, config, url, null);
    }

    @MethodDoc(
            translations = {
                    @MethodTranslationDoc(
                            language = LanguageDoc.PT,
                            description = "Lista de todos os agendamentos de serviços da aplicação.",
                            howToUse = {  }),
                    @MethodTranslationDoc(
                            language = LanguageDoc.EN,
                            description = "List of all application service schedules.",
                            howToUse = {  })
            },
            parameters = {},
            returns = {
                    @ReturnTranslationDoc(
                            language = LanguageDoc.PT,
                            description = "A lista de todos os serviços agendados da aplicação que estão configurados no Cron."
                    ),
                    @ReturnTranslationDoc(
                            language = LanguageDoc.EN,
                            description = "The list of all scheduled services of the application that are configured in Cron."
                    )
            }
    )
    public Values schedules() throws ResourceException {
        try {
            return org.netuno.cli.Cron.schedules(Config.getApp(getProteu()));
        } catch (Exception e) {
            throw new ResourceException("cron.schedules()", e);
        }
    }

    @MethodDoc(
            translations = {
                    @MethodTranslationDoc(
                            language = LanguageDoc.PT,
                            description = "Muda o estado de execução para **pause**, neste estado não é executado o serviço associado.",
                            howToUse = {
                                    @SourceCodeDoc(
                                            type = SourceCodeTypeDoc.JavaScript,
                                            code = "_cron.pause(\"atualizaPrecos\")\n"
                                    ) }),
                    @MethodTranslationDoc(
                            language = LanguageDoc.EN,
                            description = "Changes the execution state to **pause**, in this state the associated service is not executed.",
                            howToUse = {
                                    @SourceCodeDoc(
                                            type = SourceCodeTypeDoc.JavaScript,
                                            code = "_cron.pause(\"pricesUpdate\")\n"
                                    ) })
            },
    		parameters = {
    				@ParameterDoc(name = "key", translations = {
    	                    @ParameterTranslationDoc(
    	                            language=LanguageDoc.PT,
    	                            name = "chave",
    	                            description = "Nome chave de identificação do job."
    	                    ),
    	                    @ParameterTranslationDoc(
    	                            language=LanguageDoc.EN,
    	                            description = "Job ID key name."
    	                    )
    	            })
    		},
            returns = {
                    @ReturnTranslationDoc(
                            language = LanguageDoc.PT,
                            description = "Se foi possível pausar o serviço agendado."
                    ),
                    @ReturnTranslationDoc(
                            language = LanguageDoc.EN,
                            description = "Whether it was possible to pause the scheduled service."
                    )
            }
    )
    public boolean pause(String key) throws ResourceException {
        try {
            return org.netuno.cli.Cron.pause(Config.getApp(getProteu()), key);
        } catch (Exception e) {
            throw new ResourceException("cron.pause("+ key +")", e);
        }
    }

    @MethodDoc(
            translations = {
                    @MethodTranslationDoc(
                            language = LanguageDoc.PT,
                            description = "Muda o estado de execução para **resume**, após um " +
                                    "agendamento estar no estado _pause_, este pode ser reativado por via de _resume_",
                            howToUse = {
                                    @SourceCodeDoc(
                                            type = SourceCodeTypeDoc.JavaScript,
                                            code = "_cron.resume(\"atualizaPrecos\")\n"
                                    ) }),
                    @MethodTranslationDoc(
                            language = LanguageDoc.EN,
                            description = "Changes the execution state to **resume**, after a schedule is in the _pause_ state, it can be reactivated via _resume_.",
                            howToUse = {
                                    @SourceCodeDoc(
                                            type = SourceCodeTypeDoc.JavaScript,
                                            code = "_cron.resume(\"pricesUpdate\")\n"
                                    ) })
    		},
    		parameters = {
    				@ParameterDoc(name = "key", translations = {
    	                    @ParameterTranslationDoc(
    	                            language=LanguageDoc.PT,
    	                            name = "chave",
    	                            description = "Nome chave de identificação do job."
    	                    ),
    	                    @ParameterTranslationDoc(
    	                            language=LanguageDoc.EN,
    	                            description = "Job ID key name."
    	                    )
    	            })
    		},
            returns = {
                    @ReturnTranslationDoc(
                            language = LanguageDoc.PT,
                            description = "Se foi possível continuar o serviço agendado."
                    ),
                    @ReturnTranslationDoc(
                            language = LanguageDoc.EN,
                            description = "Whether it was possible to continue the scheduled service."
                    )
            }
    )
    public boolean resume(String key) throws ResourceException {
        try {
            return org.netuno.cli.Cron.resume(Config.getApp(getProteu()), key);
        } catch (Exception e) {
            throw new ResourceException("cron.resume("+ key +")", e);
        }
    }

    @MethodDoc(
            translations = {
                    @MethodTranslationDoc(
                            language = LanguageDoc.PT,
                            description = "Interrompe um agendamento de execução de serviço.",
                            howToUse = {
                                    @SourceCodeDoc(
                                            type = SourceCodeTypeDoc.JavaScript,
                                            code = "_cron.interrupt(\"atualizaPrecos\")\n"
                                    ) }),
                    @MethodTranslationDoc(
                            language = LanguageDoc.EN,
                            description = "Interrupts a service execution schedule.",
                            howToUse = {
                                    @SourceCodeDoc(
                                            type = SourceCodeTypeDoc.JavaScript,
                                            code = "_cron.interrupt(\"pricesUpdate\")\n"
                                    ) })
            },
            parameters = {
                    @ParameterDoc(name = "key", translations = {
                            @ParameterTranslationDoc(
                                    language=LanguageDoc.PT,
                                    name = "chave",
                                    description = "Nome chave de identificação do job."
                            ),
                            @ParameterTranslationDoc(
                                    language=LanguageDoc.EN,
                                    description = "Job ID key name."
                            )
                    })
            },
            returns = {
                    @ReturnTranslationDoc(
                            language = LanguageDoc.PT,
                            description = "Se foi possível interromper o serviço agendado."
                    ),
                    @ReturnTranslationDoc(
                            language = LanguageDoc.EN,
                            description = "Whether it was possible to stop the scheduled service."
                    )
            }
    )
    public boolean interrupt(String key) throws ResourceException {
        try {
            return org.netuno.cli.Cron.interrupt(Config.getApp(getProteu()), key);
        } catch (Exception e) {
            throw new ResourceException("cron.interrupt("+ key +")", e);
        }
    }

    @MethodDoc(
            translations = {
                    @MethodTranslationDoc(
                            language = LanguageDoc.PT,
                            description = "Remove (apaga) um agendamento de execução de serviço.",
                            howToUse = {
                                    @SourceCodeDoc(
                                            type = SourceCodeTypeDoc.JavaScript,
                                            code = "_cron.delete(\"atualizaPrecos\")\n"
                                    ) }),
                    @MethodTranslationDoc(
                            language = LanguageDoc.EN,
                            description = "Removes a service execution schedule.",
                            howToUse = {
                                    @SourceCodeDoc(
                                            type = SourceCodeTypeDoc.JavaScript,
                                            code = "_cron.delete(\"pricesUpdate\")\n"
                                    ) })
            },
            parameters = {
                    @ParameterDoc(name = "key", translations = {
                            @ParameterTranslationDoc(
                                    language=LanguageDoc.PT,
                                    name = "chave",
                                    description = "Nome chave de identificação do job."
                            ),
                            @ParameterTranslationDoc(
                                    language=LanguageDoc.EN,
                                    description = "Job ID key name."
                            )
                    })
            },
            returns = {
                    @ReturnTranslationDoc(
                            language = LanguageDoc.PT,
                            description = "Se foi possível apagar o serviço agendado."
                    ),
                    @ReturnTranslationDoc(
                            language = LanguageDoc.EN,
                            description = "Whether it was possible to delete the scheduled service."
                    )
            }
    )
    public boolean delete(String key) throws ResourceException {
        try {
            return org.netuno.cli.Cron.delete(Config.getApp(getProteu()), key);
        } catch (Exception e) {
            throw new ResourceException("cron.delete("+ key +")", e);
        }
    }

    @MethodDoc(
            translations = {
                    @MethodTranslationDoc(
                            language = LanguageDoc.PT,
                            description = "Remove (apaga) um agendamento de execução de serviço.",
                            howToUse = {
                                    @SourceCodeDoc(
                                            type = SourceCodeTypeDoc.JavaScript,
                                            code = "_cron.remove(\"atualizaPrecos\")\n"
                                    ) }),
                    @MethodTranslationDoc(
                            language = LanguageDoc.EN,
                            description = "Removes a service execution schedule.",
                            howToUse = {
                                    @SourceCodeDoc(
                                            type = SourceCodeTypeDoc.JavaScript,
                                            code = "_cron.remove(\"pricesUpdate\")\n"
                                    ) })
            },
            parameters = {
                    @ParameterDoc(name = "key", translations = {
                            @ParameterTranslationDoc(
                                    language=LanguageDoc.PT,
                                    name = "chave",
                                    description = "Nome chave de identificação do job."
                            ),
                            @ParameterTranslationDoc(
                                    language=LanguageDoc.EN,
                                    description = "Job ID key name."
                            )
                    })
            },
            returns = {
                    @ReturnTranslationDoc(
                            language = LanguageDoc.PT,
                            description = "Se foi possível apagar o serviço agendado."
                    ),
                    @ReturnTranslationDoc(
                            language = LanguageDoc.EN,
                            description = "Whether it was possible to delete the scheduled service."
                    )
            }
    )
    public boolean remove(String key) throws ResourceException {
        try {
            return org.netuno.cli.Cron.delete(Config.getApp(getProteu()), key);
        } catch (Exception e) {
            throw new ResourceException("cron.remove("+ key +")", e);
        }
    }

    @MethodDoc(
            translations = {
                    @MethodTranslationDoc(
                            language = LanguageDoc.PT,
                            description = "Verifica se a chave do serviço agendado existe.",
                            howToUse = {
                                    @SourceCodeDoc(
                                            type = SourceCodeTypeDoc.JavaScript,
                                            code = "if (_cron.checkExists(\"atualizaPrecos\")) {\n" +
                                                    "    _out.print(\"O serviço agendado 'atualizaPrecos' existe!\")\n" +
                                                    "}\n"
                                    ) }),
                    @MethodTranslationDoc(
                            language = LanguageDoc.EN,
                            description = "Checks if the scheduled service key exists.",
                            howToUse = {
                                    @SourceCodeDoc(
                                            type = SourceCodeTypeDoc.JavaScript,
                                            code = "if (_cron.checkExists(\"pricesUpdate\")) {\n" +
                                                    "    _out.print(\"The scheduled service 'pricesUpdate' exists!\")\n" +
                                                    "}\n"
                                    ) })
            },
            parameters = {
                    @ParameterDoc(name = "key", translations = {
                            @ParameterTranslationDoc(
                                    language=LanguageDoc.PT,
                                    name = "chave",
                                    description = "Nome chave de identificação do job."
                            ),
                            @ParameterTranslationDoc(
                                    language=LanguageDoc.EN,
                                    description = "Job ID key name."
                            )
                    })
            },
            returns = {
                    @ReturnTranslationDoc(
                            language = LanguageDoc.PT,
                            description = "Se o serviço agendado foi encontrado."
                    ),
                    @ReturnTranslationDoc(
                            language = LanguageDoc.EN,
                            description = "Whether the scheduled service was found."
                    )
            }
    )
    public boolean checkExists(String key) throws ResourceException {
        try {
            return org.netuno.cli.Cron.checkExists(Config.getApp(getProteu()), key);
        } catch (Exception e) {
            throw new ResourceException("cron.checkExists("+ key +")", e);
        }
    }
}
