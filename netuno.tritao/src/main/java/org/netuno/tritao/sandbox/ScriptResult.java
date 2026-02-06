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

import org.netuno.library.doc.LanguageDoc;
import org.netuno.library.doc.LibraryDoc;
import org.netuno.library.doc.LibraryTranslationDoc;

import org.graalvm.polyglot.Value;

import java.util.function.Consumer;

@LibraryDoc(translations = {
        @LibraryTranslationDoc(
                language = LanguageDoc.PT,
                title = "ScriptResult",
                introduction = "Resultado da exceção de scripts, se ocorreu com sucesso ou se gerou algum erro.",
                howToUse = { }
        ),
        @LibraryTranslationDoc(
                language = LanguageDoc.EN,
                title = "ScriptResult",
                introduction = "Script execution results if an error occurred or was successful.",
                howToUse = { }
        )
})
/**
 * Script Result
 * @author Eduardo Fonseca Velasques - @eduveks
 */
public class ScriptResult {
    private Object result = null;
    private Throwable error = null;

    private ScriptResult(Object result) {
        this.result = result;
    }

    private ScriptResult(Throwable error) {
        this.error = error;
    }

    public boolean isError() {
        return error != null;
    }

    public boolean isSuccess() {
        return isError() == false;
    }

    public Object get() {
        return result;
    }

    public ScriptResult onError(Consumer<Throwable> func) {
        if (isError()) {
            func.accept(error);
        }
        return this;
    }
    
    public ScriptResult onError(Value function) {
        if (isError()) {
            function.execute(error);
        }
        return this;
    }

    public ScriptResult onSuccess(Consumer<Object> func) {
        if (isSuccess()) {
            func.accept(result);
        }
        return this;
    }
    
    public ScriptResult onSuccess(Value function) {
        if (isSuccess()) {
            function.execute(result);
        }
        return this;
    }

    protected static ScriptResult withError(Throwable error) {
        return new ScriptResult(error);
    }

    protected static ScriptResult withSuccess(Object result) {
        return new ScriptResult(result);
    }
}
