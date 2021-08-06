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

import java.security.MessageDigest;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.binary.Hex;
import org.netuno.psamata.PsamataException;

/**
 * MD5 Cryptography
 * @author Eduardo Fonseca Velasques - @eduveks
 */
public final class MD5 {
    /**
     * MD5 Encoder.
     */
    private MD5() { }

    /**
     * MD5 Encoder.
     * @param text Text to encode
     * @return Text encoded
     * @throws PsamataException Exception
     */
    public static byte[] crypt(final String text) throws PsamataException {
        try {
            if (text == null || text.length() == 0) {
                return new byte[0];
            } else {
                MessageDigest md = MessageDigest.getInstance("MD5");
                md.update(text.getBytes());
                byte[] hash = md.digest();
                return hash;
            }
        } catch (Exception e) {
            throw new PsamataException("Problem encryption MD5", e);
        }
    }

    public static String cryptHex(final String text) throws PsamataException {
    	if (text == null || text.length() == 0) {
            return "";
        }
        return new String(Hex.encodeHex(crypt(text)));
    }

    public static String cryptBase64(final String text) throws PsamataException {
    	if (text == null || text.length() == 0) {
            return "";
        }
        return new String(Base64.encodeBase64(crypt(text)));
    }
}
