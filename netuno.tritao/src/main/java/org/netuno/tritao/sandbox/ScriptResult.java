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

package org.netuno.tritao.sandbox;

/**
 * Script Result
 * @author Eduardo Fonseca Velasques - @eduveks
 */
public class ScriptResult {
    private boolean error = false;

    private ScriptResult() { }

    private ScriptResult(boolean withError) {
        this.error = withError;
    }

    public boolean isError() {
        return error;
    }

    public boolean isSuccess() {
        return error == false;
    }

    public ScriptResult ifError(Runnable func) {
        if (isError()) {
            func.run();
        }
        return this;
    }

    public ScriptResult ifSuccess(Runnable func) {
        if (isSuccess()) {
            func.run();
        }
        return this;
    }

    protected static ScriptResult withError() {
        return new ScriptResult(true);
    }

    protected static ScriptResult withSuccess() {
        return new ScriptResult();
    }
}
