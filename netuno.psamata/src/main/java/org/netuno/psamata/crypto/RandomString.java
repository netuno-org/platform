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

package org.netuno.psamata.crypto;

import java.security.SecureRandom;
import java.util.Locale;
import java.util.Objects;
import java.util.Random;

import org.netuno.library.doc.LanguageDoc;
import org.netuno.library.doc.LibraryDoc;
import org.netuno.library.doc.LibraryTranslationDoc;

/**
 * Generates random strings.
 * @author Eduardo Fonseca Velasques - @eduveks
 */
@LibraryDoc(translations = {
        @LibraryTranslationDoc(
                language = LanguageDoc.PT,
                title = "RandomString",
                introduction = "O RandomString gera códigos alfanuméricos aleatórios.",
                howToUse = {}
        )
})
public class RandomString {

    public static final String UPPERCASE = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";

    public static final String LOWERCASE = UPPERCASE.toLowerCase(Locale.ROOT);

    public static final String DIGITS = "0123456789";

    public static final String ALPHANUMERIC = UPPERCASE + LOWERCASE + DIGITS;

    public static final String SYMBOLS = "!\"#$%&'()*+,-./:;<=>?@[\\]^_`{|}~";

    private Random random;

    private char[] chars;

    private char[] buf;

    public RandomString(int length, Random random, String chars) {
        if (length < 1) throw new IllegalArgumentException();
        if (chars.length() < 2) throw new IllegalArgumentException();
        this.random = Objects.requireNonNull(random);
        this.chars = chars.toCharArray();
        this.buf = new char[length];
    }

    public RandomString(int length, Random random, boolean withSymbols) {
        this(length, random, withSymbols ? ALPHANUMERIC + SYMBOLS : "");
    }

    /**
     * Create an alphanumeric string generator.
     */
    public RandomString(int length, Random random) {
        this(length, random, ALPHANUMERIC);
    }
    
    /**
     * Create an alphanumeric string generator.
     */
    public RandomString(int length, String chars) {
        this(length, new SecureRandom(), chars);
    }

    public RandomString(int length, boolean withSymbols) {
        this(length, new SecureRandom(), withSymbols);
    }

    /**
     * Create an alphanumeric strings from a secure generator.
     */
    public RandomString(int length) {
        this(length, new SecureRandom());
    }

    /**
     * Create session identifiers.
     */
    public RandomString() {
        this(24);
    }

    public Random random() {
        return random;
    }

    public Random getRandom() {
        return random;
    }
    
    public RandomString setRandom(Random random) {
        this.random = random;
        return this;
    }

    public String getSymbols() {
        return SYMBOLS;
    }

    public char[] chars() {
        return chars;
    }

    public char[] getChars() {
        return chars;
    }

    public RandomString setChars(char[] symbols) {
        this.chars = chars;
        return this;
    }

    public RandomString setChars(String chars) {
        this.chars = chars.toCharArray();
        return this;
    }

    public String charsString() {
        return new String(chars);
    }

    public String getCharsString() {
        return new String(chars);
    }
    
    public String next() {
    	return nextString();
    }

    /**
     * Generate a random string.
     */
    public String nextString() {
        for (int idx = 0; idx < buf.length; ++idx)
            buf[idx] = chars[random.nextInt(chars.length)];
        return new String(buf);
    }

    @Override
    public String toString() {
        return nextString();
    }
}
