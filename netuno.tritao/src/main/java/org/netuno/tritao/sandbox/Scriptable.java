package org.netuno.tritao.script;

import org.netuno.psamata.Values;

public interface Sandbox extends AutoCloseable {

    void newContext();

    void closeContext();

    Values run(ScriptSourceCode script, Values bindings) throws Exception;

}
