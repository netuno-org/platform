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

package org.netuno.psamata.script;

/**
 * Basic Functions to be used with scripts languages that no supports some resources.
 * @author Eduardo Fonseca Velasques - @eduveks
 */
public final class BasicFuncs {
    /**
     * Basic Functions.
     */
    private BasicFuncs() { }
    /**
     * Calculate the Module.
     * @param value Value for extracting module
     * @param div Value of module
     * @return Result
     */
    public static double mod(final double value, final double div) {
        return value % div;
    }
    /**
     * Get Char from a integer.
     * @param asciiCode Ascii Code
     * @return Character
     */
    public static String getChar(final int asciiCode) {
        return "" + (char) asciiCode;
    }
    /**
     * Replace.
     * @param text String for replacing
     * @param textold Deleting this String
     * @param textnew Inserting this String
     * @return String replaced
     */
    public static String replace(final String text, final String textold, final String textnew) {
        return text.replace((CharSequence)textold, (CharSequence)textnew);
    }
}
