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

import org.netuno.library.doc.*;
import org.netuno.proteu.Proteu;
import org.netuno.psamata.crypto.RandomString;
import org.netuno.tritao.hili.Hili;

/**
 * Random - Resource
 * @author Eduardo Fonseca Velasques - @eduveks
 */
@Resource(name = "random")
@LibraryDoc(translations = {
        @LibraryTranslationDoc(
                language = LanguageDoc.PT,
                title = "Random",
                introduction = "Geração de valores aleatórios.",
                howToUse = { }
        )
})
public class Random extends ResourceBase {
    
    public final String UPPERCASE = RandomString.UPPERCASE;

    public final String LOWERCASE = RandomString.LOWERCASE;

    public final String DIGITS = RandomString.DIGITS;

    public final String ALPHANUMERIC = RandomString.ALPHANUMERIC;

    public Random(Proteu proteu, Hili hili) {
        super(proteu, hili);
    }


    @MethodDoc(translations = {
            @MethodTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Gera toda a classe Random.",
                    howToUse = {}),
            @MethodTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Generates the all Random class.",
                    howToUse = {})
    }, parameters = {

    }, returns = {
            @ReturnTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Retorna uma classe."
            ),
            @ReturnTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Returns a class."
            )
    })


    public java.util.Random init() {
        return new java.util.Random();
    }
    @MethodDoc(translations = {
            @MethodTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Gera aleatoriamente uma seed.",
                    howToUse = {}),
            @MethodTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Generates a random seed.",
                    howToUse = {})
    }, parameters = {

    }, returns = {
            @ReturnTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Retorna uma seed."
            ),
            @ReturnTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Returns a seed."
            )
    })

    public java.util.Random init(long seed) {
        return new java.util.Random(seed);
    }

    public java.security.SecureRandom initSecure(byte[] seed) {
        return new java.security.SecureRandom(seed);
    }

    public java.security.SecureRandom initSecure() {
        return new java.security.SecureRandom();
    }
    @MethodDoc(translations = {
            @MethodTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Gera aleatoriamente um texto de 24 caracteres.",
                    howToUse = {}),
            @MethodTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Generates a random text of 24 caracteres.",
                    howToUse = {})
    }, parameters = {

    }, returns = {
            @ReturnTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Retorna um texto de 24 caracteres aleatórios."
            ),
            @ReturnTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Returns a 24 characters random string."
            )
    })

    public RandomString initString() {
        return new RandomString();
    }

    public RandomString initString(int length) {
        return new RandomString(length);
    }

    public RandomString initString(int length, java.util.Random random) {
        return new RandomString(length, random);
    }

    public RandomString initString(int length, java.util.Random random, String symbols) {
        return new RandomString(length, random, symbols);
    }
    
    public RandomString initString(int length, String symbols) {
        return new RandomString(length, symbols);
    }
}
