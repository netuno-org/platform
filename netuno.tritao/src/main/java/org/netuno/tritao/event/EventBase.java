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

package org.netuno.tritao.event;

import org.netuno.proteu.Proteu;
import org.netuno.psamata.Values;
import org.netuno.tritao.config.Config;
import org.netuno.tritao.hili.Hili;

/**
 * Event Base
 * @author Eduardo Fonseca Velasques - @eduveks
 */
public abstract class EventBase implements Comparable<EventBase> {
    private final Proteu proteu;
    private final Hili hili;
    private final String id;
    private final int order;

    protected EventBase(Proteu proteu, Hili hili, String id) {
        this.proteu = proteu;
        this.hili = hili;
        this.id = id;
        this.order = 100;
    }

    protected EventBase(Proteu proteu, Hili hili, String id, int order) {
        this.proteu = proteu;
        this.hili = hili;
        this.id = id;
        this.order = order;
    }

    @Override
    public int compareTo(EventBase o) {
        return Integer.compare(getOrder(), o.getOrder());
    }

    protected Proteu getProteu() {
        return proteu;
    }

    protected Hili getHili() {
        return hili;
    }

    protected String getId() {
        return id;
    }

    protected int getOrder() {
        return order;
    }

    protected String getAppName() {
        return Config.getApp(getProteu());
    }

    protected<T> T resource(Class<T> resourceClass) {
        return getHili().resource().get(resourceClass);
    }

    abstract public void run(Values data);
}
