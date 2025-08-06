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

import org.netuno.library.doc.LanguageDoc;
import org.netuno.library.doc.LibraryDoc;
import org.netuno.library.doc.LibraryTranslationDoc;
import org.netuno.proteu.Proteu;
import org.netuno.psamata.Values;
import org.netuno.tritao.hili.Hili;
import org.netuno.tritao.resource.util.ResourceException;
import org.netuno.tritao.util.TemplateBuilder;

import java.util.Map;

/**
 * Template - Resource
 * @author Eduardo Fonseca Velasques - @eduveks
 */
@Resource(name = "template")
@LibraryDoc(translations = {
        @LibraryTranslationDoc(
                language = LanguageDoc.PT,
                title = "Template",
                introduction = "Recurso de gestão de templates.\n\n" +
                        "Este recurso permite a manipulação de templates com base no motor de templates do [Apache Velocity](https://velocity.apache.org/engine/2.4.1/user-guide.html).",
                howToUse = { }
        ),
        @LibraryTranslationDoc(
                language = LanguageDoc.EN,
                title = "Template",
                introduction = "Template management resource.\n\n" +
                        "This resource allows manipulation of templates based on the [Apache Velocity](https://velocity.apache.org/engine/2.4.1/user-guide.html) templating engine.",
                howToUse = { }
        )
})
public class Template extends ResourceBase {
    
    private boolean core = false;

    public Template(Proteu proteu, Hili hili) {
        super(proteu, hili);
    }
    
    public Template init() {
        return new Template(getProteu(), getHili());
    }
    
    public Template initCore() {
        Template template = new Template(getProteu(), getHili());
        template.core = true;
        return template;
    }
    
    public String get(String name) throws ResourceException {
        return getOutput(name);
    }

    public String get(String name, Map data) throws ResourceException {
        return getOutput(name, new Values(data));
    }

    public String get(String name, Values data) throws ResourceException {
        return getOutput(name, data);
    }

    public String getOutput(String name) throws ResourceException {
        try {
            if (core) {
                return TemplateBuilder.getOutput(getProteu(), getHili(), name);
            }
            return TemplateBuilder.getOutputApp(getProteu(), getHili(), name);
        } catch (Exception e) {
            throw new ResourceException("Unable to output the template: "+ name, e);
        }
    }

    public String getOutput(String name, Map data) throws ResourceException {
        return getOutput(name, new Values(data));
    }

    public String getOutput(String name, Values data) throws ResourceException {
        try {
            if (core) {
                return TemplateBuilder.getOutput(getProteu(), getHili(), name, data);
            }
            return TemplateBuilder.getOutputApp(getProteu(), getHili(), name, data);
        } catch (Exception e) {
            throw new ResourceException("Unable to output the template: "+ name
                    +"\nWith data: "+ data.toJSON(), e);
        }
    }

    public void out(String name) throws ResourceException {
        output(name);
    }

    public void out(String name, Map data) throws ResourceException {
        output(name, new Values(data));
    }

    public void out(String name, Values data) throws ResourceException {
        output(name, data);
    }

    public void output(String name) throws ResourceException {
        try {
            if (core) {
                TemplateBuilder.output(getProteu(), getHili(), name);
            }
            TemplateBuilder.outputApp(getProteu(), getHili(), name);
        } catch (Exception e) {
            throw new ResourceException("Unable to output the template: "+ name, e);
        }
    }

    public void output(String name, Map data) throws ResourceException {
        output(name, new Values(data));
    }

    public void output(String name, Values data) throws ResourceException {
        try {
            if (core) {
                TemplateBuilder.output(getProteu(), getHili(), name, data);
            }
            TemplateBuilder.outputApp(getProteu(), getHili(), name, data);
        } catch (Exception e) {
            throw new ResourceException("Unable to output the template: "+ name
                    +"\nWith data: "+ data.toJSON(), e);
        }
    }
}
