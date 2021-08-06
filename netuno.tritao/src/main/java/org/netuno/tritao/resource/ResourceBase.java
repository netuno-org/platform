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

import org.netuno.proteu.Proteu;
import org.netuno.tritao.config.Hili;

/**
 * Resource Base
 * @author Eduardo Fonseca Velasques - @eduveks
 */
public class ResourceBase {

    private Proteu proteu = null;
    private Hili hili = null;

    protected ResourceBase(Proteu proteu, Hili hili) {
        this.proteu = proteu;
        this.hili = hili;
    }

    protected Proteu getProteu() {
        return proteu;
    }

    protected Hili getHili() {
        return hili;
    }

    protected<T> T resource(Class<T> resourceClass) {
        return getHili().resource(resourceClass);
    }

    protected String enumValueOf(String key) {
        return key.toUpperCase().replace("-", "_");
    }

}
