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

import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

/**
 * HMAC Cryptography
 * @author Eduardo Fonseca Velasques - @eduveks
 */
public class HMAC {
    static public byte[] encrypt256(byte[] secretKey, byte[] message) throws NoSuchAlgorithmException, InvalidKeyException {
        Mac mac = Mac.getInstance("HmacSHA256");
        SecretKeySpec secretKeySpec = new SecretKeySpec(secretKey, "HmacSHA256");
        mac.init(secretKeySpec);
        return mac.doFinal(message);
    }
    
    static public byte[] encrypt256(String secretKey, String message) throws UnsupportedEncodingException, NoSuchAlgorithmException, InvalidKeyException {
        return encrypt256(secretKey.getBytes("UTF-8"), message.getBytes("UTF-8"));
    }
    
    static public String encrypt256Hex(byte[] secretKey, byte[] message) throws NoSuchAlgorithmException, InvalidKeyException {
        return String.format("%032x", new BigInteger(1, encrypt256(secretKey, message)));
    }
    
    static public String encrypt256Hex(String secretKey, String message) throws UnsupportedEncodingException, NoSuchAlgorithmException, InvalidKeyException {
        return encrypt256Hex(secretKey.getBytes("UTF-8"), message.getBytes("UTF-8"));
    }
    
    static public String encrypt256Base64(byte[] secretKey, byte[] message) throws NoSuchAlgorithmException, InvalidKeyException {
        return Base64.getEncoder().encodeToString(encrypt256(secretKey, message));
    }
    
    static public String encrypt256Base64(String secretKey, String message) throws UnsupportedEncodingException, NoSuchAlgorithmException, InvalidKeyException {
        return encrypt256Base64(secretKey.getBytes("UTF-8"), message.getBytes("UTF-8"));
    }
    
}
