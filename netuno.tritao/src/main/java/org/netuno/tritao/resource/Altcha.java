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
import org.netuno.tritao.hili.Hili;
import org.netuno.tritao.resource.event.ResourceEvent;
import org.netuno.tritao.resource.event.ResourceEventType;

/**
 * Altcha.org - Resource
 * @author Eduardo Fonseca Velasques - @eduveks
 */
@Resource(name = "altcha")
@LibraryDoc(translations = {
        @LibraryTranslationDoc(
                language = LanguageDoc.PT,
                title = "Altcha",
                introduction = "Suporte ao [Altcha](https://altcha.org/), captcha open-source que implementa o " +
                        "mecanismo de segurança que evita robôs e garante interação humana na execução dos serviços " +
                        "na API REST.\n\n"
                        + "Exemplo da configuração completa com os valores padrão:\n"
                        + "```json\n"
                        + "\"altcha\": {\n"
                        + "    \"enabled\": true,\n"
                        + "    \"algorithm\": \"PBKDF2/SHA-256\",\n"
                        + "    \"secret\": \"POR_PADRÃO_O_SEGREDO_É_AUTOGERADO\",\n"
                        + "    \"keySecret\": \"POR_PADRÃO_A_CHAVE_DO_SEGREDO_É_AUTOGERADO\",\n"
                        + "    \"cost\": 10000,\n"
                        + "    \"counter\": 10000,\n"
                        + "    \"expires\": 3600, // EM SEGUNDOS\n"
                        + "    \"checkExpires\": true\n"
                        + "}\n"
                        + "```\n\n",
                howToUse = {
                        @SourceCodeDoc(
                                type = SourceCodeTypeDoc.JavaScript,
                                code = """
                                const altchaPayload = _req.getString("altcha");
                                if (_altcha.enabled() && !_altcha.verifySolution(altchaPayload)) {
                                    _header.status(409);
                                    _out.json(
                                        _val.map()
                                            .set("error", `invalid-altcha-payload`)
                                    );
                                    _exec.stop();
                                }
                                """
                        )
                }
        ),
        @LibraryTranslationDoc(
                language = LanguageDoc.EN,
                title = "Altcha",
                introduction = "Support for [Altcha](https://altcha.org/), an open-source captcha that implements a " +
                        "security mechanism to prevent bots and ensure human interaction in the execution of " +
                        "services on the REST API.\n\n"
                        + "Example of the complete configuration with default values:\n"
                        + "```json\n"
                        + "\"altcha\": {\n"
                        + "    \"enabled\": true,\n"
                        + "    \"algorithm\": \"PBKDF2/SHA-256\",\n"
                        + "    \"secret\": \"BY_DEFAULT_THE_SECRET_IS_AUTOGENERATE\",\n"
                        + "    \"keySecret\": \"BY_DEFAULT_THE_SECRET_KEY_IS_AUTOGENERATE\",\n"
                        + "    \"cost\": 10000,\n"
                        + "    \"counter\": 10000,\n"
                        + "    \"expires\": 3600, // a SECONDS\n"
                        + "    \"checkExpires\": true\n"
                        + "}\n"
                        + "```\n\n",
                howToUse = {
                        @SourceCodeDoc(
                                type = SourceCodeTypeDoc.JavaScript,
                                code = """
                                const altchaPayload = _req.getString("altcha");
                                if (_altcha.enabled() && !_altcha.verifySolution(altchaPayload)) {
                                    _header.status(409);
                                    _out.json(
                                        _val.map()
                                            .set("error", `invalid-altcha-payload`)
                                    );
                                    _exec.stop();
                                }
                                """
                        )
                }
        )
})
public class Altcha extends ResourceBase {
    private static org.apache.logging.log4j.Logger logger = LogManager.getLogger(Lang.class);
    private static String GLOBAL_SECRET = null;
    private static String GLOBAL_KEY_SECRET = null;
    private static final Integer GLOBAL_COUNTER = Double.valueOf((Math.random() * 100) + 50).intValue();
    public boolean enabled = true;
    public String algorithm = "";
    public String secret = "";
    public String keySecret = "";
    public int cost = 10_000;
    public int counter = 0;
    public int expires = 60;
    public boolean checkExpires = true;

    public Altcha(Proteu proteu, Hili hili) {
        super(proteu, hili);
    }

    @ResourceEvent(type= ResourceEventType.BeforeEnvironment)
    private void beforeEnvironment() {
        if (GLOBAL_SECRET == null) {
            GLOBAL_SECRET = resource(Random.class).initString(16, true).nextString();
        }
        if (GLOBAL_KEY_SECRET == null) {
            GLOBAL_KEY_SECRET = resource(Random.class).initString(16, true).nextString();
        }
        Values config = getProteu().getConfig().getValues("_app:config").getValues("altcha", Values.newMap());
        getProteu().getConfig().set("_altcha", config);
    }

    @ResourceEvent(type= ResourceEventType.AfterConfiguration)
    private void afterConfiguration() {
        load();
    }

    @ResourceEvent(type= ResourceEventType.AfterServiceConfiguration)
    private void afterServiceConfiguration() {
        load();
    }

    private org.altcha.altcha.v2.Altcha.Challenge createChallenge() {
        try {
            var options = new org.altcha.altcha.v2.Altcha.CreateChallengeOptions()
                    .algorithm(algorithm)
                    .cost(cost)
                    .hmacSignatureSecret(secret)
                    .expiresInSeconds(expires);
            if (algorithm.toUpperCase().startsWith("SHA-")) {
                options = options.counter(counter).hmacKeySignatureSecret(keySecret);
            }
            var challenge = org.altcha.altcha.v2.Altcha.createChallenge(options);
            return challenge;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @MethodDoc(translations = {
            @MethodTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Carrega as configurações relacionadas ao recurso Altcha na aplicação.",
                    howToUse = { }
            ),
            @MethodTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Loads the configurations related to the Altcha resource in the application.",
                    howToUse = { }
            )
    }, parameters = {
    }, returns = {
            @ReturnTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Instância atual do recurso Altcha."
            ),
            @ReturnTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Current instance of the Altcha resource."
            )
    })
    public Altcha load() {
        Values altcha = getProteu().getConfig().getValues("_altcha", Values.newMap());
        this.enabled = altcha.getBoolean("enabled", this.enabled);
        this.algorithm = altcha.getString("algorithm", "PBKDF2/SHA-256");
        this.secret = altcha.getString("secret", GLOBAL_SECRET);
        this.keySecret = altcha.getString("keySecret", GLOBAL_KEY_SECRET);
        this.cost = altcha.getInt("cost", this.cost);
        this.counter = altcha.getInt("counter", GLOBAL_COUNTER);
        this.expires = altcha.getInt("expires", this.expires) * 60;
        this.checkExpires = altcha.getBoolean("checkExpires", this.checkExpires);
        return this;
    }

    @MethodDoc(translations = {
            @MethodTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Obtém se o Altcha está habilitado.",
                    howToUse = { }
            ),
            @MethodTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Retrieves whether Altcha is enabled.",
                    howToUse = { }
            )
    }, parameters = {
    }, returns = {
            @ReturnTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Se está ativo ou não."
            ),
            @ReturnTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Whether it's active or not."
            )
    })
    public boolean enabled() {
        return enabled;
    }

    public boolean isEnabled() {
        return enabled;
    }

    @MethodDoc(translations = {
            @MethodTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Define se o Altcha está habilitado.",
                    howToUse = { }
            ),
            @MethodTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Defines whether Altcha is enabled.",
                    howToUse = { }
            )
    }, parameters = {
            @ParameterDoc(name = "enabled", translations = {
                    @ParameterTranslationDoc(
                            language = LanguageDoc.PT,
                            name = "habilitado",
                            description = "Se deve ser ativado ou não."
                    ),
                    @ParameterTranslationDoc(
                            language = LanguageDoc.EN,
                            description = "Whether it should be activated or not."
                    )
            })
    }, returns = {
            @ReturnTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Instância atual do recurso Altcha."
            ),
            @ReturnTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Current instance of the Altcha resource."
            )
    })
    public Altcha enabled(boolean enabled) {
        this.enabled = enabled;
        return this;
    }

    public Altcha setEnabled(boolean enabled) {
        this.enabled = enabled;
        return this;
    }

    @MethodDoc(translations = {
            @MethodTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "O tipo de algoritmo que é utilizado na encriptação.",
                    howToUse = { }
            ),
            @MethodTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "The type of algorithm used in the cryptography.",
                    howToUse = { }
            )
    }, parameters = {
    }, returns = {
            @ReturnTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "O código que identifica o algoritmo de encriptação."
            ),
            @ReturnTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "The code that identifies the encryption algorithm."
            )
    })
    public String algorithm() {
        return algorithm;
    }

    public String getAlgorithm() {
        return algorithm;
    }

    @MethodDoc(translations = {
            @MethodTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Define o tipo de algoritmo que é utilizado na encriptação.",
                    howToUse = { }
            ),
            @MethodTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "It defines the type of algorithm used in cryptography.",
                    howToUse = { }
            )
    }, parameters = {
            @ParameterDoc(name = "algorithm", translations = {
                    @ParameterTranslationDoc(
                            language = LanguageDoc.PT,
                            name = "algoritmo",
                            description = "Código que identifica o algoritmo de encriptação."
                    ),
                    @ParameterTranslationDoc(
                            language = LanguageDoc.EN,
                            description = "Code that identifies the encryption algorithm."
                    )
            })
    }, returns = {
            @ReturnTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Instância atual do recurso Altcha."
            ),
            @ReturnTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Current instance of the Altcha resource."
            )
    })
    public Altcha algorithm(String algorithm) {
        this.algorithm = algorithm;
        return this;
    }

    public Altcha setAlgorithm(String algorithm) {
        this.algorithm = algorithm;
        return this;
    }

    @MethodDoc(translations = {
            @MethodTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Obtém o código secreto que está sendo utilizado na encriptação.",
                    howToUse = { }
            ),
            @MethodTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Obtain the secret code that is being used in the encryption.",
                    howToUse = { }
            )
    }, parameters = {
    }, returns = {
            @ReturnTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "O código secreto utilizado na encriptação."
            ),
            @ReturnTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "The secret code used in cryptography."
            )
    })
    public String secret() {
        return secret;
    }

    public String getSecret() {
        return secret;
    }

    @MethodDoc(translations = {
            @MethodTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Define o código secreto que será utilizado na encriptação.",
                    howToUse = { }
            ),
            @MethodTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Define the secret code that will be used in the encryption.",
                    howToUse = { }
            )
    }, parameters = {
            @ParameterDoc(name = "secret", translations = {
                    @ParameterTranslationDoc(
                            language = LanguageDoc.PT,
                            name = "segredo",
                            description = "O código secreto que a encriptação deve usar."
                    ),
                    @ParameterTranslationDoc(
                            language = LanguageDoc.EN,
                            description = "The secret code that cryptography should use."
                    )
            })
    }, returns = {
            @ReturnTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Instância atual do recurso Altcha."
            ),
            @ReturnTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Current instance of the Altcha resource."
            )
    })
    public Altcha secret(String secret) {
        this.secret = secret;
        return this;
    }

    public Altcha setSecret(String secret) {
        this.secret = secret;
        return this;
    }

    @MethodDoc(translations = {
            @MethodTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Obtém a chave de assinatura secreta que está sendo utilizada na encriptação.",
                    howToUse = { }
            ),
            @MethodTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Obtain the secret signing key being used in the encryption.",
                    howToUse = { }
            )
    }, parameters = {
    }, returns = {
            @ReturnTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "A chave de assinatura secreta utilizada na encriptação."
            ),
            @ReturnTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "The secret signing key used in cryptography."
            )
    })
    public String keySecret() {
        return keySecret;
    }

    public String getKeySecret() {
        return keySecret;
    }

    @MethodDoc(translations = {
            @MethodTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Define a chave de assinatura secreta que será utilizada na encriptação.",
                    howToUse = { }
            ),
            @MethodTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Define the secret signing key that will be used in the encryption.",
                    howToUse = { }
            )
    }, parameters = {
            @ParameterDoc(name = "keySecret", translations = {
                    @ParameterTranslationDoc(
                            language = LanguageDoc.PT,
                            name = "chaveSecreta",
                            description = "Chave de assinatura secreta que a encriptação deve usar."
                    ),
                    @ParameterTranslationDoc(
                            language = LanguageDoc.EN,
                            description = "The secret signing key that the cryptography should use."
                    )
            })
    }, returns = {
            @ReturnTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Instância atual do recurso Altcha."
            ),
            @ReturnTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Current instance of the Altcha resource."
            )
    })
    public Altcha keySecret(String keySecret) {
        this.keySecret = keySecret;
        return this;
    }

    public Altcha setKeySecret(String keySecret) {
        this.keySecret = keySecret;
        return this;
    }

    @MethodDoc(translations = {
            @MethodTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Quantidade de interações para chegar ao prefixo da chave derivada.",
                    howToUse = { }
            ),
            @MethodTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Number of iterations required to arrive at the derived key prefix.",
                    howToUse = { }
            )
    }, parameters = {
    }, returns = {
            @ReturnTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Contagem de força bruta."
            ),
            @ReturnTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Brute force counting."
            )
    })
    public long cost() {
        return cost;
    }

    public long getCost() {
        return cost;
    }

    @MethodDoc(translations = {
            @MethodTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Define a quantidade de interações para chegar ao prefixo da chave derivada.",
                    howToUse = { }
            ),
            @MethodTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Defines the number of iterations required to arrive at the derived key prefix.",
                    howToUse = { }
            )
    }, parameters = {
            @ParameterDoc(name = "cost", translations = {
                    @ParameterTranslationDoc(
                            language = LanguageDoc.PT,
                            name = "custo",
                            description = "O número da contagem de força bruta."
                    ),
                    @ParameterTranslationDoc(
                            language = LanguageDoc.EN,
                            description = "The number of the brute force count."
                    )
            })
    }, returns = {
            @ReturnTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Instância atual do recurso Altcha."
            ),
            @ReturnTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Current instance of the Altcha resource."
            )
    })
    public Altcha cost(int cost) {
        this.cost = cost;
        return this;
    }

    public Altcha setCost(int cost) {
        this.cost = cost;
        return this;
    }

    @MethodDoc(translations = {
            @MethodTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "No modo determinístico, é calculado o prefixo da chave a partir do contador.",
                    howToUse = { }
            ),
            @MethodTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "In deterministic mode, the key prefix is calculated from the counter.",
                    howToUse = { }
            )
    }, parameters = {
    }, returns = {
            @ReturnTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Contador do modo determinístico."
            ),
            @ReturnTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Deterministic mode counter."
            )
    })
    public long counter() {
        return counter;
    }

    public long getCounter() {
        return counter;
    }

    @MethodDoc(translations = {
            @MethodTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Define no modo determinístico, o contador que é utilizado para calcular o prefixo da chave.",
                    howToUse = { }
            ),
            @MethodTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Defines, in deterministic mode, the counter that is used to calculate the key prefix.",
                    howToUse = { }
            )
    }, parameters = {
            @ParameterDoc(name = "counter", translations = {
                    @ParameterTranslationDoc(
                            language = LanguageDoc.PT,
                            name = "contador",
                            description = "O valor do contador do modo determinístico."
                    ),
                    @ParameterTranslationDoc(
                            language = LanguageDoc.EN,
                            description = "The counter value in deterministic mode."
                    )
            })
    }, returns = {
            @ReturnTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Instância atual do recurso Altcha."
            ),
            @ReturnTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Current instance of the Altcha resource."
            )
    })
    public Altcha counter(int counter) {
        this.counter = counter;
        return this;
    }

    public Altcha setCounter(int counter) {
        this.counter = counter;
        return this;
    }

    @MethodDoc(translations = {
            @MethodTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Obtém o tempo tempo de expiração em segundos.",
                    howToUse = { }
            ),
            @MethodTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Get the expiration time in seconds.",
                    howToUse = { }
            )
    }, parameters = {
    }, returns = {
            @ReturnTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "O tempo de expiração em segundos."
            ),
            @ReturnTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Expiration time in seconds."
            )
    })
    public int expires() {
        return expires;
    }

    public int getExpires() {
        return expires;
    }

    @MethodDoc(translations = {
            @MethodTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Define o tempo tempo de expiração em segundos.",
                    howToUse = { }
            ),
            @MethodTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Set the expiration time in seconds.",
                    howToUse = { }
            )
    }, parameters = {
            @ParameterDoc(name = "expires", translations = {
                    @ParameterTranslationDoc(
                            language = LanguageDoc.PT,
                            name = "expira",
                            description = "O limite em segundos para expiração."
                    ),
                    @ParameterTranslationDoc(
                            language = LanguageDoc.EN,
                            description = "The time limit in seconds for expiration."
                    )
            })
    }, returns = {
            @ReturnTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Instância atual do recurso Altcha."
            ),
            @ReturnTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Current instance of the Altcha resource."
            )
    })
    public Altcha expires(int expires) {
        this.expires = expires;
        return this;
    }

    public Altcha setExpires(int expires) {
        this.expires = expires;
        return this;
    }

    @MethodDoc(translations = {
            @MethodTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Obtém se checa o tempo de expiração ou não.",
                    howToUse = { }
            ),
            @MethodTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Obtains whether the expiration time should be checked or not.",
                    howToUse = { }
            )
    }, parameters = {
    }, returns = {
            @ReturnTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Checar o tempo de expiração ou não."
            ),
            @ReturnTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Check the expiration time or not."
            )
    })
    public boolean checkExpires() {
        return checkExpires;
    }

    public boolean isCheckExpires() {
        return checkExpires;
    }

    @MethodDoc(translations = {
            @MethodTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Define se checa o tempo de expiração ou não.",
                    howToUse = { }
            ),
            @MethodTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Defines whether to check the expiration time or not.",
                    howToUse = { }
            )
    }, parameters = {
            @ParameterDoc(name = "checkExpires", translations = {
                    @ParameterTranslationDoc(
                            language = LanguageDoc.PT,
                            name = "checaExpira",
                            description = "Se deve checar o tempo de expiração ou não."
                    ),
                    @ParameterTranslationDoc(
                            language = LanguageDoc.EN,
                            description = "Whether it should check the expiration time or not."
                    )
            })
    }, returns = {
            @ReturnTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Instância atual do recurso Altcha."
            ),
            @ReturnTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Current instance of the Altcha resource."
            )
    })
    public Altcha checkExpires(boolean checkExpires) {
        this.checkExpires = checkExpires;
        return this;
    }

    public Altcha setCheckExpires(boolean checkExpires) {
        this.checkExpires = checkExpires;
        return this;
    }

    @MethodDoc(translations = {
            @MethodTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Gera as configurações de desafio para o widget do frontend.",
                    howToUse = { }
            ),
            @MethodTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Generates the challenge settings for the frontend widget.",
                    howToUse = { }
            )
    }, parameters = {
    }, returns = {
            @ReturnTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Dados de configuração para o widget poder processar o resultado do desafio."
            ),
            @ReturnTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Configuration data for the widget to process the challenge result."
            )
    })
    public Values challenge() {
        try {
            return Values.fromJSON(createChallenge().toJson());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @MethodDoc(translations = {
            @MethodTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Verifica se o resultado do desafio gerado pelo widget no frontend é válido.",
                    howToUse = { }
            ),
            @MethodTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Checks if the result of the challenge generated by the widget on the frontend is valid.",
                    howToUse = { }
            )
    }, parameters = {
    }, returns = {
            @ReturnTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Se o resultado do desafio é válido ou não."
            ),
            @ReturnTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Whether the result of the challenge is valid or not."
            )
    })
    public boolean verifySolution(String payload) {
        try {
            org.altcha.altcha.v2.Altcha.VerifySolutionResult result;
            if (algorithm.toUpperCase().startsWith("SHA-")) {
                var parsedPayload = org.altcha.altcha.v2.Altcha.parsePayload(payload);
                result = org.altcha.altcha.v2.Altcha.verifySolution(parsedPayload.challenge(), parsedPayload.solution(), secret, keySecret, null);
            } else {
                result = org.altcha.altcha.v2.Altcha.verifySolution(payload, secret, org.altcha.altcha.v2.Altcha.kdf(algorithm));
            }
            if (result.expired()) {
                logger.debug("Challenge expired.");
            }
            if (Boolean.TRUE.equals(result.invalidSignature())) {
                logger.debug("The challenge was tampered with.");
            }
            return result.verified();
        } catch (Exception e) {
            logger.debug("Verifying solution for the payload: "+ payload, e);
            return false;
        }
    }
}
