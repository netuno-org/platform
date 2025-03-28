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

package org.netuno.tritao.com;

import org.netuno.proteu.Proteu;
import org.netuno.psamata.Values;
import org.netuno.tritao.com.Component.Mode;
import org.netuno.tritao.hili.Hili;
import org.netuno.tritao.util.Translation;
import org.netuno.tritao.util.TemplateBuilder;

/**
 * Display Name - Form Field Label Component
 * @author Eduardo Fonseca Velasques - @eduveks
 */
public class Description {
    private Proteu proteu;
    private Hili hili;
    private Values designData;
    private Values tableData;
    private Mode mode;

    public Description(Proteu proteu, Hili hili, Values designData, Values tableData, Mode mode) {
        this.proteu = proteu;
        this.hili = hili;
        this.designData = designData;
        this.tableData = tableData;
        this.mode = mode;
    }
	
    public void render() {
        try {
            String description = Translation.formFieldDescription(proteu, hili, tableData, designData);
            if (!description.isEmpty()) {
                Values data = new Values().set("description", Translation.formFieldDescription(proteu, hili, tableData, designData));
                TemplateBuilder.output(proteu, hili, "com/render/description", data);
            }
        } catch (Exception e) {
            throw new Error(e);
        }
    }
}
