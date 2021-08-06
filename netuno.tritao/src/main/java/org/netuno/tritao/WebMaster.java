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

package org.netuno.tritao;

import org.netuno.proteu.Proteu;
import org.netuno.proteu.Web;
import org.netuno.tritao.config.Hili;

/**
 * Web Master
 * @author Eduardo Fonseca Velasques - @eduveks
 */
public class WebMaster implements Web {
    private Proteu proteu;
    private Hili hili;

    public WebMaster() {

    }

    public WebMaster(Proteu proteu, Hili hili) {
        setProteu(proteu);
        setFaros(hili);
    }

    public Proteu getProteu() {
        return proteu;
    }

    public Hili getHili() {
        return hili;
    }

    public<T> T resource(Class<T> resourceClass) {
        return getHili().resource(resourceClass);
    }

    public<T> T resource(String name) {
        return getHili().resource(name);
    }

    @Override
    public void setProteu(Proteu proteu) {
        this.proteu = proteu;
    }

    @Override
    public void setFaros(Object faros) {
        this.hili = (Hili)faros;
    }

    @Override
    public void run() throws Exception {
        throw new WebMasterException("You should not execute directly this class.");
    }
}
