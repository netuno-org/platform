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

package org.netuno.psamata;

/**
 * Regular Expression.
 * @author Eduardo Fonseca Velasques - @eduveks
 */
public final class RegEx {
    /**
     * Regular Expression.
     */
    private RegEx() { }
    /**
     * To Regular Expression.
     * @param text Text
     * @return Text formatted in Relugar Expression
     */
    public static String toRegEx(final String text) {
        String r = "";
        if (text != null && !text.equals("")) {
            char[] chars = text.toCharArray();
            for (int x = 0; x < chars.length; x++) {
                String hex = Integer.toString(chars[x], 16);
                if (hex.length() == 1) {
                    hex = "0" + hex;
                }
                r += "\\x" + hex;
            }
        }
        return r;
    }
}
