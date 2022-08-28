package org.netuno.tritao.script;

public record ScriptSourceCode(
        String extension,
        String path,
        String fileName,
        String constent,
        boolean error) { }
