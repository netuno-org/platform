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
 * SHA256 Cryptography
 * @author Eduardo Fonseca Velasques - @eduveks
 */
public class SHA256 {
	public static byte[] crypt(String text) throws PsamataException {
		try {
			MessageDigest md = MessageDigest.getInstance("SHA-256");
			
			md.update(text.getBytes("UTF-8")); // Change this to "UTF-16" if needed
			
			return md.digest();
		} catch (Exception e) {
			throw new PsamataException(e);
		}
	}
	
	public static String cryptHex(String text) throws PsamataException {
		return new String(Hex.encodeHex(crypt(text)));
	}
	
	public static String cryptBase64(String text) throws PsamataException {
		return new String(Base64.encodeBase64(crypt(text)));
	}
}
