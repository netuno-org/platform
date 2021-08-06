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

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import org.netuno.psamata.PsamataException;

/**
 * DES Cryptography
 * @author Eduardo Fonseca Velasques - @eduveks
 */
public class DES {
    /**
     * Algorithm Name.
     */
    private static final String ALGONAME = "DESede";
    /**
     * Secret Key.
     */
    private SecretKey secretKey;
    /**
     * Triple Des Key Data.
     */
    private final byte[] tripleDesKeyData;
    /**
     * Clipher.
     */
    private Cipher cipher;

    /**
     * DES.
     * @param keyWord Key
     * @throws PsamataException Exception
     */
    public DES(final String keyWord) throws PsamataException {
        try {
            tripleDesKeyData = keyWord.getBytes("ASCII");
            cipher = createCipher();
        } catch (Exception e) {
            throw new PsamataException("Problem creating DES", e);
        }

    }
    /**
     * Get Cipher.
     * @return Cipher
     */
    private Cipher getCipher() {
        return cipher;
    }
    /**
     * Create Cipher.
     * @return Cipher
     * @throws PsamataException Exception
     */
    private Cipher createCipher() throws PsamataException {
        try {
            secretKey = new SecretKeySpec(tripleDesKeyData, ALGONAME);
            return Cipher.getInstance(ALGONAME + "/ECB/PKCS5Padding");
        } catch (Exception e) {
            throw new PsamataException("Cannot create Cipher", e);
        }
    }

    /**
     * Encode.
     * @param originalText Text
     * @return Text Encoded
     * @throws PsamataException Exception
     */
    public final synchronized byte[] encode(final String originalText)
    throws PsamataException {
        try {
            getCipher().init(Cipher.ENCRYPT_MODE, secretKey);
            byte[] utf8 = originalText.getBytes("UTF8");
            byte[] enc = getCipher().doFinal(utf8);
            return enc;
        } catch (Exception e) {
            throw new PsamataException("Problem encrypting", e);
        }

    }

    /**
     * Decode.
     * @param encryptedText Text encoded
     * @return Text Decoded
     * @throws PsamataException Exception
     */
    public final synchronized String decode(final byte[] encryptedText)
    throws PsamataException {
        try {
            getCipher().init(Cipher.DECRYPT_MODE, secretKey);
            byte[] utf8 = getCipher().doFinal(encryptedText);
            return new String(utf8, "UTF8");
        } catch (Exception e) {
            throw new PsamataException("Problem decrypting", e);
        }
    }
}
