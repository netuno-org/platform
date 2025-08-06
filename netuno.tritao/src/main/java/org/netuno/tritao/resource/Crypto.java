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

import org.apache.commons.codec.digest.Crypt;
import org.apache.commons.codec.digest.DigestUtils;
import org.netuno.library.doc.*;
import org.netuno.proteu.Proteu;
import org.netuno.psamata.PsamataException;
import org.netuno.psamata.crypto.AES256;
import org.netuno.psamata.crypto.BCrypt;
import org.netuno.psamata.crypto.DES;
import org.netuno.psamata.crypto.MD5;
import org.netuno.tritao.hili.Hili;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.UnsupportedEncodingException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.InvalidParameterSpecException;

import org.netuno.psamata.crypto.HMAC;

/**
 * Crypto - Resource
 * @author Eduardo Fonseca Velasques - @eduveks
 */
@Resource(name = "crypto")
@LibraryDoc(translations = {
        @LibraryTranslationDoc(
                language = LanguageDoc.PT,
                title = "Crypto",
                introduction = "Algoritmos de encriptação de dados.",
                howToUse = { }
        ),
        @LibraryTranslationDoc(
                language = LanguageDoc.EN,
                title = "Crypto",
                introduction = "Data encryption algorithms.",
                howToUse = { }
        )
})
public class Crypto extends ResourceBase {

    public Crypto(Proteu proteu, Hili hili) {
        super(proteu, hili);
    }

    @MethodDoc(translations = {
            @MethodTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Desencripta conteúdo codificado com AES256.",
                    howToUse = {}),
            @MethodTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Decrypts AES256-encoded content.",
                    howToUse = {})
    }, parameters = {
            @ParameterDoc(name = "key", translations = {
                    @ParameterTranslationDoc(
                            language = LanguageDoc.PT,
                            name = "chave",
                            description = "Chave utilizada na encriptação."
                    ),
                    @ParameterTranslationDoc(
                            language = LanguageDoc.EN,
                            description = "Key used in encryption."
                    )
            }),
            @ParameterDoc(name = "content", translations = {
                    @ParameterTranslationDoc(
                            language = LanguageDoc.PT,
                            name = "conteudo",
                            description = "Conteúdo de dados encriptado com AES256."
                    ),
                    @ParameterTranslationDoc(
                            language = LanguageDoc.EN,
                            description = "Data content encrypted with AES256."
                    )
            })
    }, returns = {
            @ReturnTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Conteúdo original desencriptado."
            ),
            @ReturnTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Decrypted original content."
            )
    })
    public String aes256Decrypt(String key, String content) throws NoSuchPaddingException, InvalidAlgorithmParameterException, NoSuchAlgorithmException, IllegalBlockSizeException, BadPaddingException, InvalidKeyException, InvalidKeySpecException {
        return AES256.decrypt(key, content);
    }

    @MethodDoc(translations = {
            @MethodTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Encripta os dados com o AES256.",
                    howToUse = {}),
            @MethodTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Encrypts data with AES256.",
                    howToUse = {})
    }, parameters = {
            @ParameterDoc(name = "key", translations = {
                    @ParameterTranslationDoc(
                            language = LanguageDoc.PT,
                            name = "chave",
                            description = "Chave secreta para a encriptação."
                    ),
                    @ParameterTranslationDoc(
                            language = LanguageDoc.EN,
                            description = "Secret key for encryption."
                    )
            }),
            @ParameterDoc(name = "content", translations = {
                    @ParameterTranslationDoc(
                            language = LanguageDoc.PT,
                            name = "conteudo",
                            description = "Conteúdo para ser encriptado com AES256."
                    ),
                    @ParameterTranslationDoc(
                            language = LanguageDoc.EN,
                            description = "Content to be encrypted with AES256."
                    )
            })
    }, returns = {
            @ReturnTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Dados encriptados com AES256."
            ),
            @ReturnTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Data encrypted with AES256."
            )
    })
    public String aes256Encrypt(String key, String content) throws NoSuchPaddingException, NoSuchAlgorithmException, IllegalBlockSizeException, BadPaddingException, InvalidKeyException, InvalidKeySpecException, InvalidParameterSpecException, UnsupportedEncodingException {
        return AES256.encrypt(key, content);
    }

    @MethodDoc(translations = {
            @MethodTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Codifica os dados com o HMAC256, que serve para garantir a integridade e autenticidade.",
                    howToUse = {}),
            @MethodTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "It encodes the data with HMAC256, which serves to guarantee integrity and authenticity.",
                    howToUse = {})
    }, parameters = {
            @ParameterDoc(name = "key", translations = {
                    @ParameterTranslationDoc(
                            language = LanguageDoc.PT,
                            name = "chave",
                            description = "Chave secreta para a codificação."
                    ),
                    @ParameterTranslationDoc(
                            language = LanguageDoc.EN,
                            description = "Secret key for encryption."
                    )
            }),
            @ParameterDoc(name = "content", translations = {
                    @ParameterTranslationDoc(
                            language = LanguageDoc.PT,
                            name = "conteudo",
                            description = "Conteúdo para ser codificado com HMAC256."
                    ),
                    @ParameterTranslationDoc(
                            language = LanguageDoc.EN,
                            description = "Content to be encoded with HMAC256."
                    )
            })
    }, returns = {
            @ReturnTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Dados codificados com HMAC256."
            ),
            @ReturnTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Data encoded with HMAC256."
            )
    })
    public byte[] hmac256(byte[] key, byte[] content) throws NoSuchAlgorithmException, InvalidKeyException {
        return HMAC.encrypt256(key, content);
    }

    //repetido?
    public byte[] hmac256(String key, String content) throws NoSuchAlgorithmException, InvalidKeyException, UnsupportedEncodingException {
        return HMAC.encrypt256(key, content);
    }

    @MethodDoc(translations = {
            @MethodTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Codifica os dados com o HMAC256 e converte para hexadecimal, que serve para garantir a integridade e autenticidade.",
                    howToUse = {}),
            @MethodTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Encodes data with HMAC256 and converts to hexadecimal, which serves to guarantee integrity and authenticity.",
                    howToUse = {})
    }, parameters = {
            @ParameterDoc(name = "key", translations = {
                    @ParameterTranslationDoc(
                            language = LanguageDoc.PT,
                            name = "chave",
                            description = "Chave secreta para a codificação."
                    ),
                    @ParameterTranslationDoc(
                            language = LanguageDoc.EN,
                            description = "Secret key for encryption."
                    )
            }),
            @ParameterDoc(name = "content", translations = {
                    @ParameterTranslationDoc(
                            language = LanguageDoc.PT,
                            name = "conteudo",
                            description = "Conteúdo para ser codificado com HMAC256."
                    ),
                    @ParameterTranslationDoc(
                            language = LanguageDoc.EN,
                            description = "Content to be encoded with HMAC256."
                    )
            })
    }, returns = {
            @ReturnTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Dados codificados com HMAC256 e em formato hexadecimal."
            ),
            @ReturnTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Data encoded with HMAC256 and in hexadecimal format."
            )
    })
    public String hmac256Hex(byte[] key, byte[] content) throws NoSuchAlgorithmException, InvalidKeyException {
        return HMAC.encrypt256Hex(key, content);
    }

    public String hmac256Hex(String key, String content) throws UnsupportedEncodingException, NoSuchAlgorithmException, InvalidKeyException {
        return HMAC.encrypt256Hex(key, content);
    }

    @MethodDoc(translations = {
            @MethodTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Codifica os dados com o HMAC256 e converte para Base64, que serve para garantir a integridade e autenticidade.",
                    howToUse = {}),
            @MethodTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Encodes data with HMAC256 and converts to Base64, which serves to guarantee integrity and authenticity.",
                    howToUse = {})
    }, parameters = {
            @ParameterDoc(name = "key", translations = {
                    @ParameterTranslationDoc(
                            language = LanguageDoc.PT,
                            name = "chave",
                            description = "Chave secreta para a codificação."
                    ),
                    @ParameterTranslationDoc(
                            language = LanguageDoc.EN,
                            description = "Secret key for encryption."
                    )
            }),
            @ParameterDoc(name = "content", translations = {
                    @ParameterTranslationDoc(
                            language = LanguageDoc.PT,
                            name = "conteudo",
                            description = "Conteúdo para ser codificado com HMAC256."
                    ),
                    @ParameterTranslationDoc(
                            language = LanguageDoc.EN,
                            description = "Content to be encoded with HMAC256."
                    )
            })
    }, returns = {
            @ReturnTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Dados codificados com HMAC256 e em formato Base64."
            ),
            @ReturnTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Data encoded with HMAC256 and in Base64 format."
            )
    })
    public String hmac256Base64(byte[] key, byte[] content) throws NoSuchAlgorithmException, InvalidKeyException {
        return HMAC.encrypt256Base64(key, content);
    }

    public String hmac256Base64(String key, String content) throws UnsupportedEncodingException, NoSuchAlgorithmException, InvalidKeyException {
        return HMAC.encrypt256Base64(key, content);
    }

    @MethodDoc(translations = {
            @MethodTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Codifica os dados com o SHA256 (SHA-2) e converte a encriptação binária em hexadecimal.",
                    howToUse = {}),
            @MethodTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Encodes data with SHA256 (SHA-2) and converts binary encryption to hexadecimal.",
                    howToUse = {})
    }, parameters = {
            @ParameterDoc(name = "content", translations = {
                    @ParameterTranslationDoc(
                            language = LanguageDoc.PT,
                            name = "conteudo",
                            description = "Conteúdo para ser encriptado."
                    ),
                    @ParameterTranslationDoc(
                            language = LanguageDoc.EN,
                            description = "Content to be encrypted."
                    )
            })
    }, returns = {
            @ReturnTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Conteúdo encriptado em SHA256 (SHA2) e convertido em hexadecimal."
            ),
            @ReturnTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Content encrypted in SHA256 (SHA2) and converted to hexadecimal."
            )
    })
    public String sha256(byte[] data) {
        return DigestUtils.sha256Hex(data);
    }

    public String sha256(String content) {
        return DigestUtils.sha256Hex(content);
    }

    @MethodDoc(translations = {
            @MethodTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Codifica os dados com o SHA256 (SHA-2) e retorna a encriptação binária.",
                    howToUse = {}),
            @MethodTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Encodes data with SHA256 (SHA-2) and returns binary encryption.",
                    howToUse = {})
    }, parameters = {
            @ParameterDoc(name = "content", translations = {
                    @ParameterTranslationDoc(
                            language = LanguageDoc.PT,
                            name = "conteudo",
                            description = "Conteúdo para ser encriptado."
                    ),
                    @ParameterTranslationDoc(
                            language = LanguageDoc.EN,
                            description = "Content to be encrypted."
                    )
            })
    }, returns = {
            @ReturnTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Dados binários do conteúdo encriptado em SHA256 (SHA-2)."
            ),
            @ReturnTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Binary data of the content encrypted in SHA256 (SHA-2)."
            )
    })
    public byte[] sha256AsBytes(byte[] data) {
        return DigestUtils.sha256(data);
    }

    public byte[] sha256AsBytes(String content) {
        return DigestUtils.sha256(content);
    }

    @MethodDoc(translations = {
            @MethodTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Codifica os dados com o SHA384 (SHA-2) e converte a encriptação binária em hexadecimal.",
                    howToUse = {}),
            @MethodTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Encodes data with SHA384 (SHA-2) and converts binary encryption to hexadecimal.",
                    howToUse = {})
    }, parameters = {
            @ParameterDoc(name = "content", translations = {
                    @ParameterTranslationDoc(
                            language = LanguageDoc.PT,
                            name = "conteudo",
                            description = "Conteúdo para ser encriptado."
                    ),
                    @ParameterTranslationDoc(
                            language = LanguageDoc.EN,
                            description = "Content to be encrypted."
                    )
            })
    }, returns = {
            @ReturnTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Conteúdo encriptado em SHA384 (SHA2) e convertido em hexadecimal."
            ),
            @ReturnTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Content encrypted in SHA384 (SHA2) and converted to hexadecimal."
            )
    })
    public String sha384(byte[] data) {
        return DigestUtils.sha384Hex(data);
    }

    public String sha384(String content) {
        return DigestUtils.sha384Hex(content);
    }

    @MethodDoc(translations = {
            @MethodTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Codifica os dados com o SHA384 (SHA-2) e retorna a encriptação binária.",
                    howToUse = {}),
            @MethodTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Encodes data with SHA384 (SHA-2) and returns binary encryption.",
                    howToUse = {})
    }, parameters = {
            @ParameterDoc(name = "content", translations = {
                    @ParameterTranslationDoc(
                            language = LanguageDoc.PT,
                            name = "conteudo",
                            description = "Conteúdo para ser encriptado."
                    ),
                    @ParameterTranslationDoc(
                            language = LanguageDoc.EN,
                            description = "Content to be encrypted."
                    )
            })
    }, returns = {
            @ReturnTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Dados binários do conteúdo encriptado em SHA384 (SHA-2)."
            ),
            @ReturnTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Binary data of the content encrypted in SHA384 (SHA-2)."
            )
    })
    public byte[] sha384AsBytes(byte[] data) {
        return DigestUtils.sha384(data);
    }

    public byte[] sha384AsBytes(String content) {return DigestUtils.sha384(content);}

    @MethodDoc(translations = {
            @MethodTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Codifica os dados com o SHA512 (SHA-2) e converte a encriptação binária em hexadecimal.",
                    howToUse = {}),
            @MethodTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Encodes data with SHA512 (SHA-2) and converts binary encryption to hexadecimal.",
                    howToUse = {})
    }, parameters = {
            @ParameterDoc(name = "content", translations = {
                    @ParameterTranslationDoc(
                            language=LanguageDoc.PT,
                            name = "conteudo",
                            description = "Conteúdo para ser encriptado."
                    ),
                    @ParameterTranslationDoc(
                            language=LanguageDoc.EN,
                            description = "Content to be encrypted."
                    )
            })
    }, returns = {
            @ReturnTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Conteúdo encriptado em SHA512 (SHA2) e convertido em hexadecimal."
            ),
            @ReturnTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Content encrypted in SHA512 (SHA2) and converted to hexadecimal."
            )
    })
    public String sha512(byte[] data) {
    	return DigestUtils.sha512Hex(data);
    }
    
    public String sha512(String content) {
    	return DigestUtils.sha512Hex(content);
    }

    @MethodDoc(translations = {
            @MethodTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Codifica os dados com o SHA512 (SHA-2) e retorna a encriptação binária.",
                    howToUse = {}),
            @MethodTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Encodes data with SHA512 (SHA-2) and returns binary encryption.",
                    howToUse = {})
    }, parameters = {
            @ParameterDoc(name = "content", translations = {
                    @ParameterTranslationDoc(
                            language=LanguageDoc.PT,
                            name = "conteudo",
                            description = "Conteúdo para ser encriptado."
                    ),
                    @ParameterTranslationDoc(
                            language=LanguageDoc.EN,
                            description = "Content to be encrypted."
                    )
            })
    }, returns = {
            @ReturnTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Dados binários do conteúdo encriptado em SHA512 (SHA-2)."
            ),
            @ReturnTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Binary data of the content encrypted in SHA512 (SHA-2)."
            )
    })
    public byte[] sha512AsBytes(byte[] data) {
    	return DigestUtils.sha512(data);
    }
    @MethodDoc(translations = {
            @MethodTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Codifica os dados com o SHA512 (SHA-512).",
                    howToUse = {}),
            @MethodTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Encodes data with SHA512 (SHA-512) and returns binary encryption.",
                    howToUse = {})
    }, parameters = {
            @ParameterDoc(name = "content", translations = {
                    @ParameterTranslationDoc(
                            language=LanguageDoc.PT,
                            name = "conteudo",
                            description = "Conteúdo para ser encriptado."
                    ),
                    @ParameterTranslationDoc(
                            language=LanguageDoc.EN,
                            description = "Content to be encrypted."
                    )
            })
    }, returns = {
            @ReturnTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Dados binários do conteúdo encriptado em SHA512 (SHA-2)."
            ),
            @ReturnTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Binary data of the content encrypted in SHA512 (SHA-2)."
            )
    })
    public byte[] sha512AsBytes(String content) {
    	return DigestUtils.sha512(content);
    }

    @MethodDoc(translations = {
            @MethodTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Codifica os dados com o desEncode.",
                    howToUse = {}),
            @MethodTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Encodes data with desEncode and returns binary encryption.",
                    howToUse = {})
    }, parameters = {
            @ParameterDoc(name = "key", translations = {
                    @ParameterTranslationDoc(
                            language = LanguageDoc.PT,
                            name = "chave",
                            description = "Chave secreta para a codificação."
                    ),
                    @ParameterTranslationDoc(
                            language = LanguageDoc.EN,
                            description = "Secret key for encryption."
                    )
            }),
            @ParameterDoc(name = "content", translations = {
                    @ParameterTranslationDoc(
                            language=LanguageDoc.PT,
                            name = "conteudo",
                            description = "Conteúdo para ser encriptado."
                    ),
                    @ParameterTranslationDoc(
                            language=LanguageDoc.EN,
                            description = "Content to be encrypted."
                    )
            }),
            @ParameterDoc(name = "charset", translations = {
                    @ParameterTranslationDoc(
                            language=LanguageDoc.PT,
                            description = "Conjunto de caracteres."
                    ),
                    @ParameterTranslationDoc(
                            language=LanguageDoc.EN,
                            description = "Charset."
                    )
            })
    }, returns = {
            @ReturnTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Dados binários do conteúdo encriptado em desEncode."
            ),
            @ReturnTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Binary data of the content encrypted in desEncode."
            )
    })
    public byte[] desEncode(String key, String content, String charset) throws PsamataException {
        return new DES(key, charset).encode(content);
    }
    public byte[] desEncode(String key, String content) throws PsamataException {
        return new DES(key).encode(content);
    }

    @MethodDoc(translations = {
            @MethodTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Codifica os dados com o desEncode.",
                    howToUse = {}),
            @MethodTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Encodes data with desEncode and returns string encryption.",
                    howToUse = {})
    }, parameters = {
            @ParameterDoc(name = "key", translations = {
                    @ParameterTranslationDoc(
                            language = LanguageDoc.PT,
                            name = "chave",
                            description = "Chave secreta para a codificação."
                    ),
                    @ParameterTranslationDoc(
                            language = LanguageDoc.EN,
                            description = "Secret key for encryption."
                    )
            }),
            @ParameterDoc(name = "content", translations = {
                    @ParameterTranslationDoc(
                            language=LanguageDoc.PT,
                            name = "conteudo",
                            description = "Conteúdo para ser encriptado."
                    ),
                    @ParameterTranslationDoc(
                            language=LanguageDoc.EN,
                            description = "Content to be encrypted."
                    )
            }),
            @ParameterDoc(name = "charset", translations = {
                    @ParameterTranslationDoc(
                            language=LanguageDoc.PT,
                            description = "Conjunto de caracteres."
                    ),
                    @ParameterTranslationDoc(
                            language=LanguageDoc.EN,
                            description = "Charset."
                    )
            })
    }, returns = {
            @ReturnTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "String do conteúdo encriptado em desEncode."
            ),
            @ReturnTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "String data of the content encrypted in desEncode."
            )
    })
    public String desEncodeBase64(String key, String content, String charset) throws PsamataException {
        Convert convert = resource(Convert.class);
        return convert.toBase64(new DES(key, charset).encode(content));
    }
    @MethodDoc(translations = {
            @MethodTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Codifica os dados com o Desdecode (Des).",
                    howToUse = {}),
            @MethodTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Encodes data with Desdecode (Des).",
                    howToUse = {})
    }, parameters = {
            @ParameterDoc(name = "content", translations = {
                    @ParameterTranslationDoc(
                            language=LanguageDoc.PT,
                            name = "conteudo",
                            description = "Conteúdo para ser encriptado."
                    ),
                    @ParameterTranslationDoc(
                            language=LanguageDoc.EN,
                            description = "Content to be encrypted."
                    )
            }),
            @ParameterDoc(name = "data", translations = {
                    @ParameterTranslationDoc(
                            language=LanguageDoc.PT,
                            name = "dados",
                            description = "Dados para serem encriptados."
                    ),
                    @ParameterTranslationDoc(
                            language=LanguageDoc.EN,
                            description = "Data to be encrypted."
                    )
            })
    }, returns = {
            @ReturnTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Dados binários do conteúdo encriptado em Desdecode (Des)."
            ),
            @ReturnTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Binary data of the content encrypted in Desdecode (Des)."
            )
    })
    public String desDecode(String key, byte[] data) throws PsamataException {
        return new DES(key).decode(data);
    }
    @MethodDoc(translations = {
            @MethodTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Codifica os dados com o SHA512 (SHA-2) e retorna a encriptação binária.",
                    howToUse = {}),
            @MethodTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Encodes data with Desdecode (Des).",
                    howToUse = {})
    }, parameters = {
            @ParameterDoc(name = "content", translations = {
                    @ParameterTranslationDoc(
                            language=LanguageDoc.PT,
                            name = "conteudo",
                            description = "Conteúdo para ser encriptado."
                    ),
                    @ParameterTranslationDoc(
                            language=LanguageDoc.EN,
                            description = "Content to be encrypted."
                    )
            }),
            @ParameterDoc(name = "data", translations = {
                    @ParameterTranslationDoc(
                            language=LanguageDoc.PT,
                            name = "dados",
                            description = "Dados para serem encriptados."
                    ),
                    @ParameterTranslationDoc(
                            language=LanguageDoc.EN,
                            description = "Data to be encrypted."
                    )
            }),
            @ParameterDoc(name = "charset", translations = {
                    @ParameterTranslationDoc(
                            language=LanguageDoc.PT,
                            description = "Conjunto de caracteres."
                    ),
                    @ParameterTranslationDoc(
                            language=LanguageDoc.EN,
                            description = "Charset."
                    )
            })
    }, returns = {
            @ReturnTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Dados binários do conteúdo encriptado em Desdecode (Des)."
            ),
            @ReturnTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Binary data of the content encrypted in Desdecode (Des)."
            )
    })
    public String desDecode(String key, byte[] data, String charset) throws PsamataException {
        return new DES(key, charset).decode(data);
    }
    @MethodDoc(translations = {
            @MethodTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Codifica os dados com o SHA512 (SHA-2) e retorna a encriptação binária.",
                    howToUse = {}),
            @MethodTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Encodes data with Desdecode (Des) e convertido em base 64.",
                    howToUse = {})
    }, parameters = {
            @ParameterDoc(name = "content", translations = {
                    @ParameterTranslationDoc(
                            language=LanguageDoc.PT,
                            name = "conteudo",
                            description = "Conteúdo para ser encriptado."
                    ),
                    @ParameterTranslationDoc(
                            language=LanguageDoc.EN,
                            description = "Content to be encrypted."
                    )
            }),
            @ParameterDoc(name = "data", translations = {
                    @ParameterTranslationDoc(
                            language=LanguageDoc.PT,
                            name = "dados",
                            description = "Dados para serem encriptados."
                    ),
                    @ParameterTranslationDoc(
                            language=LanguageDoc.EN,
                            description = "Data to be encrypted."
                    )
            }),
            @ParameterDoc(name = "charset", translations = {
                    @ParameterTranslationDoc(
                            language=LanguageDoc.PT,
                            description = "Conjunto de caracteres."
                    ),
                    @ParameterTranslationDoc(
                            language=LanguageDoc.EN,
                            description = "Charset."
                    )
            })
    }, returns = {
            @ReturnTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Dados binários do conteúdo encriptado em Desdecode (Des) e convertidos em base 64."
            ),
            @ReturnTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Binary data of the content encrypted in Desdecode (Des) and coverted in base 64."
            )
    })
    public String desDecodeBase64(String key, String data, String charset) throws PsamataException {
        Convert convert = resource(Convert.class);
        return new DES(key, charset).decode(convert.fromBase64AsBytes(data));
    }
    @MethodDoc(translations = {
            @MethodTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Codifica os dados com o cryptHash.",
                    howToUse = {}),
            @MethodTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Encodes data with cryptHash.",
                    howToUse = {})
    }, parameters = {
            @ParameterDoc(name = "content", translations = {
                    @ParameterTranslationDoc(
                            language=LanguageDoc.PT,
                            name = "conteudo",
                            description = "Conteúdo para ser encriptado."
                    ),
                    @ParameterTranslationDoc(
                            language=LanguageDoc.EN,
                            description = "Content to be encrypted."
                    )
            })
    }, returns = {
            @ReturnTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Dados do conteúdo encriptado em cryptHash."
            ),
            @ReturnTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Data of the content encrypted in cryptHash."
            )
    })
    public String bcryptSalt() {
        return BCrypt.gensalt();
    }
    @MethodDoc(translations = {
            @MethodTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Codifica os dados bcryptSalt.",
                    howToUse = {}),
            @MethodTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Encodes data with bcryptSalt.",
                    howToUse = {})
    }, parameters = {
            @ParameterDoc(name = "content", translations = {
                    @ParameterTranslationDoc(
                            language=LanguageDoc.PT,
                            name = "conteudo",
                            description = "Conteúdo para ser encriptado."
                    ),
                    @ParameterTranslationDoc(
                            language=LanguageDoc.EN,
                            description = "Content to be encrypted."
                    )
            })
    }, returns = {
            @ReturnTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Dados do conteúdo encriptado em bcryptSalt."
            ),
            @ReturnTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Data of the content encrypted in bcryptSalt."
            )
    })
    public String bcryptSalt(int rounds) {
        return BCrypt.gensalt(rounds);
    }
    @MethodDoc(translations = {
            @MethodTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Codifica os dados bcryptSalt.",
                    howToUse = {}),
            @MethodTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Encodes data with bcryptSalt.",
                    howToUse = {})
    }, parameters = {
            @ParameterDoc(name = "content", translations = {
                    @ParameterTranslationDoc(
                            language=LanguageDoc.PT,
                            name = "conteudo",
                            description = "Conteúdo para ser encriptado."
                    ),
                    @ParameterTranslationDoc(
                            language=LanguageDoc.EN,
                            description = "Content to be encrypted."
                    )
            }),
            @ParameterDoc(name = "secureRandom", translations = {
                    @ParameterTranslationDoc(
                            language=LanguageDoc.PT,
                            description = "Valor aleatório gerado com initSecure."
                    ),
                    @ParameterTranslationDoc(
                            language=LanguageDoc.EN,
                            description = "Random value generated with initSecure."
                    )
            })
    }, returns = {
            @ReturnTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Dados do conteúdo encriptado em bcryptSalt."
            ),
            @ReturnTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Data of the content encrypted in bcryptSalt."
            )
    })
    public String bcryptSalt(int rounds, SecureRandom secureRandom) {
        return BCrypt.gensalt(rounds, secureRandom);
    }

    @MethodDoc(translations = {
            @MethodTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Gera um hash com a string e o salt.",
                    howToUse = {
                            @SourceCodeDoc(
                                    type = SourceCodeTypeDoc.JavaScript,
                                    code = "let hash = _crypto.bcryptHash('netuno',_crypto.bcryptSalt(10))"
                            )
                    }),
            @MethodTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Hash the string and salt.",
                    howToUse = {
                            @SourceCodeDoc(
                                    type = SourceCodeTypeDoc.JavaScript,
                                    code = "let hash = _crypto.bcryptHash('netuno',_crypto.bcryptSalt(10))"
                            )
                    })
    }, parameters = {
            @ParameterDoc(name = "password", translations = {
                    @ParameterTranslationDoc(
                            language = LanguageDoc.PT,
                            name = "senha",
                            description = "Senha ou string que será encriptada."
                    ),
                    @ParameterTranslationDoc(
                            language = LanguageDoc.EN,
                            description = "Password or string that will be encrypted."
                    )
            }),
            @ParameterDoc(name = "salt", translations = {
                    @ParameterTranslationDoc(
                            language = LanguageDoc.PT,
                            name = "salt",
                            description = "Conteúdo encriptado com bcryptSalt."
                    ),
                    @ParameterTranslationDoc(
                            language = LanguageDoc.EN,
                            description = "Encrypted content with bcryptSalt."
                    )
            })
    }, returns = {
            @ReturnTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Resultado da encriptação da String."
            ),
            @ReturnTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Result of String encryption."
            )
    })
    public String bcryptHash(String password, String salt) {
        return BCrypt.hashpw(password, salt);
    }

    @MethodDoc(translations = {
            @MethodTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Verifica se uma String e um Salt Hash são iguais.",
                    howToUse = {
                            @SourceCodeDoc(
                                    type = SourceCodeTypeDoc.JavaScript,
                                    code = "_out.println(_crypto.bcryptCheck('texto', _crypto.bcryptHash('texto',_crypto.bcryptSalt(10))))"
                            )
                    }),
            @MethodTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Checks if a String and a Salt Hash are the same.",
                    howToUse = {
                            @SourceCodeDoc(
                                    type = SourceCodeTypeDoc.JavaScript,
                                    code = "_out.println(_crypto.bcryptCheck('text', _crypto.bcryptHash('text',_crypto.bcryptSalt(10))))"
                            )
                    })
    }, parameters = {
            @ParameterDoc(name = "plaintext", translations = {
                    @ParameterTranslationDoc(
                            language = LanguageDoc.PT,
                            name = "texto",
                            description = "Texto que será verificado."
                    ),
                    @ParameterTranslationDoc(
                            language = LanguageDoc.EN,
                            description = "Text that will be verified."
                    )
            }),
            @ParameterDoc(name = "hashed", translations = {
                    @ParameterTranslationDoc(
                            language = LanguageDoc.PT,
                            name = "hash",
                            description = "Conteúdo encriptado."
                    ),
                    @ParameterTranslationDoc(
                            language = LanguageDoc.EN,
                            description = "Encrypted content."
                    )
            })
    }, returns = {
            @ReturnTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Resultado da verificação, retornando true caso a string e o hash sejam iguais."
            ),
            @ReturnTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Verification result, returning true if string and hash are the same."
            )
    })
    public boolean bcryptCheck(String plaintext, String hashed) {
        return BCrypt.checkpw(plaintext, hashed);
    }

    @MethodDoc(translations = {
            @MethodTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Codifica os dados com o MD5 e retorna a encriptação de 128 bit.",
                    howToUse = {}),
            @MethodTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Encodes data with MD5 and returns 128-bit encryption.",
                    howToUse = {})
    }, parameters = {
            @ParameterDoc(name = "content", translations = {
                    @ParameterTranslationDoc(
                            language=LanguageDoc.PT,
                            name = "conteudo",
                            description = "Conteúdo para ser encriptado."
                    ),
                    @ParameterTranslationDoc(
                            language=LanguageDoc.EN,
                            description = "Content to be encrypted."
                    )
            })
    }, returns = {
            @ReturnTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Dados do conteúdo encriptado em MD5."
            ),
            @ReturnTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Data of the content encrypted in MD5."
            )
    })
    public byte[] md5(String content) throws PsamataException {
        return MD5.crypt(content);
    }
    @MethodDoc(translations = {
            @MethodTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Codifica os dados com o MD5 e converte a encriptação em hexadecimal.",
                    howToUse = {}),
            @MethodTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Encodes data with MD5 and converts the encryption to hexadecimal",
                    howToUse = {})
    }, parameters = {
            @ParameterDoc(name = "content", translations = {
                    @ParameterTranslationDoc(
                            language=LanguageDoc.PT,
                            name = "conteudo",
                            description = "Conteúdo para ser encriptado."
                    ),
                    @ParameterTranslationDoc(
                            language=LanguageDoc.EN,
                            description = "Content to be encrypted."
                    )
            })
    }, returns = {
            @ReturnTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Dados do conteúdo encriptado em MD5 e convertido hexadecimal."
            ),
            @ReturnTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Data of the content encrypted in MD5 and converted in hexadecimal format."
            )
    })

    public String md5Hex(String content) throws PsamataException {
        return MD5.cryptHex(content);
    }
    @MethodDoc(translations = {
            @MethodTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Codifica os dados com o cryptHash.",
                    howToUse = {}),
            @MethodTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Encodes data with cryptHash.",
                    howToUse = {})
    }, parameters = {
            @ParameterDoc(name = "content", translations = {
                    @ParameterTranslationDoc(
                            language=LanguageDoc.PT,
                            name = "conteudo",
                            description = "Conteúdo para ser encriptado."
                    ),
                    @ParameterTranslationDoc(
                            language=LanguageDoc.EN,
                            description = "Content to be encrypted."
                    )
            })
    }, returns = {
            @ReturnTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Dados do conteúdo encriptado em cryptHash."
            ),
            @ReturnTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Data of the content encrypted in cryptHash."
            )
    })
    public String md5Base64(String content) throws PsamataException {
        return MD5.cryptBase64(content);
    }
    @MethodDoc(translations = {
            @MethodTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Codifica os dados com o cryptHash",
                    howToUse = {}),
            @MethodTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Encodes data with cryptHash.",
                    howToUse = {})
    }, parameters = {
            @ParameterDoc(name = "content", translations = {
                    @ParameterTranslationDoc(
                            language=LanguageDoc.PT,
                            name = "conteúdo",
                            description = "Conteúdo para ser encriptado."
                    ),
                    @ParameterTranslationDoc(
                            language=LanguageDoc.EN,
                            description = "Content to be encrypted."
                    )
            }),
            @ParameterDoc(name = "salt", translations = {
                    @ParameterTranslationDoc(
                            language=LanguageDoc.PT,
                            description = "Salt Hash para ser utilizado na encriptação."
                    ),
                    @ParameterTranslationDoc(
                            language=LanguageDoc.EN,
                            description = "Salt Hash to be used in encryption."
                    )
            })
    }, returns = {
            @ReturnTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Dados do conteúdo encriptado cryptHash."
            ),
            @ReturnTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Data of the content encrypted cryptHash."
            )
    })
    public String cryptHash(String key, String salt) {
        return Crypt.crypt(key, salt);
    }

    @MethodDoc(translations = {
            @MethodTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Codifica os dados com o cryptHash",
                    howToUse = {}),
            @MethodTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Encodes data with cryptHash.",
                    howToUse = {})
    }, parameters = {
            @ParameterDoc(name = "content", translations = {
                    @ParameterTranslationDoc(
                            language=LanguageDoc.PT,
                            name = "conteúdo",
                            description = "Conteúdo para ser encriptado."
                    ),
                    @ParameterTranslationDoc(
                            language=LanguageDoc.EN,
                            description = "Content to be encrypted."
                    )
            }),
            @ParameterDoc(name = "salt", translations = {
                    @ParameterTranslationDoc(
                            language=LanguageDoc.PT,
                            description = "Salt Hash para ser utilizado na encriptação."
                    ),
                    @ParameterTranslationDoc(
                            language=LanguageDoc.EN,
                            description = "Salt Hash to be used in encryption."
                    )
            })
    }, returns = {
            @ReturnTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Dados do conteúdo encriptado cryptHash."
            ),
            @ReturnTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Data of the content encrypted cryptHash."
            )
    })
    public String cryptHash(String key) {
        return Crypt.crypt(key);
    }

    public String cryptHash(byte[] key, String salt) {
        return Crypt.crypt(key, salt);
    }

    public String cryptHash(byte[] key) {
        return Crypt.crypt(key);
    }


}
