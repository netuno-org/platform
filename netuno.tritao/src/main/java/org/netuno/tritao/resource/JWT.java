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

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.JwsHeader;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.MacAlgorithm;
import io.jsonwebtoken.security.SignatureAlgorithm;

import org.apache.logging.log4j.LogManager;
import org.netuno.library.doc.*;
import org.netuno.proteu.Proteu;
import org.netuno.psamata.Values;
import org.netuno.tritao.config.Config;
import org.netuno.tritao.hili.Hili;
import org.netuno.tritao.resource.util.ResourceException;

import javax.crypto.SecretKey;

import org.netuno.tritao.resource.event.AppEvent;
import org.netuno.tritao.resource.event.AppEventType;

/**
 * JWT (JSON Web Token) - Resource
 * @author Eduardo Fonseca Velasques - @eduveks
 * @author Érica Ferreira
 */
@Resource(name = "jwt")
@LibraryDoc(translations = {
        @LibraryTranslationDoc(
                language = LanguageDoc.PT,
                title = "JWT",
                introduction = "Manipulação de JSON Web Tokens.",
                howToUse = { }
        ),
        @LibraryTranslationDoc(
                language = LanguageDoc.EN,
                title = "JWT",
                introduction = "Handling JSON Web Tokens.",
                howToUse = { }
        )
})
public class JWT extends ResourceBase {
    private static org.apache.logging.log4j.Logger logger = LogManager.getLogger(JWT.class);
    private boolean enabled = false;
    private SecretKey key = null;

    public JWT(Proteu proteu, Hili hili) {
        super(proteu, hili);
        if (!Config.isAppConfigLoaded(proteu)) {
            return;
        }
        isEnabled();
    }

    public JWT(Proteu proteu, Hili hili, SecretKey key) {
        super(proteu, hili);
        this.key = key;
        isEnabled();
    }
    
    @AppEvent(type=AppEventType.BeforeEnvironment)
    private void beforeEnvironment() {
        getProteu().getConfig().set("_jwt", getProteu().getConfig().getValues("_app:config").getValues("jwt"));
        init();
    }
    
    @AppEvent(type=AppEventType.BeforeServiceConfiguration)
    private void beforeServiceConfiguration() {
        init();
    }

    public JWT init() throws ResourceException {
        return new JWT(getProteu(), getHili());
    }

    public JWT init(String secret) throws ResourceException {
        if (secret.isEmpty() || secret.length() < 16) {
            throw new ResourceException("JWT secret is weak! Please choose a more secure secret with at least 16 characters.");
        }
        JWT jwt = new JWT(getProteu(), getHili());
        jwt.setHMACKeyFromSecret(secret);
        return jwt;
    }

    public JWT init(SecretKey key) throws ResourceException {
        JWT jwt = new JWT(getProteu(), getHili());
        jwt.key(key);
        return jwt;
    }

    @MethodDoc(
        translations = {
            @MethodTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Verifica se o JWT está ativo.",
                    howToUse = {}),
            @MethodTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Verify if the JWT is enable.",
                    howToUse = {})
        },
        parameters = {},
        returns = {
            @ReturnTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Retorna se está ativado."
            ),
            @ReturnTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Returns if is enabled."
            )
        }
    )
    public boolean enabled() {
        return enabled;
    }
    public boolean isEnabled() {
        return enabled();
    }

    public JWT enabled(boolean enabled) {
        this.enabled = enabled;
        return this;
    }
    public JWT setEnabled(boolean enabled) {
        return enabled(enabled);
    }

    public JWT setHMACKeyFromSecret(String secret) {
        this.key = Keys.hmacShaKeyFor(secret.getBytes());
        return this;
    }

    public SecretKey getHMACKeyFromSecret(String secret) {
        return Keys.hmacShaKeyFor(secret.getBytes());
    }

    public SecretKey key() {
        return key;
    }

    public SecretKey getKey() {
        return key();
    }

    public JWT key(SecretKey key) {
        this.key = key;
        return this;
    }

    public JWT setKey(SecretKey key) {
        return key(key);
    }

    @MethodDoc(translations = {
            @MethodTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Gera um token através do jwtBuilder.",
                    howToUse = {}),
            @MethodTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Generates a token through jwtBuilder.",
                    howToUse = {})
    },
            parameters = {
                    @ParameterDoc(name = "Values", translations = {
                            @ParameterTranslationDoc(
                                    language=LanguageDoc.PT,
                                    name = "valores",
                                    description = "Valores para o jwtBuilder."
                            ),
                            @ParameterTranslationDoc(
                                    language=LanguageDoc.EN,
                                    description = "Values for the jwtBuilder."
                            )
                    })
            },
            returns = {
                    @ReturnTranslationDoc(
                            language = LanguageDoc.PT,
                            description = "Retorna o que foi gerado."
                    ),
                    @ReturnTranslationDoc(
                            language = LanguageDoc.EN,
                            description = "Returns the generation."
                    )
            }
    )
    public String token(Values data) {
        JwtBuilder jwtBuilder = Jwts.builder();
        jwtBuilder.claim("token_type", "Bearer");
        /*jwtBuilder.claim("expires_in", 86400);
        JWT refreshToken = init();
        refreshToken.token(
                new Values()
                        .set("expires", System.currentTimeMillis() + 600000)
                        .set("expires_in", System.currentTimeMillis() + 600000)
        );*/
        jwtBuilder.claim("uid", new UID(getProteu(), getHili()).generate());
        for (String key : data.keys()) {
            jwtBuilder.claim(key, data.get(key));
        }
        return jwtBuilder.signWith(key()).compact();
    }

    @MethodDoc(translations = {
            @MethodTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Codifica o valor do body inserido.",
                    howToUse = {}),
            @MethodTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Encodes the body value inserted.",
                    howToUse = {})
    },
            parameters = {
                    @ParameterDoc(name = "body", translations = {
                            @ParameterTranslationDoc(
                                    language=LanguageDoc.PT,
                                    name = "corpo",
                                    description = "Valor do corpo."
                            ),
                            @ParameterTranslationDoc(
                                    language = LanguageDoc.EN,
                                    description = "Body value."
                            )

                    })
            }, returns = {
            @ReturnTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Retorna o valores codificado."
            ),
            @ReturnTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Returns the value enconded."
            )
    })
    public String encode(Values body) {
        return encode(new Values(), body);
    }

    @MethodDoc(translations = {
            @MethodTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Codifica os valores do header e do body inseridos.",
                    howToUse = {}),
            @MethodTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Encodes the values of the header and body inserted.",
                    howToUse = {})
    },
            parameters = {
                    @ParameterDoc(name = "header", translations = {
                            @ParameterTranslationDoc(
                                    language = LanguageDoc.PT,
                                    name = "cabeçalho",
                                    description = "Valor do cabeçalho."
                            ),
                            @ParameterTranslationDoc(
                                    language = LanguageDoc.EN,
                                    description = "Header value."
                            )
                    }),
                    @ParameterDoc(name = "body", translations = {
                            @ParameterTranslationDoc(
                                    language=LanguageDoc.PT,
                                    name = "corpo",
                                    description = "Valor do corpo."
                            ),
                            @ParameterTranslationDoc(
                                    language = LanguageDoc.EN,
                                    description = "Body value."
                            )

                    })
            }, returns = {
            @ReturnTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Retorna os valores codificados."
            ),
            @ReturnTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Returns the values encoded."
            )
    })
    public String encode(Values header, Values body) {
        JwtBuilder jwtBuilder = Jwts.builder();
        header.keys().forEach(key -> {
            jwtBuilder.header().add(key, header.get(key));
        });
        body.keys().forEach(key -> {
            jwtBuilder.claim(key, body.get(key));
        });
        return jwtBuilder.signWith(key()).compact();
    }
    @MethodDoc(translations = {
            @MethodTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Codifica os valores do header e do body inseridos.",
                    howToUse = {}),
            @MethodTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Encodes the values of the header and body inserted.",
                    howToUse = {})
    },
            parameters = {
                    @ParameterDoc(name = "token", translations = {
                            @ParameterTranslationDoc(
                                    language=LanguageDoc.PT,
                                    name = "token",
                                    description = "Código de acesso."
                            ),
                            @ParameterTranslationDoc(
                                    language = LanguageDoc.EN,
                                    description = "Acess code."
                            )

                    })
            }, returns = {
            @ReturnTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Retorna os valores decodificados."
            ),
            @ReturnTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Returns the values decoded."
            )
    })
    public Values decode(String token) {
        Jws<Claims> jwtParts = Jwts.parser().verifyWith(key()).build().parseSignedClaims(token);
        JwsHeader jwtHeader = jwtParts.getHeader();
        Claims jwtPayload = jwtParts.getPayload();
        Values header = new Values();
        jwtHeader.keySet().forEach(key -> {
            header.set(key.toString(), jwtHeader.get(key));
        });
        Values body = new Values();
        jwtPayload.keySet().forEach(key -> {
            body.set(key, jwtPayload.get(key));
        });
        return new Values().set("header", header)
                .set("body", body)
                .set("signature", jwtParts.getDigest());
    }

    public Values data(String token) {
        try {
            Claims claims = Jwts.parser().verifyWith(key()).build().parseSignedClaims(token).getPayload();
            Values data = new Values();
            for (String key : claims.keySet()) {
                data.set(key, claims.get(key));
            }
            return data;
        } catch (Exception e) {
            logger.fatal(e);
            return null;
        }
    }

    @MethodDoc(translations = {
            @MethodTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Obtém o tipo de algoritmo para assinatura do tipo ECDSA.",
                    howToUse = {}),
            @MethodTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Gets the type of algorithm for signing of type ECDSA.",
                    howToUse = {})
    },
            parameters = {
                    @ParameterDoc(name = "bits", translations = {
                            @ParameterTranslationDoc(
                                    language=LanguageDoc.PT,
                                    name = "bits",
                                    description = "Quantidade de bits do algoritmo de assinatura, pode ser 256, 384 ou 512."
                            ),
                            @ParameterTranslationDoc(
                                    language=LanguageDoc.EN,
                                    description = "Number of bits in the signature algorithm, it can be 256, 384 or 512."
                            )
                    })
            },
            returns = {
                    @ReturnTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Retorna o objeto do tipo de algoritmo da assinatura."
            ),
                    @ReturnTranslationDoc(
                            language = LanguageDoc.EN,
                            description = "Returns the signature algorithm type object."
                    )}
    )
    public SignatureAlgorithm algorithmES(int bits) {
        try {
            return (SignatureAlgorithm)Jwts.SIG.class.getDeclaredField("ES"+ bits).get(null);
        } catch (IllegalAccessException | NoSuchFieldException e) {
            throw new ResourceException(e.toString() +" >> The quantity of bits available in the signature algorithm is 256, 384, or 512.");
        }
    }

    @MethodDoc(translations = {
        @MethodTranslationDoc(
                language = LanguageDoc.PT,
                description = "Obtém o tipo de algoritmo para assinatura do tipo HMAC.",
                howToUse = {}),
        @MethodTranslationDoc(
                language = LanguageDoc.EN,
                description = "Gets the type of algorithm for signing of type HMAC.",
                howToUse = {})
    },
        parameters = {
                @ParameterDoc(name = "bits", translations = {
                        @ParameterTranslationDoc(
                                language=LanguageDoc.PT,
                                name = "bits",
                                description = "Quantidade de bits do algoritmo de assinatura, pode ser 256, 384 ou 512."
                        ),
                        @ParameterTranslationDoc(
                                language=LanguageDoc.EN,
                                description = "Number of bits in the signature algorithm, it can be 256, 384 or 512."
                        )
                })
        },
        returns = {
                @ReturnTranslationDoc(
                        language = LanguageDoc.PT,
                        description = "Retorna o objeto do tipo de algoritmo da assinatura."
                ),
                @ReturnTranslationDoc(
                        language = LanguageDoc.EN,
                        description = "Returns the signature algorithm type object."
                )}
    )
    public MacAlgorithm algorithmHS(int bits) {
        try {
            return (MacAlgorithm)Jwts.SIG.class.getDeclaredField("HS"+ bits).get(null);
        } catch (IllegalAccessException | NoSuchFieldException e) {
            throw new ResourceException(e.toString() +" >> The quantity of bits available in the signature algorithm is 256, 384, or 512.");
        }
    }
    
    @MethodDoc(translations = {
        @MethodTranslationDoc(
                language = LanguageDoc.PT,
                description = "Obtém o tipo de algoritmo para assinatura do tipo RSASS e MGF1.",
                howToUse = {}),
        @MethodTranslationDoc(
                language = LanguageDoc.EN,
                description = "Gets the type of algorithm for signing of type RSASS and MGF1.",
                howToUse = {})
        },
        parameters = {
                @ParameterDoc(name = "bits", translations = {
                        @ParameterTranslationDoc(
                                language=LanguageDoc.PT,
                                name = "bits",
                                description = "Quantidade de bits do algoritmo de assinatura, pode ser 256, 384 ou 512."
                        ),
                        @ParameterTranslationDoc(
                                language=LanguageDoc.EN,
                                description = "Number of bits in the signature algorithm, it can be 256, 384 or 512."
                        )
                })
        },
        returns = {
                @ReturnTranslationDoc(
                        language = LanguageDoc.PT,
                        description = "Retorna o objeto do tipo de algoritmo da assinatura."
                ),
                @ReturnTranslationDoc(
                        language = LanguageDoc.EN,
                        description = "Returns the signature algorithm type object."
                )}
    )
    public SignatureAlgorithm algorithmPS(int bits) {
        try {
            return (SignatureAlgorithm)Jwts.SIG.class.getDeclaredField("PS"+ bits).get(null);
        } catch (IllegalAccessException | NoSuchFieldException e) {
            throw new ResourceException(e.toString() +" >> The quantity of bits available in the signature algorithm is 256, 384, or 512.");
        }
    }

    @MethodDoc(translations = {
        @MethodTranslationDoc(
                language = LanguageDoc.PT,
                description = "Obtém o tipo de algoritmo para assinatura do tipo RSASSA-PKCS1-v1_5.",
                howToUse = {}),
        @MethodTranslationDoc(
                language = LanguageDoc.EN,
                description = "Gets the type of algorithm for signing of type RSASSA-PKCS1-v1_5.",
                howToUse = {})
        },
        parameters = {
                @ParameterDoc(name = "bits", translations = {
                        @ParameterTranslationDoc(
                                language=LanguageDoc.PT,
                                name = "bits",
                                description = "Quantidade de bits do algoritmo de assinatura, pode ser 256, 384 ou 512."
                        ),
                        @ParameterTranslationDoc(
                                language=LanguageDoc.EN,
                                description = "Number of bits in the signature algorithm, it can be 256, 384 or 512."
                        )
                })
        },
        returns = {
                @ReturnTranslationDoc(
                        language = LanguageDoc.PT,
                        description = "Retorna o objeto do tipo de algoritmo da assinatura."
                ),
                @ReturnTranslationDoc(
                        language = LanguageDoc.EN,
                        description = "Returns the signature algorithm type object."
                )}
    )
    public SignatureAlgorithm algorithmRS(int bits) {
        try {
            return (SignatureAlgorithm)Jwts.SIG.class.getDeclaredField("RS"+ bits).get(null);
        } catch (IllegalAccessException | NoSuchFieldException e) {
            throw new ResourceException(e.toString() +" >> The quantity of bits available in the signature algorithm is 256, 384, or 512.");
        }
    }
}
