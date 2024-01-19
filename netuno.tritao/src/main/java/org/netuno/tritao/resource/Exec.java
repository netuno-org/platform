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

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import org.graalvm.polyglot.Value;
import org.netuno.library.doc.*;
import org.netuno.proteu.Proteu;
import org.netuno.psamata.Values;
import org.netuno.psamata.io.SafePath;
import org.netuno.psamata.script.GraalRunner;
import org.netuno.psamata.script.ScriptRunner;
import org.netuno.tritao.config.Config;
import org.netuno.tritao.hili.Hili;
import org.netuno.tritao.resource.util.ResourceException;
import org.netuno.tritao.sandbox.ScriptResult;

/**
 * Execution - Resource
 * @author Eduardo Fonseca Velasques - @eduveks
 */
@Resource(name = "exec")
@LibraryDoc(translations = {
        @LibraryTranslationDoc(
                language = LanguageDoc.PT,
                title = "Exec",
                introduction = "Funcionalidades para auxiliar à execução do código.\n"
                        + "Realiza a execução de outros scripts, suporta também executar scripts em outras linguagens de programação.\n"
                        + "Executa o script indicado, retornando o seu output.\n"
                        + "Principais funções:\n"
                        + "- [bind](#bind)\n"
                        + "- [core](#core)\n"
                        + "- [service](#service)\n"
                        + "- [sleep](#sleep)\n"
                        + "- [stop](#stop)\n"
                        + "- [gc](#gc)\n",
                howToUse = {
                        @SourceCodeDoc(
                                type = SourceCodeTypeDoc.JavaScript,
                                code = "// Executa outro script da App em `server/core/`\n" +
                                        "const outputOutput = _exec.core(\"outro-script\");\n" +
                                        "\n"+
                                        "// Executa outro script da App em `server/services/`\n" +
                                        "const outputServico = _exec.service(\"outro-servico\");\n" +
                                        "\n"+
                                        "// Pausa por 2 segundos:`\n" +
                                        "_exec.sleep(2000);\n"+
                                        "\n"+
                                        "// Para a execução:`\n" +
                                        "_exec.stop();\n"
                        )
                }
        ),
        @LibraryTranslationDoc(
                language = LanguageDoc.EN,
                title = "Exec",
                introduction = "Functionalities to aid code execution.\n"
                        + "It performs the execution of other scripts, it also supports the execution of scripts in other programming languages.\n"
                        + "Executes the indicated script, returning its output.\n"
                        + "Main functions:\n"
                        + "- [bind](#bind)\n"
                        + "- [core](#core)\n"
                        + "- [service](#service)\n"
                        + "- [sleep](#sleep)\n"
                        + "- [stop](#stop)\n"
                        + "- [gc](#gc)\n",
                howToUse = {
                        @SourceCodeDoc(
                                type = SourceCodeTypeDoc.JavaScript,
                                code = "// Run another App script in `server/core/`\n" +
                                        "const outputOutput = _exec.core(\"other-script\");\n" +
                                        "\n"+
                                        "// Run another App script in `server/services/`\n" +
                                        "const outputService = _exec.service(\"other-service\");\n" +
                                        "\n"+
                                        "// Pause for 2 seconds:`\n" +
                                        "_exec.sleep(2000);\n"+
                                        "\n"+
                                        "// Execution stop:`\n" +
                                        "_exec.stop();\n"
                        )
                }
        )
})
public class Exec extends ResourceBaseValues {

    public Exec(Proteu proteu, Hili hili) {
        super(proteu, hili, new Values());
    }

    @MethodDoc(translations = {
            @MethodTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Realiza a transição de variáveis entre scripts, inclusive entre linguagens de programação diferentes.",
                    howToUse = {
                            @SourceCodeDoc(
                                    type = SourceCodeTypeDoc.JavaScript,
                                    code = "const minhaVarOriginal = \"teste\";\n" +
                                            "_exec" +
                                            "    .bind(\"transitarVar\", minhaVarOriginal)" +
                                            "    .core(\"outro-script-talvez-em-outra-linguagem\");"
                            ) }),
            @MethodTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Transitions variables between scripts, including between different programming languages.",
                    howToUse = {
                            @SourceCodeDoc(
                                    type = SourceCodeTypeDoc.JavaScript,
                                    code = "const originalVar = \"test\";\n" +
                                            "_exec" +
                                            "    .bind(\"transitVar\", originalVar)" +
                                            "    .core(\"another-script-maybe-in-another-language\");"
                            ) })
    }, parameters = {
            @ParameterDoc(name = "key", translations = {
                    @ParameterTranslationDoc(
                            language=LanguageDoc.PT,
                            name = "variavel",
                            description = "Nome da variável que estará disponível no outro script que será executado."
                    ),
                    @ParameterTranslationDoc(
                            language=LanguageDoc.EN,
                            description = "Variable name that will be available in the other script that will be executed."
                    )
            }),
            @ParameterDoc(name = "value", translations = {
                    @ParameterTranslationDoc(
                            language=LanguageDoc.PT,
                            name = "objeto",
                            description = "Objeto a ser passado para o outro script que será executado."
                    ),
                    @ParameterTranslationDoc(
                            language=LanguageDoc.EN,
                            description = "Object to be passed to the other script that will be executed."
                    )
            }) 
    }, returns = {})
    public Exec bind(String key,
            Object object) {
        getHili().sandbox().bind(key, object);
        return this;
    }

    @MethodDoc(translations = {
            @MethodTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Execução de scripts que estão na pasta `server/core/`.",
                    howToUse = {
                            @SourceCodeDoc(
                                    type = SourceCodeTypeDoc.JavaScript,
                                    code = "_exec.core(\"outro-script-talvez-em-outro-linguagem\");"
                            ) }),
            @MethodTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Execution of scripts that are in the `server/core/` folder.",
                    howToUse = {
                            @SourceCodeDoc(
                                    type = SourceCodeTypeDoc.JavaScript,
                                    code = "_exec.core(\"another-script-maybe-in-another-language\");"
                            ) })
    }, parameters = {
            @ParameterDoc(name = "path", translations = {
                    @ParameterTranslationDoc(
                            language=LanguageDoc.PT,
                            name = "caminho",
                            description = "Caminho do script com origem em `core/` a executar."
                    ),
                    @ParameterTranslationDoc(
                            language=LanguageDoc.EN,
                            description = "Script path with source in `core/` to execute."
                    )
            }),
            @ParameterDoc(name = "path", translations = {
                    @ParameterTranslationDoc(
                            language=LanguageDoc.PT,
                            name = "caminho",
                            description = "Caminho do script com origem em `core/` a executar."
                    ),
                    @ParameterTranslationDoc(
                            language=LanguageDoc.EN,
                            description = "Script path with source in `core/` to execute."
                    )
            }),
            @ParameterDoc(name = "preserveContext", translations = {
                    @ParameterTranslationDoc(
                            language=LanguageDoc.PT,
                            name = "preservarContexto",
                            description = "Se deve manter o mesmo contexto de execução ou iniciar um novo."
                    ),
                    @ParameterTranslationDoc(
                            language=LanguageDoc.EN,
                            description = "Whether to keep the same execution context or start a new one."
                    )
            })
    }, returns = {})
    public ScriptResult core(String path, boolean preserveContext) throws ResourceException {
        path = SafePath.fileSystemPath(path);
        String scriptPath = ScriptRunner.searchScriptFile(Config.getPathAppCore(getProteu()) + "/" +  path);
        if (scriptPath != null) {
            return getHili().sandbox().runScript(Config.getPathAppCore(getProteu()), path, preserveContext);
        }
        throw new ResourceException("Core script not found: "+ path);
    }
    public ScriptResult core(String path) throws ResourceException {
        return core(path, true);
    }

    @MethodDoc(translations = {
            @MethodTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Execução de scripts que estão na pasta `server/services/`.",
                    howToUse = {
                            @SourceCodeDoc(
                                    type = SourceCodeTypeDoc.JavaScript,
                                    code = "_exec.service(\"outro-script-talvez-em-outro-linguagem\");"
                            ) }),
            @MethodTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Execution of scripts that are in the `server/services/` folder.",
                    howToUse = {
                            @SourceCodeDoc(
                                    type = SourceCodeTypeDoc.JavaScript,
                                    code = "_exec.service(\"another-script-maybe-in-another-language\");"
                            ) })
    }, parameters = {
            @ParameterDoc(name = "path",
                    translations = {
                            @ParameterTranslationDoc(
                                    language=LanguageDoc.PT,
                                    name = "caminho",
                                    description = "Caminho do script com origem em 'services/' a executar."
                            ),
                            @ParameterTranslationDoc(
                                    language=LanguageDoc.EN,
                                    description = "Script path originating from 'services/' to be executed."
                            )
                    }),
            @ParameterDoc(name = "preserveContext", translations = {
                    @ParameterTranslationDoc(
                            language=LanguageDoc.PT,
                            name = "preservarContexto",
                            description = "Se deve manter o mesmo contexto de execução ou iniciar um novo."
                    ),
                    @ParameterTranslationDoc(
                            language=LanguageDoc.EN,
                            description = "Whether to keep the same execution context or start a new one."
                    )
            })
    }, returns = {})
    public ScriptResult service(String path, boolean preserveContext) throws ResourceException {
        path = SafePath.fileSystemPath(path);
        String scriptPath = ScriptRunner.searchScriptFile(Config.getPathAppServices(getProteu()) + "/" +  path);
        if (scriptPath != null) {
            return getHili().sandbox().runScript(Config.getPathAppServices(getProteu()), path, preserveContext);
        }
        throw new ResourceException("Service script not found: "+ path);
    }
    public ScriptResult service(String path) throws ResourceException {
        return service(path, true);
    }
    
    @MethodDoc(translations = {
            @MethodTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Realiza uma pausa na execução, útil para provocar algum atraso de processamento controlado.",
                    howToUse = {
                            @SourceCodeDoc(
                                    type = SourceCodeTypeDoc.JavaScript,
                                    code = "// Para a execução por 3 segundos:\n"
                                            + "_exec.sleep(3000);"
                            ) }),
            @MethodTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Pauses execution, useful for causing some controlled processing delay.",
                    howToUse = {
                            @SourceCodeDoc(
                                    type = SourceCodeTypeDoc.JavaScript,
                                    code = "// Stop the execution for 3 seconds:\n"
                                            + "_exec.sleep(3000);"
                            ) })
    }, parameters = {
            @ParameterDoc(name = "interval",
                    translations = {
                            @ParameterTranslationDoc(
                                    language=LanguageDoc.PT,
                                    name = "intervalo",
                                    description = "Intervalo de tempo em milissegundos que deve fazer a pausa na execução."
                            ),
                            @ParameterTranslationDoc(
                                    language=LanguageDoc.EN,
                                    description = "Time interval in milliseconds to pause execution."
                            )
                    }),
    }, returns = {})
    public void sleep(long interval) throws InterruptedException {
        Thread.sleep(interval);
    }

    @MethodDoc(translations = {
            @MethodTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Realiza a paragem da execução do script atual, útil para interromper da execução.",
                    howToUse = {
                            @SourceCodeDoc(
                                    type = SourceCodeTypeDoc.JavaScript,
                                    code = "// Para a execução:\n"
                                            + "_out.println('Vai parar...<br>');\n"
                                            + "_exec.stop();\n"
                                            + "_out.println('Não chega nesta linha.');"
                            ) }),
            @MethodTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "It stops the execution of the current script, useful for interrupting the execution.",
                    howToUse = {
                            @SourceCodeDoc(
                                    type = SourceCodeTypeDoc.JavaScript,
                                    code = "// For the execution:\n"
                                            + "_out.println('Will stop...<br>');\n"
                                            + "_exec.stop();\n"
                                            + "_out.println('Not run this line.');"
                            ) })
    }, parameters = { }, returns = {})
    public void stop() {
        getHili().sandbox().stopScript();
    }

    @MethodDoc(translations = {
            @MethodTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Executa a limpeza da memória através da execução do coletor de lixo ([JVM garbage collector](https://www.baeldung.com/jvm-garbage-collectors)).",
                    howToUse = {
                            @SourceCodeDoc(
                                    type = SourceCodeTypeDoc.JavaScript,
                                    code = "// Libertar memória executando o Garbage Collector:\n"
                                            + "_exec.gc();"
                            ) }),
            @MethodTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Performs memory cleanup by running the garbage collector ([JVM garbage collector](https://www.baeldung.com/jvm-garbage-collectors)).",
                    howToUse = {
                            @SourceCodeDoc(
                                    type = SourceCodeTypeDoc.JavaScript,
                                    code = "// Free up memory by running Garbage Collector:\n"
                                            + "_exec.gc();"
                            ) })
    }, parameters = { }, returns = {})
    public void gc() {
        System.gc();
        System.runFinalization();
    }
    
    @MethodDoc(translations = {
            @MethodTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Execução de funções de forma assíncrona.",
                    howToUse = { }),
            @MethodTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Execution of functions asynchronously.",
                    howToUse = { })
    }, parameters = {}, returns = {})
    public Async async(Value... functions) throws ResourceException {
        return new Async(functions);
    }
    
    public Async asyncData(Object data, Value... functions) throws ResourceException {
        return new Async(data, functions);
    }
    
    public Async asyncList(Values list, Value function) throws ResourceException {
        return new Async(function, list);
    }
    
    @LibraryDoc(translations = {
        @LibraryTranslationDoc(
                language = LanguageDoc.PT,
                title = "Async",
                introduction = "Orquestra os modos de execução assíncrona.",
                howToUse = {}
        ),
        @LibraryTranslationDoc(
                language = LanguageDoc.EN,
                title = "Async",
                introduction = "Orchestrates asynchronous execution modes.",
                howToUse = {}
        )
    })
    public class Async {
        private ExecutorService executor = null;
        private Value[] functions = null;
        private Object data = null;
        private Values list = null;
        
        public Async(Value... functions) {
            this.functions = functions;
            executor = Executors.newFixedThreadPool(functions.length);
        }
        
        public Async(Object data, Value... functions) {
            this.functions = functions;
            this.data = data;
            executor = Executors.newFixedThreadPool(functions.length);
        }
        
        public Async(Value function, Values list) {
            this.functions = new Value[] { function };
            this.list = list;
            if (!this.list.isList()) {
                throw new ResourceException("The asyncList only accept object of list type.");
            }
            executor = Executors.newFixedThreadPool(list.size());
        }
        
        private List<Callable<Object>> listOfCallables() {
            List<Callable<Object>> callables = new ArrayList<>();
            if (list != null && functions.length == 1) {
                list.forEach((i) -> {
                    callables.add(() -> {
                        return GraalRunner.toObject(functions[0].execute(i));
                    });
                });
            } else {
                for (int i = 0; i < functions.length; i++) {
                    final int index = i;
                    callables.add(() -> {
                        if (data != null) {
                            return GraalRunner.toObject(functions[index].execute(data));
                        } else {
                            return GraalRunner.toObject(functions[index].execute());
                        }
                    });
                }
            }
            return callables;
        }
        
        public List<Object> invokeAll() {
            List<Object> results = new ArrayList<>();
            try {
                List<Future<Object>> futures = executor.invokeAll(listOfCallables());
                executor.shutdown();
                for (Future<Object> future : futures) {
                    results.add(future.get());
                }
            } catch (InterruptedException | ExecutionException e) {
                throw new ResourceException("Fail to invoke the execution of all async functions.", e);
            }
            return results;
        }
        
        public List<Object> invokeAll(long timeout) {
            List<Object> results = new ArrayList<>();
            try {
                List<Future<Object>> futures = executor.invokeAll(listOfCallables(), timeout, TimeUnit.SECONDS);
                executor.shutdown();
                for (Future<Object> future : futures) {
                    results.add(future.get());
                }
            } catch (InterruptedException | ExecutionException e) {
                throw new ResourceException("Fail to invoke the execution of all async functions with timeout of "+ timeout +" seconds.", e);
            }
            return results;
        }
        
        public Object invokeAny() {
            Object result = null;
            try {
                result = executor.invokeAny(listOfCallables());
                executor.shutdown();
            } catch (InterruptedException | ExecutionException e) {
                throw new ResourceException("Fail to invoke the execution of any async functions.", e);
            }
            return result;
        }
        
        public Object invokeAny(long timeout) {
            Object result = null;
            try {
                result = executor.invokeAny(listOfCallables(), timeout, TimeUnit.SECONDS);
                executor.shutdown();
            } catch (InterruptedException | ExecutionException | TimeoutException e) {
                throw new ResourceException("Fail to invoke the execution of any async functions with timeout of "+ timeout +" seconds.", e);
            }
            return result;
        }
        
        public Async start() {
            if (list != null && functions.length == 1) {
                list.forEach((i) -> {
                    executor.execute(new Thread() {
                        public void run() {
                            functions[0].execute(i);
                        }
                    });
                });
            } else {
                for (int i = 0; i < functions.length; i++) {
                    final int index = i;
                    executor.execute(new Thread() {
                        public void run() {
                            if (data != null) {
                                functions[index].execute(data);
                            } else {
                                functions[index].execute();
                            }
                        }
                    });
                }
            }
            executor.shutdown();
            return this;
        }
        
        public boolean await() {
            return executor.isTerminated();
        }
        
        public boolean await(long timeout) throws InterruptedException {
            return executor.awaitTermination(timeout, TimeUnit.SECONDS);
        }
        
        public Async stop() {
            executor.shutdownNow();
            return this;
        }
    }

}
