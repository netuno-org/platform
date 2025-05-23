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

package org.netuno.tritao.hili;

import com.vdurmont.emoji.EmojiParser;
import org.netuno.proteu.Proteu;
import org.netuno.proteu.ProteuError;
import org.netuno.tritao.config.Config;

/**
 * Hili Error - Global Features
 * @author Eduardo Fonseca Velasques - @eduveks
 */
public class HiliError  extends ProteuError {

    public HiliError(Proteu proteu, Hili hili, String message) {
        super("\n#"
                + "\n# " + EmojiParser.parseToUnicode(":sparkles:") + " "+ Config.getApp(proteu)
                + "\n#"
                + "\n# "+ message.replace("\n", "\n# ")
                + "\n#"
                + "\n");
    }

    public HiliError(Proteu proteu, Hili hili, String message, Throwable cause) {
        super("\n#"
                + "\n# " + EmojiParser.parseToUnicode(":sparkles:") + " "+ Config.getApp(proteu)
                + "\n#"
                + "\n#"+ message.replace("\n", "\n# ")
                + "\n#"
                + "\n", cause);
    }

    public HiliError(Proteu proteu, Hili hili, Throwable cause) {
        super("\n#"
                + "\n# " + EmojiParser.parseToUnicode(":sparkles:") + " "+ Config.getApp(proteu)
                + "\n#"
                + "\n", cause);
    }
}