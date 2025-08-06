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

import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.xssf.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.ss.util.CellReference;
import org.apache.poi.ss.util.WorkbookUtil;
import org.netuno.library.doc.*;
import org.netuno.proteu.Proteu;
import org.netuno.psamata.Values;
import org.netuno.psamata.io.InputStream;
import org.netuno.psamata.io.OutputStream;
import org.netuno.psamata.io.File;
import org.netuno.tritao.hili.Hili;
import org.netuno.tritao.resource.util.FileSystemPath;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.*;

/**
 * XLS/Excel - Resource
 * @author Eduardo Fonseca Velasques - @eduveks
 */
@Resource(name = "xls")
@LibraryDoc(translations = {
        @LibraryTranslationDoc(
                language= LanguageDoc.PT,
                title = "XLS",
                introduction = "Criar e ler ficheiros _Excel_, suporta para ficheiros _XLS_ como ficheiros _XLSX_.\n\n"+
                        "Este recurso utiliza a biblioteca [Apache POI](https://poi.apache.org/ \"Apache POI\").",
                howToUse = {
                        @SourceCodeDoc(
                                type = SourceCodeTypeDoc.JavaScript,
                                code = "const excel = _xls.create();\n"+
                                        "const titulos = _val.init()\n"+
                                        "    .add(\n"+
                                        "        _val.init()\n"+
                                        "            .set('value', 'Nome')\n"+
                                        "    ).add(\n"+
                                        "        _val.init()\n"+
                                        "            .set('value', 'Idade')\n"+
                                        "    );\n"+
                                        "const endPosition = excel.addDataTable(1, 1, titulos)\n" +
                                        "const dados = _val.init()\n"+
                                        "    .add(\n"+
                                        "        _val.init()\n"+
                                        "            .add(\n"+
                                        "                 _val.init()\n"+
                                        "                     .set('value', 'Maria')\n"+
                                        "            ).add(\n"+
                                        "                 _val.init()\n"+
                                        "                     .set('value', 24)\n"+
                                        "            )\n"+
                                        "    ).add(\n"+
                                        "        _val.init()\n"+
                                        "            .add(\n"+
                                        "                 _val.init()\n"+
                                        "                     .set('value', 'Ricardo')\n"+
                                        "            ).add(\n"+
                                        "                 _val.init()\n"+
                                        "                     .set('value', 22)\n"+
                                        "            )\n"+
                                        "    );\n"+
                                        "endPosition = excel.addDataTable(endPosition.row, 1, data)\n"+
                                        "excel.output(\"idades.xls\");"
                        )
                }
        ),
        @LibraryTranslationDoc(
                language= LanguageDoc.EN,
                title = "XLS",
                introduction = "Create and read _Excel_ files, support for _XLS_ files as _XLSX_ files.\n\n"+
                        "This feature uses the [Apache POI](https://poi.apache.org/ \"Apache POI\") library.",
                howToUse = {
                        @SourceCodeDoc(
                                type = SourceCodeTypeDoc.JavaScript,
                                code = "const excel = _xls.create();\n"+
                                        "const titles = _val.init()\n"+
                                        "    .add(\n"+
                                        "        _val.init()\n"+
                                        "            .set('value', 'Name')\n"+
                                        "    ).add(\n"+
                                        "        _val.init()\n"+
                                        "            .set('value', 'Age')\n"+
                                        "    );\n"+
                                        "const endPosition = excel.addDataTable(1, 1, titles)\n" +
                                        "const data = _val.init()\n"+
                                        "    .add(\n"+
                                        "        _val.init()\n"+
                                        "            .add(\n"+
                                        "                 _val.init()\n"+
                                        "                     .set('value', 'John')\n"+
                                        "            ).add(\n"+
                                        "                 _val.init()\n"+
                                        "                     .set('value', 24)\n"+
                                        "            )\n"+
                                        "    ).add(\n"+
                                        "        _val.init()\n"+
                                        "            .add(\n"+
                                        "                 _val.init()\n"+
                                        "                     .set('value', 'Annye')\n"+
                                        "            ).add(\n"+
                                        "                 _val.init()\n"+
                                        "                     .set('value', 22)\n"+
                                        "            )\n"+
                                        "    );\n"+
                                        "endPosition = excel.addDataTable(endPosition.row, 1, data)\n"+
                                        "excel.output(\"ages.xls\");"
                        )
                }
        )
})
public class XLS extends ResourceBase {

    public Workbook workbook = null;

    public Sheet sheet = null;

    public XLS(Proteu proteu, Hili hili) {
        super(proteu, hili);
    }

    public XLS(Proteu proteu, Hili hili, Workbook workbook, Sheet activeSheet) {
        super(proteu, hili);
        this.workbook = workbook;
        this.sheet = activeSheet;
    }

    @MethodDoc(translations = {
        @MethodTranslationDoc(
                language = LanguageDoc.PT,
                description = "Cria um novo documento Excel.",
                howToUse = {
                    @SourceCodeDoc(
                            type = SourceCodeTypeDoc.JavaScript,
                            code = "const excel = _xls.create();"
                    )
                }),
        @MethodTranslationDoc(
                language = LanguageDoc.EN,
                description = "Creates a new Excel document.",
                howToUse = {
                    @SourceCodeDoc(
                            type = SourceCodeTypeDoc.JavaScript,
                            code = "const excel = _xls.create();"
                    )
                })
    }, parameters = {}, returns = {
        @ReturnTranslationDoc(
                language = LanguageDoc.PT,
                description = "Nova instância do recurso XLS."
        ),
        @ReturnTranslationDoc(
                language = LanguageDoc.EN,
                description = "New XLS resource instance."
        )
    })
    public XLS create() {
        Workbook workbook = new XSSFWorkbook();
        return new XLS(getProteu(), getHili(), workbook, workbook.createSheet());
    }

    @MethodDoc(translations = {
            @MethodTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Cria um novo documento Excel 97-2007, no formato antigo.",
                    howToUse = {
                            @SourceCodeDoc(
                                    type = SourceCodeTypeDoc.JavaScript,
                                    code = "const excel = _xls.create2007();"
                            )
                    }),
            @MethodTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Creates a new Excel 97-2007 document, in the old format.",
                    howToUse = {
                            @SourceCodeDoc(
                                    type = SourceCodeTypeDoc.JavaScript,
                                    code = "const excel = _xls.create2007();"
                            )
                    })
    }, parameters = {}, returns = {
            @ReturnTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Nova instância do recurso XLS."
            ),
            @ReturnTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "New XLS resource instance."
            )
    })
    public XLS create2007() {
        Workbook workbook = new HSSFWorkbook();
        return new XLS(getProteu(), getHili(), workbook, workbook.createSheet());
    }

    @MethodDoc(translations = {
            @MethodTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Abre um arquivo do Excel.",
                    howToUse = { }),
            @MethodTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Opens an Excel file.",
                    howToUse = { })
    }, parameters = {
            @ParameterDoc(name = "storage", translations = {
                    @ParameterTranslationDoc(
                            language=LanguageDoc.PT,
                            description = "Referência do storage onde está o arquivo que será aberto."
                    ),
                    @ParameterTranslationDoc(
                            language=LanguageDoc.EN,
                            description = "Storage reference where the file will be opened."
                    )
            }),
            @ParameterDoc(name = "password", translations = {
                    @ParameterTranslationDoc(
                            language=LanguageDoc.PT,
                            name = "senha",
                            description = "Senha do arquivo."
                    ),
                    @ParameterTranslationDoc(
                            language=LanguageDoc.EN,
                            description = "File password."
                    )
            })
    }, returns = {
            @ReturnTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Arquivo aberto em uma nova instância do recurso XLS."
            ),
            @ReturnTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "File opened in a new instance of the XLS resource."
            )
    })
    public XLS open(Storage storage, String password) throws IOException {
        Workbook workbook = WorkbookFactory.create(new java.io.File(FileSystemPath.absoluteFromStorage(getProteu(), storage)), password);
        return new XLS(getProteu(), getHili(), workbook, workbook.getSheetAt(0));
    }

    public XLS open(Storage storage) throws IOException {
        Workbook workbook = WorkbookFactory.create(new java.io.File(FileSystemPath.absoluteFromStorage(getProteu(), storage)));
        return new XLS(getProteu(), getHili(), workbook, workbook.getSheetAt(0));
    }

    @MethodDoc(translations = {
            @MethodTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Abre um arquivo do Excel.",
                    howToUse = { }),
            @MethodTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Opens an Excel file.",
                    howToUse = { })
    }, parameters = {
            @ParameterDoc(name = "file", translations = {
                    @ParameterTranslationDoc(
                            language=LanguageDoc.PT,
                            name = "arquivo",
                            description = "Arquivo do tipo documento de Excel."
                    ),
                    @ParameterTranslationDoc(
                            language=LanguageDoc.EN,
                            description = "Excel document type file."
                    )
            }),
            @ParameterDoc(name = "password", translations = {
                    @ParameterTranslationDoc(
                            language=LanguageDoc.PT,
                            name = "senha",
                            description = "Senha do arquivo."
                    ),
                    @ParameterTranslationDoc(
                            language=LanguageDoc.EN,
                            description = "File password."
                    )
            })
    }, returns = {
            @ReturnTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Arquivo aberto em uma nova instância do recurso XLS."
            ),
            @ReturnTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "File opened in a new instance of the XLS resource."
            )
    })
    public XLS open(File file, String password) throws IOException {
        Workbook workbook = WorkbookFactory.create(new java.io.File(file.getFullPath()), password);
        return new XLS(getProteu(), getHili(), workbook, workbook.getSheetAt(0));
    }

    public XLS open(File file) throws IOException {
        Workbook workbook = WorkbookFactory.create(new java.io.File(file.getFullPath()));
        return new XLS(getProteu(), getHili(), workbook, workbook.getSheetAt(0));
    }

    @MethodDoc(translations = {
            @MethodTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Abre um arquivo do Excel.",
                    howToUse = { }),
            @MethodTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Opens an Excel file.",
                    howToUse = { })
    }, parameters = {
            @ParameterDoc(name = "input", translations = {
                    @ParameterTranslationDoc(
                            language=LanguageDoc.PT,
                            name = "entrada",
                            description = "Objeto de fluxo de entrada de dados do arquivo."
                    ),
                    @ParameterTranslationDoc(
                            language=LanguageDoc.EN,
                            description = "File data input stream object."
                    )
            }),
            @ParameterDoc(name = "password", translations = {
                    @ParameterTranslationDoc(
                            language=LanguageDoc.PT,
                            name = "senha",
                            description = "Senha do arquivo."
                    ),
                    @ParameterTranslationDoc(
                            language=LanguageDoc.EN,
                            description = "File password."
                    )
            })
    }, returns = {
            @ReturnTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Arquivo aberto em uma nova instância do recurso XLS."
            ),
            @ReturnTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "File opened in a new instance of the XLS resource."
            )
    })
    public XLS open(InputStream input, String password) throws IOException {
        Workbook workbook = WorkbookFactory.create(input, password);
        return new XLS(getProteu(), getHili(), workbook, workbook.getSheetAt(0));
    }

    public XLS open(InputStream input) throws IOException {
        Workbook workbook = WorkbookFactory.create(input);
        return new XLS(getProteu(), getHili(), workbook, workbook.getSheetAt(0));
    }

    @MethodDoc(translations = {
            @MethodTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Abre um arquivo do Excel.",
                    howToUse = { }),
            @MethodTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Opens an Excel file.",
                    howToUse = { })
    }, parameters = {
            @ParameterDoc(name = "input", translations = {
                    @ParameterTranslationDoc(
                            language=LanguageDoc.PT,
                            name = "entrada",
                            description = "Objeto de fluxo de entrada de dados do arquivo."
                    ),
                    @ParameterTranslationDoc(
                            language=LanguageDoc.EN,
                            description = "File data input stream object."
                    )
            }),
            @ParameterDoc(name = "password", translations = {
                    @ParameterTranslationDoc(
                            language=LanguageDoc.PT,
                            name = "senha",
                            description = "Senha do arquivo."
                    ),
                    @ParameterTranslationDoc(
                            language=LanguageDoc.EN,
                            description = "File password."
                    )
            })
    }, returns = {
            @ReturnTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Arquivo aberto em uma nova instância do recurso XLS."
            ),
            @ReturnTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "File opened in a new instance of the XLS resource."
            )
    })
    public XLS open(java.io.InputStream input, String password) throws IOException {
        Workbook workbook = WorkbookFactory.create(input, password);
        return new XLS(getProteu(), getHili(), workbook, workbook.getSheetAt(0));
    }

    public XLS open(java.io.InputStream input) throws IOException {
        Workbook workbook = WorkbookFactory.create(input);
        return new XLS(getProteu(), getHili(), workbook, workbook.getSheetAt(0));
    }

    @MethodDoc(dependency = "create", translations = {
            @MethodTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Cria um novo documento Excel.",
                    howToUse = {
                            @SourceCodeDoc(
                                    type = SourceCodeTypeDoc.JavaScript,
                                    code = "const workbook = _xls.create().workbook();"
                            )
                    }),
            @MethodTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Creates a new Excel document.",
                    howToUse = {
                            @SourceCodeDoc(
                                    type = SourceCodeTypeDoc.JavaScript,
                                    code = "const workbook = _xls.create().workbook();"
                            )
                    })
    }, parameters = {}, returns = {
        @ReturnTranslationDoc(
                language = LanguageDoc.PT,
                description = "Obtém o workbook."
        ),
        @ReturnTranslationDoc(
                language = LanguageDoc.EN,
                description = "Gets the workbook."
        )
    })
    public Workbook workbook() {
        return workbook;
    }

    @MethodDoc(dependency = "create", translations = {
        @MethodTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Para obter a folha de cálculos atual que está sendo utilizada para manipular os dados.",
                    howToUse = {
                            @SourceCodeDoc(
                                    type = SourceCodeTypeDoc.JavaScript,
                                    code = "const folhaDeCalculos = _xls.create().sheet();"
                            )
                    }),
        @MethodTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "To obtain the current spreadsheet that is being used to manipulate the data.",
                    howToUse = {
                            @SourceCodeDoc(
                                    type = SourceCodeTypeDoc.JavaScript,
                                    code = "const spreadsheet = _xls.create().sheet();"
                            )
                    })
    }, parameters = {}, returns = {
            @ReturnTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Obtém a folha de cálculos atual."
            ),
            @ReturnTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Gets the current spreadsheet."
            )
    })
    public Sheet sheet() {
        return sheet;
    }

    @MethodDoc(dependency = "create", translations = {
            @MethodTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Obtém o número total de folhas de cálculos.",
                    howToUse = { }),
            @MethodTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Gets the total number of spreadsheets.",
                    howToUse = { })
    }, parameters = {
    }, returns = {
            @ReturnTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "O número total de folhas de cálculos existentes no documento."
            ),
            @ReturnTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "The total number of spreadsheets in the document."
            )
    })
    public int getNumberOfSheets() {
        return workbook.getNumberOfSheets();
    }

    @MethodDoc(dependency = "create", translations = {
            @MethodTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Lista com todas as folhas de cálculos.",
                    howToUse = { }),
            @MethodTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "List of all spreadsheets.",
                    howToUse = { })
    }, parameters = {
    }, returns = {
            @ReturnTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Todas as folhas de cálculos existentes no documento."
            ),
            @ReturnTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "All existing spreadsheets in the document."
            )
    })
    public List<Sheet> getAllSheets() {
        Iterator<Sheet> sheetIterator = workbook.sheetIterator();
        List<Sheet> sheets = new ArrayList<>();
        while (sheetIterator.hasNext()) {
            sheets.add(sheetIterator.next());
        }
        return sheets;
    }

    @MethodDoc(dependency = "create", translations = {
            @MethodTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Obtém a folha de cálculos através do nome.",
                    howToUse = { }),
            @MethodTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Gets the spreadsheet by name.",
                    howToUse = { })
    }, parameters = {
            @ParameterDoc(name = "name", translations = {
                    @ParameterTranslationDoc(
                            language=LanguageDoc.PT,
                            name = "nome",
                            description = "Nome da folha de cálculos."
                    ),
                    @ParameterTranslationDoc(
                            language=LanguageDoc.EN,
                            description = "Spreadsheet name."
                    )
            })
    }, returns = {
            @ReturnTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Folha de cálculos referente ao nome."
            ),
            @ReturnTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Spreadsheet for the name."
            )
    })
    public Sheet getSheet(String name) {
        return workbook.getSheet(name);
    }

    @MethodDoc(dependency = "create", translations = {
            @MethodTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Obtém a folha de cálculos através do número (index).",
                    howToUse = { }),
            @MethodTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Get the spreadsheet by number (index).",
                    howToUse = { })
    }, parameters = {
            @ParameterDoc(name = "index", translations = {
                    @ParameterTranslationDoc(
                            language=LanguageDoc.PT,
                            description = "Número da folha de cálculos."
                    ),
                    @ParameterTranslationDoc(
                            language=LanguageDoc.EN,
                            description = "Spreadsheet number."
                    )
            })
    }, returns = {
            @ReturnTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Folha de cálculos referente ao número (index)."
            ),
            @ReturnTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Spreadsheet referring to the number (index)."
            )
    })
    public Sheet getSheet(int index) {
        return workbook.getSheetAt(index);
    }

    @MethodDoc(dependency = "create", translations = {
            @MethodTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Cria uma nova folha de cálculos.",
                    howToUse = { }),
            @MethodTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Creates a new spreadsheet.",
                    howToUse = { })
    }, parameters = {}, returns = {
            @ReturnTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Folha de cálculos criada."
            ),
            @ReturnTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Spreadsheet created."
            )
    })
    public Sheet createSheet() {
        this.sheet = workbook.createSheet();
        return this.sheet;
    }

    @MethodDoc(dependency = "create", translations = {
            @MethodTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Cria uma nova folha de cálculos e define o seu nome.",
                    howToUse = { }),
            @MethodTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Create a new spreadsheet and define its name.",
                    howToUse = { })
    }, parameters = {
            @ParameterDoc(name = "name", translations = {
                    @ParameterTranslationDoc(
                            language=LanguageDoc.PT,
                            name = "nome",
                            description = "Nome da folha de cálculos."
                    ),
                    @ParameterTranslationDoc(
                            language=LanguageDoc.EN,
                            description = "Spreadsheet name."
                    )
            })
    }, returns = {
            @ReturnTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Folha de cálculos criada."
            ),
            @ReturnTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Spreadsheet created."
            )
    })
    public Sheet createSheet(String name) {
        this.sheet = workbook.createSheet(name);
        return this.sheet;
    }

    @MethodDoc(dependency = "create", translations = {
            @MethodTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Define a folha de cálculos que fica ativa.",
                    howToUse = { }),
            @MethodTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Sets the spreadsheet that is active.",
                    howToUse = { })
    }, parameters = {
            @ParameterDoc(name = "index", translations = {
                    @ParameterTranslationDoc(
                            language=LanguageDoc.PT,
                            description = "Número da folha de cálculos."
                    ),
                    @ParameterTranslationDoc(
                            language=LanguageDoc.EN,
                            description = "Spreadsheet number."
                    )
            })
    }, returns = {
        @ReturnTranslationDoc(
                language = LanguageDoc.PT,
                description = "A instância atual do recurso XLS."
        ),
        @ReturnTranslationDoc(
                language = LanguageDoc.EN,
                description = "A instância atual do recurso XLS."
        )
    })
    public XLS activeSheet(int index) {
        workbook.setActiveSheet(index);
        this.sheet = workbook.getSheetAt(index);
        return this;
    }

    @MethodDoc(dependency = "create", translations = {
            @MethodTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Define a folha de cálculos que fica ativa.",
                    howToUse = { }),
            @MethodTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Sets the spreadsheet that is active.",
                    howToUse = { })
    }, parameters = {
            @ParameterDoc(name = "sheet", translations = {
                    @ParameterTranslationDoc(
                            language=LanguageDoc.PT,
                            description = "Objeto de referência da folha de cálculos."
                    ),
                    @ParameterTranslationDoc(
                            language=LanguageDoc.EN,
                            description = "Spreadsheet reference object."
                    )
            })
    }, returns = {
            @ReturnTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "A instância atual do recurso XLS."
            ),
            @ReturnTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "The current instance of the XLS resource."
            )
    })
    public XLS activeSheet(Sheet sheet) {
        if (sheet instanceof XSSFSheet) {
            int i = 0;
            for (Iterator<Sheet> it = workbook.sheetIterator(); it.hasNext();) {
                Sheet _sheet = it.next();
                if (sheet == _sheet) {
                    workbook.setActiveSheet(i);
                }
                i++;
            }
        } else if (sheet instanceof HSSFSheet) {
            ((HSSFSheet)sheet).setActive(true);
        }
        this.sheet = sheet;
        return this;
    }

    @MethodDoc(translations = {
            @MethodTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Obtém a referência da coluna (letras) com base na sua posição numérico.",
                    howToUse = { }),
            @MethodTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Gets the column reference (letters) based on its numeric position.",
                    howToUse = { })
    }, parameters = {
            @ParameterDoc(name = "index", translations = {
                    @ParameterTranslationDoc(
                            language=LanguageDoc.PT,
                            description = "Número da coluna."
                    ),
                    @ParameterTranslationDoc(
                            language=LanguageDoc.EN,
                            description = "Column number."
                    )
            })
    }, returns = {
            @ReturnTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Referência em letras da coluna."
            ),
            @ReturnTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Reference in column letters."
            )
    })
    public String columnReference(int index) {
        return CellReference.convertNumToColString(index);
    }

    @MethodDoc(translations = {
            @MethodTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Obtém a posição numérica da coluna com base na referência em letras.",
                    howToUse = { }),
            @MethodTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Gets the numeric position of the column based on the letter reference.",
                    howToUse = { })
    }, parameters = {
            @ParameterDoc(name = "index", translations = {
                    @ParameterTranslationDoc(
                            language=LanguageDoc.PT,
                            description = "Referência em letras da coluna."
                    ),
                    @ParameterTranslationDoc(
                            language=LanguageDoc.EN,
                            description = "Reference in column letters."
                    )
            })
    }, returns = {
            @ReturnTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Posição numérica da coluna."
            ),
            @ReturnTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Numeric position of the column."
            )
    })
    public int columnReference(String index) {
        return CellReference.convertColStringToIndex(index);
    }

    @MethodDoc(dependency = "create", translations = {
            @MethodTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Obtém uma linha da folha de cálculos e caso não exista então será criada.",
                    howToUse = { }),
            @MethodTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Gets a row from the spreadsheet and if it does not exist then it will be created.",
                    howToUse = { })
    }, parameters = {
            @ParameterDoc(name = "rowIndex", translations = {
                    @ParameterTranslationDoc(
                            language=LanguageDoc.PT,
                            name = "linha",
                            description = "Número da linha."
                    ),
                    @ParameterTranslationDoc(
                            language=LanguageDoc.EN,
                            description = "Line number."
                    )
            })
    }, returns = {
        @ReturnTranslationDoc(
                language = LanguageDoc.PT,
                description = "Objeto de representação da linha."
        ),
        @ReturnTranslationDoc(
                language = LanguageDoc.EN,
                description = "Line representation object."
        )
    })
    public Row row(int rowIndex) {
        return row(sheet, rowIndex);
    }

    @MethodDoc(dependency = "create", translations = {
            @MethodTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Obtém uma linha da folha de cálculos e caso não exista então será criada.",
                    howToUse = { }),
            @MethodTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Gets a row from the spreadsheet and if it does not exist then it will be created.",
                    howToUse = { })
    }, parameters = {
            @ParameterDoc(name = "sheet", translations = {
                    @ParameterTranslationDoc(
                            language=LanguageDoc.PT,
                            name = "folhaCalculos",
                            description = "Objeto de folha de cálculos."
                    ),
                    @ParameterTranslationDoc(
                            language=LanguageDoc.EN,
                            description = "Spreadsheet object."
                    )
            }),
            @ParameterDoc(name = "rowIndex", translations = {
                    @ParameterTranslationDoc(
                            language=LanguageDoc.PT,
                            name = "linha",
                            description = "Número da linha."
                    ),
                    @ParameterTranslationDoc(
                            language=LanguageDoc.EN,
                            description = "Line number."
                    )
            })
    }, returns = {
        @ReturnTranslationDoc(
                language = LanguageDoc.PT,
                description = "Objeto de representação da linha."
        ),
        @ReturnTranslationDoc(
                language = LanguageDoc.EN,
                description = "Line representation object."
        )
    })
    public Row row(Sheet sheet, int rowIndex) {
        Row row = sheet.getRow(rowIndex);
        if (row == null) {
            row = sheet.createRow(rowIndex);
        }
        return row;
    }

    @MethodDoc(dependency = "create", translations = {
            @MethodTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Obtém uma célula da folha de cálculos e caso não exista então será criada.",
                    howToUse = { }),
            @MethodTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Gets a spreadsheet cell and if it does not exist then it will be created.",
                    howToUse = { })
    }, parameters = {
            @ParameterDoc(name = "rowIndex", translations = {
                    @ParameterTranslationDoc(
                            language=LanguageDoc.PT,
                            name = "linha",
                            description = "Número da linha."
                    ),
                    @ParameterTranslationDoc(
                            language=LanguageDoc.EN,
                            description = "Line number."
                    )
            }),
            @ParameterDoc(name = "colIndex", translations = {
                    @ParameterTranslationDoc(
                            language=LanguageDoc.PT,
                            name = "coluna",
                            description = "Número da coluna."
                    ),
                    @ParameterTranslationDoc(
                            language=LanguageDoc.EN,
                            description = "Column number."
                    )
            })
    }, returns = {
        @ReturnTranslationDoc(
                language = LanguageDoc.PT,
                description = "Objeto de representação da célula."
        ),
        @ReturnTranslationDoc(
                language = LanguageDoc.EN,
                description = "Cell representation object."
        )
    })
    public Cell cell(int rowIndex, int colIndex) {
        return cell(sheet, rowIndex, colIndex);
    }

    @MethodDoc(dependency = "create", translations = {
            @MethodTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Obtém uma célula da folha de cálculos e caso não exista então será criada.",
                    howToUse = { }),
            @MethodTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Gets a spreadsheet cell and if it does not exist then it will be created.",
                    howToUse = { })
    }, parameters = {
        @ParameterDoc(name = "sheet", translations = {
                @ParameterTranslationDoc(
                        language=LanguageDoc.PT,
                        name = "linha",
                        description = "Objeto da folha de cálculos."
                ),
                @ParameterTranslationDoc(
                        language=LanguageDoc.EN,
                        description = "Spreadsheet object."
                )
        }),
        @ParameterDoc(name = "rowIndex", translations = {
                @ParameterTranslationDoc(
                        language=LanguageDoc.PT,
                        name = "linha",
                        description = "Número da linha da folha."
                ),
                @ParameterTranslationDoc(
                        language=LanguageDoc.EN,
                        description = "Line number of the sheet."
                )
        }),
        @ParameterDoc(name = "colIndex", translations = {
                @ParameterTranslationDoc(
                        language=LanguageDoc.PT,
                        name = "coluna",
                        description = "Número da coluna."
                ),
                @ParameterTranslationDoc(
                        language=LanguageDoc.EN,
                        description = "Column number."
                )
        })
    }, returns = {
        @ReturnTranslationDoc(
                language = LanguageDoc.PT,
                description = "Objeto de representação da célula."
        ),
        @ReturnTranslationDoc(
                language = LanguageDoc.EN,
                description = "Cell representation object."
        )
    })
    public Cell cell(Sheet sheet, int rowIndex, int colIndex) {
        Row row = sheet.getRow(rowIndex);
        if (row == null) {
            row = sheet.createRow(rowIndex);
        }
        Cell cell = row.getCell(colIndex);
        if (cell == null) {
            cell = row.createCell(colIndex);
        }
        return cell;
    }

    @MethodDoc(dependency = "create", translations = {
            @MethodTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Obtém um objeto com os dados de uma célula da folha de cálculos.",
                    howToUse = { }),
            @MethodTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Gets an object with data from a spreadsheet cell.",
                    howToUse = { })
    }, parameters = {
            @ParameterDoc(name = "rowIndex", translations = {
                    @ParameterTranslationDoc(
                            language=LanguageDoc.PT,
                            name = "linha",
                            description = "Número da linha."
                    ),
                    @ParameterTranslationDoc(
                            language=LanguageDoc.EN,
                            description = "Line number."
                    )
            }),
            @ParameterDoc(name = "colIndex", translations = {
                    @ParameterTranslationDoc(
                            language=LanguageDoc.PT,
                            name = "coluna",
                            description = "Número da coluna."
                    ),
                    @ParameterTranslationDoc(
                            language=LanguageDoc.EN,
                            description = "Column number."
                    )
            })
    }, returns = {
            @ReturnTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Informações de dados da célula."
            ),
            @ReturnTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Cell data information."
            )
    })
    public Values getCellData(int rowIndex, int colIndex) {
        return getCellData(sheet, rowIndex, colIndex);
    }

    @MethodDoc(dependency = "create", translations = {
            @MethodTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Obtém um objeto com os dados de uma célula da folha de cálculos.",
                    howToUse = { }),
            @MethodTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Gets an object with data from a spreadsheet cell.",
                    howToUse = { })
    }, parameters = {
            @ParameterDoc(name = "sheet", translations = {
                    @ParameterTranslationDoc(
                            language=LanguageDoc.PT,
                            name = "linha",
                            description = "Objeto da folha de cálculos."
                    ),
                    @ParameterTranslationDoc(
                            language=LanguageDoc.EN,
                            description = "Spreadsheet object."
                    )
            }),
            @ParameterDoc(name = "rowIndex", translations = {
                    @ParameterTranslationDoc(
                            language=LanguageDoc.PT,
                            name = "linha",
                            description = "Número da linha da folha."
                    ),
                    @ParameterTranslationDoc(
                            language=LanguageDoc.EN,
                            description = "Line number of the sheet."
                    )
            }),
            @ParameterDoc(name = "colIndex", translations = {
                    @ParameterTranslationDoc(
                            language=LanguageDoc.PT,
                            name = "coluna",
                            description = "Número da coluna."
                    ),
                    @ParameterTranslationDoc(
                            language=LanguageDoc.EN,
                            description = "Column number."
                    )
            })
    }, returns = {
            @ReturnTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Informações de dados da célula."
            ),
            @ReturnTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Cell data information."
            )
    })
    public Values getCellData(Sheet sheet, int rowIndex, int colIndex) {
        Row row = sheet.getRow(rowIndex);
        if (row == null) {
            return null;
        }
        Cell cell = row.getCell(colIndex);
        if (cell == null) {
            return null;
        }
        return getCellData(cell);
    }

    @MethodDoc(dependency = "create", translations = {
            @MethodTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Obtém um objeto com os dados de uma célula da folha de cálculos.",
                    howToUse = { }),
            @MethodTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Gets an object with data from a spreadsheet cell.",
                    howToUse = { })
    }, parameters = {
            @ParameterDoc(name = "cell", translations = {
                    @ParameterTranslationDoc(
                            language=LanguageDoc.PT,
                            name = "celula",
                            description = "Objeto que representa a célula."
                    ),
                    @ParameterTranslationDoc(
                            language=LanguageDoc.EN,
                            description = "Object that represents the cell."
                    )
            })
    }, returns = {
            @ReturnTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Informações de dados da célula."
            ),
            @ReturnTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Cell data information."
            )
    })
    public Values getCellData(Cell cell) {
        Values cellData = new Values();
        cellData.set("column", cell.getColumnIndex());
        cellData.set("row", cell.getRowIndex());
        cellData.set("address", cell.getAddress().formatAsString());
        if (cell.getCellType() == CellType.STRING) {
            cellData.set("type", "string");
            cellData.set("value", cell.getStringCellValue());
            cellData.set("richValue", cell.getRichStringCellValue());
        } else if (cell.getCellType() == CellType.NUMERIC) {
            cellData.set("type", "numeric");
            cellData.set("value", cell.getNumericCellValue());
            if (DateUtil.isCellDateFormatted(cell)) {
                try {
                    cellData.set("localDateTime", cell.getLocalDateTimeCellValue());
                    cellData.set("localDate", cell.getLocalDateTimeCellValue().toLocalDate());
                    cellData.set("localTime", cell.getLocalDateTimeCellValue().toLocalTime());
                    cellData.set("instant", cell.getDateCellValue().toInstant().atZone(java.time.ZoneId.systemDefault()));
                    cellData.set("date", cell.getDateCellValue());
                } catch (NullPointerException e) {
                    cellData.set("localDateTime", null);
                    cellData.set("localDate", null);
                    cellData.set("localTime", null);
                    cellData.set("instant", null);
                    cellData.set("date", null);
                }
            }
        } else if (cell.getCellType() == CellType.BOOLEAN) {
            cellData.set("type", "boolean");
            cellData.set("value", cell.getBooleanCellValue());
        } else if (cell.getCellType() == CellType.BLANK) {
            cellData.set("type", "blank");
        } else if (cell.getCellType() == CellType.FORMULA) {
            cellData.set("type", "formula");
            cellData.set("formula", cell.getCellFormula());
            switch (cell.getCachedFormulaResultType()) {
                case NUMERIC:
                    cellData.set("value", cell.getNumericCellValue());
                    break;
                case STRING:
                    cellData.set("value", cell.getStringCellValue());
                    cellData.set("richValue", cell.getRichStringCellValue());
                    break;
            }
        } else if (cell.getCellType() == CellType.ERROR) {
            cellData.set("type", "error");
            cellData.set("value", cell.getErrorCellValue());
            try {
                cellData.set("code", FormulaError.forInt(cell.getErrorCellValue()).getString());
            } catch (Exception e) { }
        }
        return cellData;
    }

    @MethodDoc(dependency = "create", translations = {
            @MethodTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Adiciona uma tabela de dados na folha de cálculos.",
                    howToUse = { }),
            @MethodTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Adds a data table to the spreadsheet.",
                    howToUse = { })
    }, parameters = {
            @ParameterDoc(name = "config", translations = {
                    @ParameterTranslationDoc(
                            language=LanguageDoc.PT,
                            description = "Configuração da tabela de dados."
                    ),
                    @ParameterTranslationDoc(
                            language=LanguageDoc.EN,
                            description = "Configuration of the data table."
                    )
            }),
            @ParameterDoc(name = "data", translations = {
                    @ParameterTranslationDoc(
                            language=LanguageDoc.PT,
                            name = "dados",
                            description = "Lista de dados que serão inseridos."
                    ),
                    @ParameterTranslationDoc(
                            language=LanguageDoc.EN,
                            description = "List of data to be inserted."
                    )
            })
    }, returns = {
        @ReturnTranslationDoc(
                language = LanguageDoc.PT,
                description = "Referência da posição da última célula com dados inserida."
        ),
        @ReturnTranslationDoc(
                language = LanguageDoc.EN,
                description = "Reference of the position of the last cell with data entered."
        )
    })
    public XLSPosition addDataTable(Map config, List data) {
        Values conf = new Values(config);
        Sheet sheet = this.sheet;
        if (conf.hasKey("sheet")) {
            sheet = (Sheet)config.get("sheet");
        }
        int rowIndex = conf.getInt("row");
        int colIndex = conf.getInt("col");
        boolean vertical = conf.getBoolean("vertical");
        return addDataTable(sheet, rowIndex, colIndex, new Values(data), vertical);
    }

    @MethodDoc(dependency = "create", translations = {
            @MethodTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Adiciona uma tabela de dados na folha de cálculos.",
                    howToUse = { }),
            @MethodTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Adds a data table to the spreadsheet.",
                    howToUse = { })
    }, parameters = {
            @ParameterDoc(name = "rowIndex", translations = {
                    @ParameterTranslationDoc(
                            language=LanguageDoc.PT,
                            name = "linha",
                            description = "Número da linha."
                    ),
                    @ParameterTranslationDoc(
                            language=LanguageDoc.EN,
                            description = "Line number."
                    )
            }),
            @ParameterDoc(name = "colIndex", translations = {
                    @ParameterTranslationDoc(
                            language=LanguageDoc.PT,
                            name = "coluna",
                            description = "Número da coluna."
                    ),
                    @ParameterTranslationDoc(
                            language=LanguageDoc.EN,
                            description = "Column number."
                    )
            }),
            @ParameterDoc(name = "data", translations = {
                    @ParameterTranslationDoc(
                            language=LanguageDoc.PT,
                            name = "dados",
                            description = "Lista de dados que serão inseridos."
                    ),
                    @ParameterTranslationDoc(
                            language=LanguageDoc.EN,
                            description = "List of data to be inserted."
                    )
            })
    }, returns = {
        @ReturnTranslationDoc(
                language = LanguageDoc.PT,
                description = "Referência da posição da última célula com dados inserida."
        ),
        @ReturnTranslationDoc(
                language = LanguageDoc.EN,
                description = "Reference of the position of the last cell with data entered."
        )
    })
    public XLSPosition addDataTable(int rowIndex, int colIndex, List data) {
        return addDataTable(sheet, rowIndex, colIndex, new Values(data), false);
    }

    public XLSPosition addDataTable(int rowIndex, int colIndex, Values data) {
        return addDataTable(sheet, rowIndex, colIndex, data, false);
    }

    @MethodDoc(dependency = "create", translations = {
            @MethodTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Adiciona uma tabela de dados na folha de cálculos.",
                    howToUse = { }),
            @MethodTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Adds a data table to the spreadsheet.",
                    howToUse = { })
    }, parameters = {
            @ParameterDoc(name = "rowIndex", translations = {
                    @ParameterTranslationDoc(
                            language=LanguageDoc.PT,
                            name = "linha",
                            description = "Número da linha."
                    ),
                    @ParameterTranslationDoc(
                            language=LanguageDoc.EN,
                            description = "Line number."
                    )
            }),
            @ParameterDoc(name = "colIndex", translations = {
                    @ParameterTranslationDoc(
                            language=LanguageDoc.PT,
                            name = "coluna",
                            description = "Número da coluna."
                    ),
                    @ParameterTranslationDoc(
                            language=LanguageDoc.EN,
                            description = "Column number."
                    )
            }),
            @ParameterDoc(name = "data", translations = {
                    @ParameterTranslationDoc(
                            language=LanguageDoc.PT,
                            name = "dados",
                            description = "Lista de dados que serão inseridos."
                    ),
                    @ParameterTranslationDoc(
                            language=LanguageDoc.EN,
                            description = "List of data to be inserted."
                    )
            }),
            @ParameterDoc(name = "vertical", translations = {
                    @ParameterTranslationDoc(
                            language=LanguageDoc.PT,
                            description = "Inserir dados na vertical."
                    ),
                    @ParameterTranslationDoc(
                            language=LanguageDoc.EN,
                            description = "Insert data vertically."
                    )
            })
    }, returns = {
        @ReturnTranslationDoc(
                language = LanguageDoc.PT,
                description = "Referência da posição da última célula com dados inserida."
        ),
        @ReturnTranslationDoc(
                language = LanguageDoc.EN,
                description = "Reference of the position of the last cell with data entered."
        )
    })
    public XLSPosition addDataTable(int rowIndex, int colIndex, List data, boolean vertical) {
        return addDataTable(sheet, rowIndex, colIndex, new Values(data), vertical);
    }

    
    public XLSPosition addDataTable(int rowIndex, int colIndex, Values data, boolean vertical) {
        return addDataTable(sheet, rowIndex, colIndex, data, vertical);
    }

    @MethodDoc(dependency = "create", translations = {
            @MethodTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Adiciona uma tabela de dados na folha de cálculos.",
                    howToUse = { }),
            @MethodTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Adds a data table to the spreadsheet.",
                    howToUse = { })
    }, parameters = {
            @ParameterDoc(name = "sheet", translations = {
                    @ParameterTranslationDoc(
                            language=LanguageDoc.PT,
                            name = "folhaCalculos",
                            description = "Objeto da folha de cálculos."
                    ),
                    @ParameterTranslationDoc(
                            language=LanguageDoc.EN,
                            description = "Spreadsheet object."
                    )
            }),
            @ParameterDoc(name = "rowIndex", translations = {
                    @ParameterTranslationDoc(
                            language=LanguageDoc.PT,
                            name = "linha",
                            description = "Número da linha."
                    ),
                    @ParameterTranslationDoc(
                            language=LanguageDoc.EN,
                            description = "Line number."
                    )
            }),
            @ParameterDoc(name = "colIndex", translations = {
                    @ParameterTranslationDoc(
                            language=LanguageDoc.PT,
                            name = "coluna",
                            description = "Número da coluna."
                    ),
                    @ParameterTranslationDoc(
                            language=LanguageDoc.EN,
                            description = "Column number."
                    )
            }),
            @ParameterDoc(name = "data", translations = {
                    @ParameterTranslationDoc(
                            language=LanguageDoc.PT,
                            name = "dados",
                            description = "Lista de dados que serão inseridos."
                    ),
                    @ParameterTranslationDoc(
                            language=LanguageDoc.EN,
                            description = "List of data to be inserted."
                    )
            }),
            @ParameterDoc(name = "vertical", translations = {
                    @ParameterTranslationDoc(
                            language=LanguageDoc.PT,
                            description = "Inserir os dados na vertical."
                    ),
                    @ParameterTranslationDoc(
                            language=LanguageDoc.EN,
                            description = "Insert the data vertically."
                    )
            })
    }, returns = {
        @ReturnTranslationDoc(
                language = LanguageDoc.PT,
                description = "Referência da posição da última célula com dados inserida."
        ),
        @ReturnTranslationDoc(
                language = LanguageDoc.EN,
                description = "Reference of the position of the last cell with data entered."
        )
    })
    public XLSPosition addDataTable(Sheet sheet, int rowIndex, int colIndex, List data) {
        return addDataTable(sheet, rowIndex, colIndex, new Values(data), false);
    }

    public XLSPosition addDataTable(Sheet sheet, int rowIndex, int colIndex, Values data) {
        return addDataTable(sheet, rowIndex, colIndex, new Values(data), false);
    }

    public XLSPosition addDataTable(Sheet sheet, int rowIndex, int colIndex, List data, boolean vertical) {
        return addDataTable(sheet, rowIndex, colIndex, new Values(data), vertical);
    }
    
    public XLSPosition addDataTable(Sheet sheet, int rowIndex, int colIndex, Values data, boolean vertical) {
        if (data.isList()) {
            int rowCount = rowIndex;
            int colCount = colIndex;
            if (vertical) {
                boolean endWithList = false;
                for (Values values : data.listOfValues()) {
                    if (values.isList()) {
                        XLSPosition position = addDataTable(sheet, rowIndex, colCount, values, vertical);
                        colCount = position.getCol();
                        endWithList = true;
                    } else {
                        setCellData(sheet, rowCount, colCount, values);
                        endWithList = false;
                    }
                    rowCount++;
                }
                if (!endWithList) {
                    colCount++;
                }
                return new XLSPosition(rowCount, colCount);
            } else {
                boolean endWithList = false;
                for (Values values : data.listOfValues()) {
                    if (values.isList()) {
                        XLSPosition position = addDataTable(sheet, rowCount, colIndex, values, vertical);
                        rowCount = position.getRow();
                        endWithList = true;
                    } else {
                        setCellData(sheet, rowCount, colCount, values);
                        endWithList = false;
                    }
                    colCount++;
                }
                if (!endWithList) {
                    rowCount++;
                }
                return new XLSPosition(rowCount, colCount);
            }
        }
        return null;
    }

    @MethodDoc(translations = {
            @MethodTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Insere dados numa célula específica.",
                    howToUse = { }),
            @MethodTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Insert data in a specific cell.",
                    howToUse = { })
    }, parameters = {
            @ParameterDoc(name = "row", translations = {
                    @ParameterTranslationDoc(
                            language=LanguageDoc.PT,
                            name = "linha",
                            description = "Número da linha."
                    ),
                    @ParameterTranslationDoc(
                            language=LanguageDoc.EN,
                            description = "Line number."
                    )
            }),
            @ParameterDoc(name = "col", translations = {
                    @ParameterTranslationDoc(
                            language=LanguageDoc.PT,
                            name = "coluna",
                            description = "Número da coluna."
                    ),
                    @ParameterTranslationDoc(
                            language=LanguageDoc.EN,
                            description = "Column number."
                    )
            }),
            @ParameterDoc(name = "data", translations = {
                    @ParameterTranslationDoc(
                            language=LanguageDoc.PT,
                            name = "dados",
                            description = "Dados que serão inseridos."
                    ),
                    @ParameterTranslationDoc(
                            language=LanguageDoc.EN,
                            description = "Data to be inserted."
                    )
            })
    }, returns = {
        @ReturnTranslationDoc(
                language = LanguageDoc.PT,
                description = "A instância atual do recurso XLS."
        ),
        @ReturnTranslationDoc(
                language = LanguageDoc.EN,
                description = "The current instance of the XLS resource."
        )
    })
    public XLS setCellData(int row, int col, Map data) {
        return setCellData(cell(row, col), data);
    }

    public XLS setCellData(int row, int col, Values data) {
        return setCellData(cell(row, col), data);
    }

    @MethodDoc(translations = {
            @MethodTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Insere dados numa célula específica em uma planilha de cálculos.",
                    howToUse = { }),
            @MethodTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Inserts data into a specific cell in a spreadsheet.",
                    howToUse = { })
    }, parameters = {
            @ParameterDoc(name = "sheet", translations = {
                    @ParameterTranslationDoc(
                            language=LanguageDoc.PT,
                            name = "folhaCalculos",
                            description = "Objeto da folha de cálculos."
                    ),
                    @ParameterTranslationDoc(
                            language=LanguageDoc.EN,
                            description = "Spreadsheet object."
                    )
            }),
            @ParameterDoc(name = "row", translations = {
                    @ParameterTranslationDoc(
                            language=LanguageDoc.PT,
                            name = "linha",
                            description = "Número da linha."
                    ),
                    @ParameterTranslationDoc(
                            language=LanguageDoc.EN,
                            description = "Line number."
                    )
            }),
            @ParameterDoc(name = "col", translations = {
                    @ParameterTranslationDoc(
                            language=LanguageDoc.PT,
                            name = "coluna",
                            description = "Número da coluna."
                    ),
                    @ParameterTranslationDoc(
                            language=LanguageDoc.EN,
                            description = "Column number."
                    )
            }),
            @ParameterDoc(name = "data", translations = {
                    @ParameterTranslationDoc(
                            language=LanguageDoc.PT,
                            name = "dados",
                            description = "Dados que serão inseridos."
                    ),
                    @ParameterTranslationDoc(
                            language=LanguageDoc.EN,
                            description = "Data to be inserted."
                    )
            })
    }, returns = {
            @ReturnTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "A instância atual do recurso XLS."
            ),
            @ReturnTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "The current instance of the XLS resource."
            )
    })
    public XLS setCellData(Sheet sheet, int row, int col, Map data) {
        return setCellData(cell(sheet, row, col), data);
    }

    public XLS setCellData(Sheet sheet, int row, int col, Values data) {
        return setCellData(cell(sheet, row, col), data);
    }

    @MethodDoc(translations = {
            @MethodTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Insere dados numa célula específica.",
                    howToUse = { }),
            @MethodTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Insert data in a specific cell.",
                    howToUse = { })
    }, parameters = {
            @ParameterDoc(name = "cell", translations = {
                    @ParameterTranslationDoc(
                            language=LanguageDoc.PT,
                            name = "celula",
                            description = "Objeto da célula."
                    ),
                    @ParameterTranslationDoc(
                            language=LanguageDoc.EN,
                            description = "Cell object."
                    )
            }),
            @ParameterDoc(name = "data", translations = {
                    @ParameterTranslationDoc(
                            language=LanguageDoc.PT,
                            name = "dados",
                            description = "Dados que serão inseridos."
                    ),
                    @ParameterTranslationDoc(
                            language=LanguageDoc.EN,
                            description = "Data to be inserted."
                    )
            })
    }, returns = {
        @ReturnTranslationDoc(
                language = LanguageDoc.PT,
                description = "A instância atual do recurso XLS."
        ),
        @ReturnTranslationDoc(
                language = LanguageDoc.EN,
                description = "The current instance of the XLS resource."
        )
    })
    public XLS setCellData(Cell cell, Map data) {
        return setCellData(cell, new Values(data));
    }
    
    public XLS setCellData(Cell cell, Values values) {
        if (values.hasKey("value")) {
            Object val = values.get("value");
            if (val instanceof String) {
                cell.setCellValue(values.getString("value"));
            } else if (val instanceof Boolean) {
                cell.setCellValue(values.getBoolean("value"));
            } else if (val instanceof Byte
                    || val instanceof Short
                    || val instanceof Integer
                    || val instanceof Long
                    || val instanceof Float
                    || val instanceof Double) {
                cell.setCellValue(values.getDouble("value"));
            } else if (val instanceof java.sql.Timestamp) {
                cell.setCellValue(values.getSQLTimestamp("value").toLocalDateTime());
            } else if (val instanceof java.time.LocalDateTime) {
                cell.setCellValue(values.getLocalDateTime("value"));
            } else if (val instanceof java.time.LocalDate) {
                cell.setCellValue(values.getLocalDate("value"));
            } if (val instanceof Date || val instanceof java.sql.Date || val instanceof java.sql.Time
                || val instanceof java.time.Instant || val instanceof java.time.LocalTime) {
                cell.setCellValue(values.getDate("value"));
            } else if (val instanceof Calendar) {
                cell.setCellValue(values.getCalendar("value"));
            }
        }
        if (values.hasKey("type")) {
            String type = values.getString("type");
            CellType cellType = CellType._NONE;
            if (type.equalsIgnoreCase("string")) {
                cellType = cellType.STRING;
            } else if (type.equalsIgnoreCase("numeric")) {
                cellType = cellType.NUMERIC;
            } else if (type.equalsIgnoreCase("formula")) {
                cellType = cellType.FORMULA;
            } else if (type.equalsIgnoreCase("blank")) {
                cellType = cellType.BLANK;
            } else if (type.equalsIgnoreCase("boolean")) {
                cellType = cellType.BOOLEAN;
            } else if (type.equalsIgnoreCase("error")) {
                cellType = cellType.ERROR;
            }
            cell.setCellType(cellType);
        }
        if (values.hasKey("formula")) {
            cell.setCellFormula(values.getString("formula"));
        }
        if (values.hasKey("style")) {
            cell.setCellStyle((CellStyle) values.get("style"));
        }
        return this;
    }

    @MethodDoc(translations = {
            @MethodTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Transforma o texto passado para ser um nome válido de folha de cálculos cumprindo as regras do Excel.",
                    howToUse = { }),
            @MethodTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Transforms the passed text to be a valid spreadsheet name while complying with Excel rules.",
                    howToUse = { })
    }, parameters = {
            @ParameterDoc(name = "nameProposal", translations = {
                    @ParameterTranslationDoc(
                            language=LanguageDoc.PT,
                            name = "nome",
                            description = "Nome que deverá ser transformado."
                    ),
                    @ParameterTranslationDoc(
                            language=LanguageDoc.EN,
                            description = "Name that should be transformed."
                    )
            }),
            @ParameterDoc(name = "replaceChar", translations = {
                    @ParameterTranslationDoc(
                            language=LanguageDoc.PT,
                            name = "caracter",
                            description = "Carácter de substituição."
                    ),
                    @ParameterTranslationDoc(
                            language=LanguageDoc.EN,
                            description = "Replacement character."
                    )
            })
    }, returns = {
        @ReturnTranslationDoc(
                language = LanguageDoc.PT,
                description = "Nome válido para ser utilizado como nome da folha de cálculos."
        ),
        @ReturnTranslationDoc(
                language = LanguageDoc.EN,
                description = "Valid name to be used as the name of the spreadsheet."
        )
    })
    public String safeSheetName(String nameProposal, char replaceChar) {
        return WorkbookUtil.createSafeSheetName(nameProposal, replaceChar);
    }
    
    public String safeSheetName(String nameProposal) {
        return WorkbookUtil.createSafeSheetName(nameProposal);
    }

    @MethodDoc(translations = {
            @MethodTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Verifica se o texto passado é um nome válido de folha de cálculos que cumpre as regras do Excel.",
                    howToUse = { }),
            @MethodTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Checks whether the passed text is a valid spreadsheet name that complies with Excel rules.",
                    howToUse = { })
    }, parameters = {
            @ParameterDoc(name = "nameProposal", translations = {
                    @ParameterTranslationDoc(
                            language=LanguageDoc.PT,
                            name = "nome",
                            description = "Nome que deverá ser validado."
                    ),
                    @ParameterTranslationDoc(
                            language=LanguageDoc.EN,
                            description = "Name that must be validated."
                    )
            })
    }, returns = {
        @ReturnTranslationDoc(
                language = LanguageDoc.PT,
                description = "Se é válido para ser utilizado como nome da folha de cálculos."
        ),
        @ReturnTranslationDoc(
                language = LanguageDoc.EN,
                description = "Whether it is valid to be used as a spreadsheet name."
        )
    })
    public boolean validSheetName(String nameProposal) {
        try {
            WorkbookUtil.validateSheetName(nameProposal);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @MethodDoc(translations = {
        @MethodTranslationDoc(
                language = LanguageDoc.PT,
                description = "Cria um novo estilo de fonte no workbook.",
                howToUse = { }),
        @MethodTranslationDoc(
                language = LanguageDoc.EN,
                description = "Creates a new font style in the workbook.",
                howToUse = { })
    }, parameters = {
    }, returns = {
        @ReturnTranslationDoc(
                language = LanguageDoc.PT,
                description = "O novo estilo de fonte criado."
        ),
        @ReturnTranslationDoc(
                language = LanguageDoc.EN,
                description = "The new font style created."
        )
    })
    public Font font() {
        return this.workbook.createFont();
    }

    @MethodDoc(translations = {
        @MethodTranslationDoc(
                language = LanguageDoc.PT,
                description = "Cria um novo estilo de célula no workbook.",
                howToUse = { }),
        @MethodTranslationDoc(
                language = LanguageDoc.EN,
                description = "Creates a new cell style in the workbook.",
                howToUse = { })
    }, parameters = {
    }, returns = {
        @ReturnTranslationDoc(
                language = LanguageDoc.PT,
                description = "O novo estilo de célula criado."
        ),
        @ReturnTranslationDoc(
                language = LanguageDoc.EN,
                description = "The new cell style created."
        )
    })
    public CellStyle cellStyle() {
        return this.workbook.createCellStyle();
    }

    @MethodDoc(translations = {
        @MethodTranslationDoc(
                language = LanguageDoc.PT,
                description = "Cria um novo formato de célula no workbook.",
                howToUse = { }),
        @MethodTranslationDoc(
                language = LanguageDoc.EN,
                description = "Creates a new cell format in the workbook.",
                howToUse = { })
    }, parameters = {
        @ParameterDoc(name = "format", translations = {
                @ParameterTranslationDoc(
                        language=LanguageDoc.PT,
                        name = "formato",
                        description = "Definição do padrão do formato."
                ),
                @ParameterTranslationDoc(
                        language=LanguageDoc.EN,
                        description = "Format standard definition."
                )
        })
    }, returns = {
        @ReturnTranslationDoc(
                language = LanguageDoc.PT,
                description = "Código identificador do novo formato."
        ),
        @ReturnTranslationDoc(
                language = LanguageDoc.EN,
                description = "Identifier code for the new format."
        )
    })
    public short format(String format) {
        return this.workbook.getCreationHelper().createDataFormat().getFormat(format);
    }

    @MethodDoc(translations = {
        @MethodTranslationDoc(
                language = LanguageDoc.PT,
                description = "Cria um novo estilo de célula com um formato associado no workbook.",
                howToUse = { }),
        @MethodTranslationDoc(
                language = LanguageDoc.EN,
                description = "Creates a new cell style with an associated format in the workbook.",
                howToUse = { })
    }, parameters = {
        @ParameterDoc(name = "format", translations = {
                @ParameterTranslationDoc(
                        language=LanguageDoc.PT,
                        name = "formato",
                        description = "Definição do padrão do formato."
                ),
                @ParameterTranslationDoc(
                        language=LanguageDoc.EN,
                        description = "Format standard definition."
                )
        })
    }, returns = {
        @ReturnTranslationDoc(
                language = LanguageDoc.PT,
                description = "O novo estilo de célula criado com o formato configurado."
        ),
        @ReturnTranslationDoc(
                language = LanguageDoc.EN,
                description = "The new cell style created with the configured format."
        )
    })
    public CellStyle cellStyleFormat(String format) {
        CellStyle cellStyle = cellStyle();
        cellStyle.setDataFormat(format(format));
        return cellStyle;
    }

    @MethodDoc(translations = {
            @MethodTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Gera o alinhamento horizontal.",
                    howToUse = { }),
            @MethodTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Generates horizontal alignment.",
                    howToUse = { })
    }, parameters = {
            @ParameterDoc(name = "type", translations = {
                    @ParameterTranslationDoc(
                            language=LanguageDoc.PT,
                            name = "tipo",
                            description = "Nome do tipo de alinhamento horizontal, suporta:<br/>"+
                                    "<ul>"+
                                    "<li>general</li>"+
                                    "<li>left</li>"+
                                    "<li>center</li>"+
                                    "<li>right</li>"+
                                    "<li>fill</li>"+
                                    "<li>justify</li>"+
                                    "<li>center-selection</li>"+
                                    "<li>distributed</li>"+
                                    "</ul>"
                    ),
                    @ParameterTranslationDoc(
                            language=LanguageDoc.EN,
                            description = "Horizontal alignment type name, supports:<br/>"+
                                    "<ul>"+
                                    "<li>general</li>"+
                                    "<li>left</li>"+
                                    "<li>center</li>"+
                                    "<li>right</li>"+
                                    "<li>fill</li>"+
                                    "<li>justify</li>"+
                                    "<li>center-selection</li>"+
                                    "<li>distributed</li>"+
                                    "</ul>"
                    )
            })
    }, returns = {
        @ReturnTranslationDoc(
                language = LanguageDoc.PT,
                description = "O alinhamento horizontal configurado com o tipo definido."
        ),
        @ReturnTranslationDoc(
                language = LanguageDoc.EN,
                description = "The horizontal alignment configured with the defined type."
        )
    })
    public HorizontalAlignment horizontalAlignment(String value) {
        return HorizontalAlignment.valueOf(value.toUpperCase().replace("-", "_"));
    }

    @MethodDoc(translations = {
            @MethodTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Gera o alinhamento vertical.",
                    howToUse = { }),
            @MethodTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Generates vertical alignment.",
                    howToUse = { })
    }, parameters = {
            @ParameterDoc(name = "type", translations = {
                    @ParameterTranslationDoc(
                            language=LanguageDoc.PT,
                            name = "tipo",
                            description = "Nome do tipo de alinhamento vertical, suporta:<br/>"+
                                    "<ul>"+
                                    "<li>top</li>"+
                                    "<li>center</li>"+
                                    "<li>bottom</li>"+
                                    "<li>justify</li>"+
                                    "<li>distributed</li>"+
                                    "</ul>"
                    ),
                    @ParameterTranslationDoc(
                            language=LanguageDoc.EN,
                            description = "Vertical alignment type name, supports:<br/>"+
                                    "<ul>"+
                                    "<li>top</li>"+
                                    "<li>center</li>"+
                                    "<li>bottom</li>"+
                                    "<li>justify</li>"+
                                    "<li>distributed</li>"+
                                    "</ul>"
                    )
            })
    }, returns = {
        @ReturnTranslationDoc(
                language = LanguageDoc.PT,
                description = "O alinhamento vertical configurado com o tipo definido."
        ),
        @ReturnTranslationDoc(
                language = LanguageDoc.EN,
                description = "The vertical alignment configured with the defined type."
        )
    })
    public VerticalAlignment verticalAlignment(String value) {
        return VerticalAlignment.valueOf(value.toUpperCase().replace("-", "_"));
    }

    @MethodDoc(translations = {
            @MethodTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Gera o padrão de preenchimento de fundo.",
                    howToUse = { }),
            @MethodTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Generates the background fill pattern.",
                    howToUse = { })
    }, parameters = {
            @ParameterDoc(name = "value", translations = {
                    @ParameterTranslationDoc(
                            language=LanguageDoc.PT,
                            name = "tipo",
                            description = "Tipo do padrão de preenchimento, suporta:<br/>"+
                                    "<ul>"+
                                    "<li>no-fill</li>"+
                                    "<li>solid-foreground</li>"+
                                    "<li>fine-dots</li>"+
                                    "<li>alt-bars</li>"+
                                    "<li>sparse-dots</li>"+
                                    "<li>thick-horz-bands</li>"+
                                    "<li>thick-vert-bands</li>"+
                                    "<li>thick-backward-diag</li>"+
                                    "<li>thick-forward-diag</li>"+
                                    "<li>big-spots</li>"+
                                    "<li>bricks</li>"+
                                    "<li>thin-horz-bands</li>"+
                                    "<li>thin-vert-bands</li>"+
                                    "<li>thin-backward-diag</li>"+
                                    "<li>thin-forward-diag</li>"+
                                    "<li>squares</li>"+
                                    "<li>diamonds</li>"+
                                    "<li>less-dots</li>"+
                                    "<li>least-dots</li>"+
                                    "</ul>"
                    ),
                    @ParameterTranslationDoc(
                            language=LanguageDoc.EN,
                            name = "type",
                            description = "Fill pattern type, supports:<br/>"+
                                    "<ul>"+
                                    "<li>no-fill</li>"+
                                    "<li>solid-foreground</li>"+
                                    "<li>fine-dots</li>"+
                                    "<li>alt-bars</li>"+
                                    "<li>sparse-dots</li>"+
                                    "<li>thick-horz-bands</li>"+
                                    "<li>thick-vert-bands</li>"+
                                    "<li>thick-backward-diag</li>"+
                                    "<li>thick-forward-diag</li>"+
                                    "<li>big-spots</li>"+
                                    "<li>bricks</li>"+
                                    "<li>thin-horz-bands</li>"+
                                    "<li>thin-vert-bands</li>"+
                                    "<li>thin-backward-diag</li>"+
                                    "<li>thin-forward-diag</li>"+
                                    "<li>squares</li>"+
                                    "<li>diamonds</li>"+
                                    "<li>less-dots</li>"+
                                    "<li>least-dots</li>"+
                                    "</ul>"
                    )
            })
    }, returns = {
        @ReturnTranslationDoc(
                language = LanguageDoc.PT,
                description = "O padrão de preenchimento configurado com o tipo definido."
        ),
        @ReturnTranslationDoc(
                language = LanguageDoc.EN,
                description = "The fill pattern configured with the defined type."
        )
    })
    public FillPatternType fillPattern(String value) {
        return FillPatternType.valueOf(value.toUpperCase().replace("-", "_"));
    }

    @MethodDoc(translations = {
            @MethodTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Gera o estilo de contorno das bordas das células.",
                    howToUse = { }),
            @MethodTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Generates the outline style of the cell borders.",
                    howToUse = { })
    }, parameters = {
            @ParameterDoc(name = "type", translations = {
                    @ParameterTranslationDoc(
                            language=LanguageDoc.PT,
                            name = "tipo",
                            description = "Nome do tipo de estilo de bordas, suporta:<br/>"+
                                    "<ul>"+
                                    "<li>none</li>"+
                                    "<li>thin</li>"+
                                    "<li>medium</li>"+
                                    "<li>dashed</li>"+
                                    "<li>dotted</li>"+
                                    "<li>thick</li>"+
                                    "<li>double</li>"+
                                    "<li>hair</li>"+
                                    "<li>medium-dashed</li>"+
                                    "<li>dash-dot</li>"+
                                    "<li>medium-dash-dot</li>"+
                                    "</ul>"
                    ),
                    @ParameterTranslationDoc(
                            language=LanguageDoc.EN,
                            description = "Name of the border style type, supports:<br/>"+
                                    "<ul>"+
                                    "<li>none</li>"+
                                    "<li>thin</li>"+
                                    "<li>medium</li>"+
                                    "<li>dashed</li>"+
                                    "<li>dotted</li>"+
                                    "<li>thick</li>"+
                                    "<li>double</li>"+
                                    "<li>hair</li>"+
                                    "<li>medium-dashed</li>"+
                                    "<li>dash-dot</li>"+
                                    "<li>medium-dash-dot</li>"+
                                    "</ul>"
                    )
            })
    }, returns = {
        @ReturnTranslationDoc(
                language = LanguageDoc.PT,
                description = "O estilo de contorno de borda configurado com o tipo definido."
        ),
        @ReturnTranslationDoc(
                language = LanguageDoc.EN,
                description = "The border outline style configured with the defined type."
        )
    })
    public BorderStyle borderStyle(String value) {
        return BorderStyle.valueOf(value.toUpperCase().replace("-", "_"));
    }

    @MethodDoc(translations = {
            @MethodTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Gera o tipo de âncora.",
                    howToUse = { }),
            @MethodTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Generates the type of anchor.",
                    howToUse = { })
    }, parameters = {
            @ParameterDoc(name = "type", translations = {
                    @ParameterTranslationDoc(
                            language=LanguageDoc.PT,
                            name = "tipo",
                            description = "Nome do tipo de âncora, suporta:<br/>"+
                                    "<ul>"+
                                    "<li>move-and-resize</li>"+
                                    "<li>dont-move-do-resize</li>"+
                                    "<li>move-dont-resize</li>"+
                                    "<li>dont-move-and-resize</li>"+
                                    "</ul>"
                    ),
                    @ParameterTranslationDoc(
                            language=LanguageDoc.EN,
                            description = "Anchor type name, supports:<br/>"+
                                    "<ul>"+
                                    "<li>move-and-resize</li>"+
                                    "<li>dont-move-do-resize</li>"+
                                    "<li>move-dont-resize</li>"+
                                    "<li>dont-move-and-resize</li>"+
                                    "</ul>"
                    )
            })
    }, returns = {
        @ReturnTranslationDoc(
                language = LanguageDoc.PT,
                description = "O tipo de âncora configurado com o tipo definido."
        ),
        @ReturnTranslationDoc(
                language = LanguageDoc.EN,
                description = "The anchor type configured with the defined type."
        )
    })
    public ClientAnchor.AnchorType anchorType(String value) {
        return ClientAnchor.AnchorType.valueOf(value.toUpperCase().replace("-", "_"));
    }

    @MethodDoc(translations = {
            @MethodTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Gera o endereço de região da área das células.",
                    howToUse = { }),
            @MethodTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Generates the region address of the cell area.",
                    howToUse = { })
    }, parameters = {
            @ParameterDoc(name = "firstRow", translations = {
                    @ParameterTranslationDoc(
                            language=LanguageDoc.PT,
                            name = "primeiraLinha",
                            description = "Número da primeira linha."
                    ),
                    @ParameterTranslationDoc(
                            language=LanguageDoc.EN,
                            description = "First line number."
                    )
            }),
            @ParameterDoc(name = "lastRow", translations = {
                    @ParameterTranslationDoc(
                            language=LanguageDoc.PT,
                            name = "ultimaLinha",
                            description = "Número da última linha."
                    ),
                    @ParameterTranslationDoc(
                            language=LanguageDoc.EN,
                            description = "Last line number."
                    )
            }),
            @ParameterDoc(name = "firstCol", translations = {
                    @ParameterTranslationDoc(
                            language=LanguageDoc.PT,
                            name = "primeiraColuna",
                            description = "Número da primeira coluna."
                    ),
                    @ParameterTranslationDoc(
                            language=LanguageDoc.EN,
                            description = "First column number."
                    )
            }),
            @ParameterDoc(name = "lastCol", translations = {
                    @ParameterTranslationDoc(
                            language=LanguageDoc.PT,
                            name = "ultimaColuna",
                            description = "Número da última coluna."
                    ),
                    @ParameterTranslationDoc(
                            language=LanguageDoc.EN,
                            description = "Last column number."
                    )
            })
    }, returns = {
        @ReturnTranslationDoc(
                language = LanguageDoc.PT,
                description = "A referência do endereço da área das células."
        ),
        @ReturnTranslationDoc(
                language = LanguageDoc.EN,
                description = "The cell area address reference."
        )
    })
    public CellRangeAddress cellRangeAddress(int firstRow, int lastRow, int firstCol, int lastCol) {
        return new CellRangeAddress(firstRow, lastRow, firstCol, lastCol);
    }

    @MethodDoc(translations = {
            @MethodTranslationDoc(
                language = LanguageDoc.PT,
                description = "Realiza a mesclagem de células na região.",
                howToUse = { }),
            @MethodTranslationDoc(
                language = LanguageDoc.EN,
                description = "Performs the merging of cells in the region.",
                howToUse = { })
    }, parameters = {
            @ParameterDoc(name = "firstRow", translations = {
                @ParameterTranslationDoc(
                    language=LanguageDoc.PT,
                    name = "primeiraLinha",
                    description = "Número da primeira linha."
                ),
                @ParameterTranslationDoc(
                    language=LanguageDoc.EN,
                    description = "First line number."
                )
            }),
            @ParameterDoc(name = "lastRow", translations = {
                @ParameterTranslationDoc(
                    language=LanguageDoc.PT,
                    name = "ultimaLinha",
                    description = "Número da última linha."
                ),
                @ParameterTranslationDoc(
                    language=LanguageDoc.EN,
                    description = "Last line number."
                )
            }),
            @ParameterDoc(name = "firstCol", translations = {
                @ParameterTranslationDoc(
                    language=LanguageDoc.PT,
                    name = "primeiraColuna",
                    description = "Número da primeira coluna."
                ),
                @ParameterTranslationDoc(
                    language=LanguageDoc.EN,
                    description = "First column number."
                )
            }),
            @ParameterDoc(name = "lastCol", translations = {
                @ParameterTranslationDoc(
                    language=LanguageDoc.PT,
                    name = "ultimaColuna",
                    description = "Número da última coluna."
                ),
                @ParameterTranslationDoc(
                    language=LanguageDoc.EN,
                    description = "Last column number."
                )
            })
    }, returns = {
            @ReturnTranslationDoc(
                language = LanguageDoc.PT,
                description = "A referência da região de células mesclada."
            ),
            @ReturnTranslationDoc(
                language = LanguageDoc.EN,
                description = "The reference of the merged cell region."
            )
    })
    public int mergedRegion(int firstRow, int lastRow, int firstCol, int lastCol) {
        return this.sheet.addMergedRegion(this.cellRangeAddress(firstRow, lastRow, firstCol, lastCol));
    }

    @MethodDoc(translations = {
        @MethodTranslationDoc(
                language = LanguageDoc.PT,
                description = "Realiza a mesclagem de células na região passada em uma folha de cálculos específica.",
                howToUse = { }),
        @MethodTranslationDoc(
                language = LanguageDoc.EN,
                description = "Performs the merging of cells in the passed region in a specific worksheet.",
                howToUse = { })
        }, parameters = {
                @ParameterDoc(name = "sheet", translations = {
                        @ParameterTranslationDoc(
                                language=LanguageDoc.PT,
                                name = "folhaCalculos",
                                description = "Folha de cálculos que será mesclada as células."
                        ),
                        @ParameterTranslationDoc(
                                language=LanguageDoc.EN,
                                description = "Spreadsheet that will be merged the cells."
                        )
                }),
                @ParameterDoc(name = "firstRow", translations = {
                        @ParameterTranslationDoc(
                                language=LanguageDoc.PT,
                                name = "primeiraLinha",
                                description = "Número da primeira linha."
                        ),
                        @ParameterTranslationDoc(
                                language=LanguageDoc.EN,
                                description = "First line number."
                        )
                }),
                @ParameterDoc(name = "lastRow", translations = {
                        @ParameterTranslationDoc(
                                language=LanguageDoc.PT,
                                name = "ultimaLinha",
                                description = "Número da última linha."
                        ),
                        @ParameterTranslationDoc(
                                language=LanguageDoc.EN,
                                description = "Last line number."
                        )
                }),
                @ParameterDoc(name = "firstCol", translations = {
                        @ParameterTranslationDoc(
                                language=LanguageDoc.PT,
                                name = "primeiraColuna",
                                description = "Número da primeira coluna."
                        ),
                        @ParameterTranslationDoc(
                                language=LanguageDoc.EN,
                                description = "First column number."
                        )
                }),
                @ParameterDoc(name = "lastCol", translations = {
                        @ParameterTranslationDoc(
                                language=LanguageDoc.PT,
                                name = "ultimaColuna",
                                description = "Número da última coluna."
                        ),
                        @ParameterTranslationDoc(
                                language=LanguageDoc.EN,
                                description = "Last column number."
                        )
                })
        }, returns = {
                @ReturnTranslationDoc(
                        language = LanguageDoc.PT,
                        description = "A referência da região de células mesclada."
                ),
                @ReturnTranslationDoc(
                        language = LanguageDoc.EN,
                        description = "The reference of the merged cell region."
                )
    })
    public int mergedRegion(Sheet sheet, int firstRow, int lastRow, int firstCol, int lastCol) {
        return sheet.addMergedRegion(this.cellRangeAddress(firstRow, lastRow, firstCol, lastCol));
    }

    @MethodDoc(translations = {
            @MethodTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Gera o código da cor baseado em nomes pré definidos.",
                    howToUse = { }),
            @MethodTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Generates the color code based on predefined names.",
                    howToUse = { })
    }, parameters = {
            @ParameterDoc(name = "color", translations = {
                    @ParameterTranslationDoc(
                            language=LanguageDoc.PT,
                            name = "nome",
                            description = "Nome da cor, suporta:<br/>"+
                                    "<ul>"+
                                    "<li>black</li>"+
                                    "<li>brown</li>"+
                                    "<li>olive_green</li>"+
                                    "<li>dark_green</li>"+
                                    "<li>dark_teal</li>"+
                                    "<li>dark_blue</li>"+
                                    "<li>indigo</li>"+
                                    "<li>grey_80_percent</li>"+
                                    "<li>orange</li>"+
                                    "<li>dark_yellow</li>"+
                                    "<li>green</li>"+
                                    "<li>teal</li>"+
                                    "<li>blue</li>"+
                                    "<li>blue_grey</li>"+
                                    "<li>grey_50_percent</li>"+
                                    "<li>red</li>"+
                                    "<li>light_orange</li>"+
                                    "<li>lime</li>"+
                                    "<li>sea_green</li>"+
                                    "<li>aqua</li>"+
                                    "<li>light_blue</li>"+
                                    "<li>violet</li>"+
                                    "<li>grey_40_percent</li>"+
                                    "<li>pink</li>"+
                                    "<li>gold</li>"+
                                    "<li>yellow</li>"+
                                    "<li>bright_green</li>"+
                                    "<li>turquoise</li>"+
                                    "<li>dark_red</li>"+
                                    "<li>sky_blue</li>"+
                                    "<li>plum</li>"+
                                    "<li>grey_25_percent</li>"+
                                    "<li>rose</li>"+
                                    "<li>light_yellow</li>"+
                                    "<li>light_green</li>"+
                                    "<li>light_turquoise</li>"+
                                    "<li>pale_blue</li>"+
                                    "<li>lavender</li>"+
                                    "<li>white</li>"+
                                    "<li>cornflower_blue</li>"+
                                    "<li>lemon_chiffon</li>"+
                                    "<li>maroon</li>"+
                                    "<li>orchid</li>"+
                                    "<li>coral</li>"+
                                    "<li>royal_blue</li>"+
                                    "<li>light_cornflower_blue</li>"+
                                    "<li>tan</li>"+
                                    "<li>automatic</li>"+
                                    "</ul>"
                    ),
                    @ParameterTranslationDoc(
                            language=LanguageDoc.EN,
                            description = "Color name, supports:<br/>"+
                                    "<ul>"+
                                    "<li>black</li>"+
                                    "<li>brown</li>"+
                                    "<li>olive_green</li>"+
                                    "<li>dark_green</li>"+
                                    "<li>dark_teal</li>"+
                                    "<li>dark_blue</li>"+
                                    "<li>indigo</li>"+
                                    "<li>grey_80_percent</li>"+
                                    "<li>orange</li>"+
                                    "<li>dark_yellow</li>"+
                                    "<li>green</li>"+
                                    "<li>teal</li>"+
                                    "<li>blue</li>"+
                                    "<li>blue_grey</li>"+
                                    "<li>grey_50_percent</li>"+
                                    "<li>red</li>"+
                                    "<li>light_orange</li>"+
                                    "<li>lime</li>"+
                                    "<li>sea_green</li>"+
                                    "<li>aqua</li>"+
                                    "<li>light_blue</li>"+
                                    "<li>violet</li>"+
                                    "<li>grey_40_percent</li>"+
                                    "<li>pink</li>"+
                                    "<li>gold</li>"+
                                    "<li>yellow</li>"+
                                    "<li>bright_green</li>"+
                                    "<li>turquoise</li>"+
                                    "<li>dark_red</li>"+
                                    "<li>sky_blue</li>"+
                                    "<li>plum</li>"+
                                    "<li>grey_25_percent</li>"+
                                    "<li>rose</li>"+
                                    "<li>light_yellow</li>"+
                                    "<li>light_green</li>"+
                                    "<li>light_turquoise</li>"+
                                    "<li>pale_blue</li>"+
                                    "<li>lavender</li>"+
                                    "<li>white</li>"+
                                    "<li>cornflower_blue</li>"+
                                    "<li>lemon_chiffon</li>"+
                                    "<li>maroon</li>"+
                                    "<li>orchid</li>"+
                                    "<li>coral</li>"+
                                    "<li>royal_blue</li>"+
                                    "<li>light_cornflower_blue</li>"+
                                    "<li>tan</li>"+
                                    "<li>automatic</li>"+
                                    "</ul>"
                    )
            })
    }, returns = {
        @ReturnTranslationDoc(
                language = LanguageDoc.PT,
                description = "A referência da cor."
        ),
        @ReturnTranslationDoc(
                language = LanguageDoc.EN,
                description = "The color reference."
        )
    })
    public Object color(String color) {
        switch (workbook) {
            case null -> {
                return null;
            }
            case XSSFWorkbook sheets -> {
                if (color.startsWith("#")) {
                    return new XSSFColor(java.awt.Color.decode(color), null);
                }
                return IndexedColors.valueOf(color.toUpperCase().replace("-", "_")).getIndex();
            }
            case HSSFWorkbook sheets -> {
                if (color.startsWith("#")) {
                    java.awt.Color c = java.awt.Color.decode(color);
                    HSSFPalette palette = sheets.getCustomPalette();
                    return palette.findSimilarColor(c.getRed(), c.getGreen(), c.getBlue());
                }
                return HSSFColor.HSSFColorPredefined.valueOf(color.toUpperCase().replace("-", "_")).getIndex();
            }
            default -> {
            }
        }
        return null;
    }

    @MethodDoc(dependency = "create", translations = {
            @MethodTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Realiza o envio de dados do ficheiro final para o cliente realizar o download.",
                    howToUse = { }),
            @MethodTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Performs the sending of data from the final file to the client to download it.",
                    howToUse = { })
    }, parameters = {
            @ParameterDoc(name = "fileName", translations = {
                    @ParameterTranslationDoc(
                            language=LanguageDoc.PT,
                            name = "nomeFicheiro",
                            description = "Nome do ficheiro."
                    ),
                    @ParameterTranslationDoc(
                            language=LanguageDoc.EN,
                            description = "Name of the file."
                    )
            })
    }, returns = {})
    public void output(String fileName) throws IOException {
        getProteu().setResponseHeaderNoCache();
        getProteu().setResponseHeaderDownloadFile(fileName);
        workbook.write(getProteu().getOutput());
    }

    @MethodDoc(dependency = "create", translations = {
            @MethodTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Realiza a escrita dos dados do ficheiro final para o storage interno da aplicação.",
                    howToUse = { }),
            @MethodTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Writes the data from the final file to the internal storage of the application.",
                    howToUse = { })
    }, parameters = {
            @ParameterDoc(name = "storage", translations = {
                    @ParameterTranslationDoc(
                            language=LanguageDoc.PT,
                            description = "Referência do storage onde o ficheiro será guardado."
                    ),
                    @ParameterTranslationDoc(
                            language=LanguageDoc.EN,
                            description = "Storage reference where the file will be saved."
                    )
            })
    }, returns = {})
    public void save(Storage storage) throws IOException {
        FileOutputStream fos = new FileOutputStream(FileSystemPath.absoluteFromStorage(getProteu(), storage));
        try {
            workbook.write(fos);
        } finally {
            fos.close();
        }
    }

    @MethodDoc(dependency = "create", translations = {
            @MethodTranslationDoc(
                language = LanguageDoc.PT,
                description = "Realiza a escrita dos dados do ficheiro final.",
                howToUse = { }),
            @MethodTranslationDoc(
                language = LanguageDoc.EN,
                description = "Writes the data to the final file.",
                howToUse = { })
        }, parameters = {
            @ParameterDoc(name = "file", translations = {
                @ParameterTranslationDoc(
                    language=LanguageDoc.PT,
                    description = "Ficheiro que será guardado."
                ),
                @ParameterTranslationDoc(
                    language=LanguageDoc.EN,
                    description = "File that will be saved."
                )
            })
    }, returns = {})
    public void save(File file) throws IOException {
        FileOutputStream fos = new FileOutputStream(file.fullPath());
        try {
            workbook.write(fos);
        } finally {
            fos.close();
        }
    }

    @MethodDoc(dependency = "create", translations = {
            @MethodTranslationDoc(
                language = LanguageDoc.PT,
                description = "Realiza a escrita dos dados do ficheiro final para o output.",
                howToUse = { }),
            @MethodTranslationDoc(
                language = LanguageDoc.EN,
                description = "Writes the data from the final file to the output.",
                howToUse = { })
        }, parameters = {
            @ParameterDoc(name = "output", translations = {
                @ParameterTranslationDoc(
                    language=LanguageDoc.PT,
                    description = "Output onde o ficheiro será guardado."
                ),
                @ParameterTranslationDoc(
                    language=LanguageDoc.EN,
                    description = "Output where the file will be saved."
                )
            })
    }, returns = {})
    public void save(OutputStream output) throws IOException {
        workbook.write(output);
    }

    @MethodDoc(dependency = "create", translations = {
            @MethodTranslationDoc(
                language = LanguageDoc.PT,
                description = "Realiza a escrita dos dados do ficheiro final para o output.",
                howToUse = { }),
            @MethodTranslationDoc(
                language = LanguageDoc.EN,
                description = "Writes the data from the final file to the output.",
                howToUse = { })
        }, parameters = {
            @ParameterDoc(name = "output", translations = {
                @ParameterTranslationDoc(
                    language=LanguageDoc.PT,
                    description = "Output onde o ficheiro será guardado."
                ),
                @ParameterTranslationDoc(
                    language=LanguageDoc.EN,
                    description = "Output where the file will be saved."
                )
        })
    }, returns = {})
    public void save(java.io.OutputStream output) throws IOException {
        workbook.write(output);
    }

    @MethodDoc(dependency = "create", translations = {
            @MethodTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Insere a imagem na célula específicada.",
                    howToUse = { }),
            @MethodTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Insert the image into the specified cell.",
                    howToUse = { })
    }, parameters = {
            @ParameterDoc(name = "storage", translations = {
                    @ParameterTranslationDoc(
                            language=LanguageDoc.PT,
                            description = "Referência do storage associado a um ficheiro de imagem."
                    ),
                    @ParameterTranslationDoc(
                            language=LanguageDoc.EN,
                            description = "Storage reference associated with an image file."
                    )
            }),
            @ParameterDoc(name = "row", translations = {
                    @ParameterTranslationDoc(
                            language=LanguageDoc.PT,
                            name = "linha",
                            description = "Número da linha."
                    ),
                    @ParameterTranslationDoc(
                            language=LanguageDoc.EN,
                            name = "linha",
                            description = "Line number."
                    )
            }),
            @ParameterDoc(name = "column", translations = {
                    @ParameterTranslationDoc(
                            language=LanguageDoc.PT,
                            name = "coluna",
                            description = "Número da coluna."
                    ),
                    @ParameterTranslationDoc(
                            language=LanguageDoc.EN,
                            name = "coluna",
                            description = "Column number."
                    )
            })
    }, returns = {
        @ReturnTranslationDoc(
                language = LanguageDoc.PT,
                description = "O objeto de referência da imagem inserida."
        ),
        @ReturnTranslationDoc(
                language = LanguageDoc.EN,
                description = "The reference object of the inserted image."
        )
    })
    public Picture insertPicture(Storage storage, int row, int column) throws IOException {
        CreationHelper helper = workbook.getCreationHelper();
        ClientAnchor anchor = helper.createClientAnchor();
        anchor.setAnchorType(ClientAnchor.AnchorType.MOVE_AND_RESIZE);
        anchor.setCol1(column);
        anchor.setRow1(row);
        return insertPicture(storage, anchor);
    }

    @MethodDoc(dependency = "create", translations = {
            @MethodTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Insere uma imagem associada à âncora.",
                    howToUse = { }),
            @MethodTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Inserts an image associated with the anchor.",
                    howToUse = { })
    }, parameters = {
            @ParameterDoc(name = "storage", translations = {
                    @ParameterTranslationDoc(
                            language=LanguageDoc.PT,
                            description = "Referência do storage associado a um ficheiro de imagem."
                    ),
                    @ParameterTranslationDoc(
                            language=LanguageDoc.EN,
                            description = "Storage reference associated with an image file."
                    )
            }),
            @ParameterDoc(name = "anchor", translations = {
                    @ParameterTranslationDoc(
                            language=LanguageDoc.PT,
                            name = "ancora",
                            description = "Âncora para associar a imagem."
                    ),
                    @ParameterTranslationDoc(
                            language=LanguageDoc.EN,
                            description = "Anchor to associate the image."
                    )
            })
    }, returns = {
        @ReturnTranslationDoc(
                language = LanguageDoc.PT,
                description = "O objeto de referência da imagem inserida."
        ),
        @ReturnTranslationDoc(
                language = LanguageDoc.EN,
                description = "The reference object of the inserted image."
        )
    })
    public Picture insertPicture(Storage storage, ClientAnchor anchor) throws IOException {
        return insertPicture(sheet, storage, anchor);
    }

    @MethodDoc(translations = {
            @MethodTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Insere uma imagem associada à âncora em uma folha de cálculos específica.",
                    howToUse = { }),
            @MethodTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Inserts an image associated with the anchor in a specific spreadsheet.",
                    howToUse = { })
    }, parameters = {
            @ParameterDoc(name = "sheet", translations = {
                    @ParameterTranslationDoc(
                            language=LanguageDoc.PT,
                            name = "folhaCalculos",
                            description = "Folha de cálculos que será utilizada para inserir a imagem."
                    ),
                    @ParameterTranslationDoc(
                            language=LanguageDoc.EN,
                            description = "Spreadsheet that will be used to insert the image."
                    )
            }),
            @ParameterDoc(name = "storage", translations = {
                    @ParameterTranslationDoc(
                            language=LanguageDoc.PT,
                            description = "Referência do storage associado a um ficheiro de imagem."
                    ),
                    @ParameterTranslationDoc(
                            language=LanguageDoc.EN,
                            description = "Storage reference associated with an image file."
                    )
            }),
            @ParameterDoc(name = "anchor", translations = {
                    @ParameterTranslationDoc(
                            language=LanguageDoc.PT,
                            name = "ancora",
                            description = "Âncora para associar a imagem."
                    ),
                    @ParameterTranslationDoc(
                            language=LanguageDoc.EN,
                            description = "Anchor to associate the image."
                    )
            })
    }, returns = {
        @ReturnTranslationDoc(
                language = LanguageDoc.PT,
                description = "O objeto de referência da imagem inserida."
        ),
        @ReturnTranslationDoc(
                language = LanguageDoc.EN,
                description = "The reference object of the inserted image."
        )
    })
    public Picture insertPicture(Sheet sheet, Storage storage, ClientAnchor anchor) throws IOException {
        Drawing drawing = sheet.createDrawingPatriarch();
        
        int pictureIndex = workbook.addPicture(
                InputStream.readAllBytesFromFile(
                        FileSystemPath.absoluteFromStorage(getProteu(), storage)
                ), storage.isExtension("png") ? Workbook.PICTURE_TYPE_PNG : Workbook.PICTURE_TYPE_JPEG
        );

        Picture pict = drawing.createPicture(anchor, pictureIndex);
        return pict;
    }

    @MethodDoc(dependency = "create", translations = {
        @MethodTranslationDoc(
                language = LanguageDoc.PT,
                description = "Insere a imagem na célula específicada.",
                howToUse = { }),
        @MethodTranslationDoc(
                language = LanguageDoc.EN,
                description = "Insert the image into the specified cell.",
                howToUse = { })
        }, parameters = {
                @ParameterDoc(name = "file", translations = {
                        @ParameterTranslationDoc(
                                language=LanguageDoc.PT,
                                description = "Ficheiro de imagem."
                        ),
                        @ParameterTranslationDoc(
                                language=LanguageDoc.EN,
                                description = "Image file."
                        )
                }),
                @ParameterDoc(name = "row", translations = {
                        @ParameterTranslationDoc(
                                language=LanguageDoc.PT,
                                name = "linha",
                                description = "Número da linha."
                        ),
                        @ParameterTranslationDoc(
                                language=LanguageDoc.EN,
                                name = "linha",
                                description = "Line number."
                        )
                }),
                @ParameterDoc(name = "column", translations = {
                        @ParameterTranslationDoc(
                                language=LanguageDoc.PT,
                                name = "coluna",
                                description = "Número da coluna."
                        ),
                        @ParameterTranslationDoc(
                                language=LanguageDoc.EN,
                                name = "coluna",
                                description = "Column number."
                        )
                })
        }, returns = {
        @ReturnTranslationDoc(
                language = LanguageDoc.PT,
                description = "O objeto de referência da imagem inserida."
        ),
        @ReturnTranslationDoc(
                language = LanguageDoc.EN,
                description = "The reference object of the inserted image."
        )
    })
    public Picture insertPicture(File file, int row, int column) throws IOException {
        CreationHelper helper = workbook.getCreationHelper();
        ClientAnchor anchor = helper.createClientAnchor();
        anchor.setAnchorType(ClientAnchor.AnchorType.MOVE_AND_RESIZE);
        anchor.setCol1(column);
        anchor.setRow1(row);
        return insertPicture(file, anchor);
    }

    @MethodDoc(dependency = "create", translations = {
        @MethodTranslationDoc(
                language = LanguageDoc.PT,
                description = "Insere uma imagem associada à âncora.",
                howToUse = { }),
        @MethodTranslationDoc(
                language = LanguageDoc.EN,
                description = "Inserts an image associated with the anchor.",
                howToUse = { })
        }, parameters = {
                @ParameterDoc(name = "file", translations = {
                        @ParameterTranslationDoc(
                                language=LanguageDoc.PT,
                                description = "Ficheiro de imagem."
                        ),
                        @ParameterTranslationDoc(
                                language=LanguageDoc.EN,
                                description = "Image file."
                        )
                }),
                @ParameterDoc(name = "anchor", translations = {
                        @ParameterTranslationDoc(
                                language=LanguageDoc.PT,
                                name = "ancora",
                                description = "Âncora para associar a imagem."
                        ),
                        @ParameterTranslationDoc(
                                language=LanguageDoc.EN,
                                description = "Anchor to associate the image."
                        )
                })
        }, returns = {
        @ReturnTranslationDoc(
                language = LanguageDoc.PT,
                description = "O objeto de referência da imagem inserida."
        ),
        @ReturnTranslationDoc(
                language = LanguageDoc.EN,
                description = "The reference object of the inserted image."
        )
    })
    public Picture insertPicture(File file, ClientAnchor anchor) throws IOException {
        return insertPicture(sheet, file, anchor);
    }
    
    @MethodDoc(translations = {
        @MethodTranslationDoc(
                language = LanguageDoc.PT,
                description = "Insere uma imagem associada à âncora em uma folha de cálculos específica.",
                howToUse = { }),
        @MethodTranslationDoc(
                language = LanguageDoc.EN,
                description = "Inserts an image associated with the anchor in a specific spreadsheet.",
                howToUse = { })
        }, parameters = {
                @ParameterDoc(name = "sheet", translations = {
                        @ParameterTranslationDoc(
                                language=LanguageDoc.PT,
                                name = "folhaCalculos",
                                description = "Folha de cálculos que será utilizada para inserir a imagem."
                        ),
                        @ParameterTranslationDoc(
                                language=LanguageDoc.EN,
                                description = "Spreadsheet that will be used to insert the image."
                        )
                }),
                @ParameterDoc(name = "file", translations = {
                        @ParameterTranslationDoc(
                                language=LanguageDoc.PT,
                                description = "Ficheiro de imagem."
                        ),
                        @ParameterTranslationDoc(
                                language=LanguageDoc.EN,
                                description = "Image file."
                        )
                }),
                @ParameterDoc(name = "anchor", translations = {
                        @ParameterTranslationDoc(
                                language=LanguageDoc.PT,
                                name = "ancora",
                                description = "Âncora para associar a imagem."
                        ),
                        @ParameterTranslationDoc(
                                language=LanguageDoc.EN,
                                description = "Anchor to associate the image."
                        )
                })
        }, returns = {
        @ReturnTranslationDoc(
                language = LanguageDoc.PT,
                description = "O objeto de referência da imagem inserida."
        ),
        @ReturnTranslationDoc(
                language = LanguageDoc.EN,
                description = "The reference object of the inserted image."
        )
    })
    public Picture insertPicture(Sheet sheet, File file, ClientAnchor anchor) throws IOException {
        Drawing drawing = sheet.createDrawingPatriarch();
        
        int pictureIndex = workbook.addPicture(
                file.bytes(), file.isExtension("png") ? Workbook.PICTURE_TYPE_PNG : Workbook.PICTURE_TYPE_JPEG
        );

        Picture pict = drawing.createPicture(anchor, pictureIndex);
        return pict;
    }

    @MethodDoc(translations = {
            @MethodTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Obtém os tipos de unidades pré definidos no Excel, útil para realizar a conversão entre pixeis e pontos.",
                    howToUse = { }),
            @MethodTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "It obtains the types of units predefined in Excel, useful to perform the conversion between pixels and points.",
                    howToUse = { })
    }, parameters = {}, returns = {
        @ReturnTranslationDoc(
                language = LanguageDoc.PT,
                description = "A classe do Apache POI que ajuda a manipular as unidades do Excel."
        ),
        @ReturnTranslationDoc(
                language = LanguageDoc.EN,
                description = "The Apache POI class that helps you manipulate Excel units."
        )
    })
    public Class<org.apache.poi.util.Units> units() {
        return org.apache.poi.util.Units.class;
    }

    @MethodDoc(translations = {
            @MethodTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Obtém o objeto de posicionamento.",
                    howToUse = { }),
            @MethodTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Gets the positioning object.",
                    howToUse = { })
    }, parameters = {
            @ParameterDoc(name = "rowIndex", translations = {
                    @ParameterTranslationDoc(
                            language=LanguageDoc.PT,
                            name = "linha",
                            description = "Número da linha."
                    ),
                    @ParameterTranslationDoc(
                            language=LanguageDoc.EN,
                            description = "Line number."
                    )
            }),
            @ParameterDoc(name = "colIndex", translations = {
                    @ParameterTranslationDoc(
                            language=LanguageDoc.PT,
                            name = "coluna",
                            description = "Número da coluna."
                    ),
                    @ParameterTranslationDoc(
                            language=LanguageDoc.EN,
                            description = "Column number."
                    )
            })
    }, returns = {
        @ReturnTranslationDoc(
                language = LanguageDoc.PT,
                description = "Referência da posição com base nas coordenadas passadas."
        ),
        @ReturnTranslationDoc(
                language = LanguageDoc.EN,
                description = "Position reference based on passed coordinates."
        )
    })
    public XLSPosition position(int rowIndex, int colIndex) {
        return new XLSPosition(rowIndex, colIndex);
    }

    public Values read(Storage storage) throws IOException {
        return read(storage, "", -1, false);
    }

    public Values read(Storage storage, String password) throws IOException {
        return read(storage, password, -1, false);
    }

    @MethodDoc(translations = {
            @MethodTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Realiza a leitura de um ficheiro Excel, obtém todos os dados.",
                    howToUse = { }),
            @MethodTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Reads an Excel file, obtains all data.",
                    howToUse = { })
    }, parameters = {
            @ParameterDoc(name = "input", translations = {
                    @ParameterTranslationDoc(
                            language=LanguageDoc.PT,
                            description = "Referência do ficheiro Excel."
                    ),
                    @ParameterTranslationDoc(
                            language=LanguageDoc.EN,
                            description = "Excel file reference."
                    )
            }),
            @ParameterDoc(name = "hiddenSheets", translations = {
                    @ParameterTranslationDoc(
                            language=LanguageDoc.PT,
                            name = "escondidas",
                            description = "Processa também folhas de cálculos escondidas."
                    ),
                    @ParameterTranslationDoc(
                            language=LanguageDoc.EN,
                            description = "It also processes hidden spreadsheets."
                    )
            })
    }, returns = {
        @ReturnTranslationDoc(
                language = LanguageDoc.PT,
                description = "Estrutura com todos os dados obtidos através da leitura e processamento do ficheiros Excel."
        ),
        @ReturnTranslationDoc(
                language = LanguageDoc.EN,
                description = "Structure with all the data obtained by reading and processing the Excel files."
        )
    })
    public Values read(Storage storage, boolean hiddenSheets) throws IOException {
        return read(storage, "", -1, hiddenSheets);
    }

    public Values read(Storage storage, String password, boolean hiddenSheets) throws IOException {
        return read(storage, password, -1, hiddenSheets);
    }

    @MethodDoc(translations = {
            @MethodTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Realiza a leitura de um ficheiro Excel, obtém todos os dados.",
                    howToUse = { }),
            @MethodTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Reads an Excel file, obtains all data.",
                    howToUse = { })
    }, parameters = {
            @ParameterDoc(name = "input", translations = {
                    @ParameterTranslationDoc(
                            language=LanguageDoc.PT,
                            description = "Referência do ficheiro Excel."
                    ),
                    @ParameterTranslationDoc(
                            language=LanguageDoc.EN,
                            description = "Excel file reference."
                    )
            }),
            @ParameterDoc(name = "sheetNumber", translations = {
                    @ParameterTranslationDoc(
                            language=LanguageDoc.PT,
                            name = "numero",
                            description = "Número da folha de cálculos."
                    ),
                    @ParameterTranslationDoc(
                            language=LanguageDoc.EN,
                            description = "Spreadsheet number."
                    )
            })
    }, returns = {
        @ReturnTranslationDoc(
                language = LanguageDoc.PT,
                description = "Estrutura com todos os dados obtidos através da leitura e processamento do ficheiros Excel."
        ),
        @ReturnTranslationDoc(
                language = LanguageDoc.EN,
                description = "Structure with all the data obtained by reading and processing the Excel files."
        )
    })
    public Values read(Storage storage, int sheetNumber) throws IOException {
        return read(storage, "", sheetNumber, true);
    }

    public Values read(Storage storage, String password, int sheetNumber) throws IOException {
        return read(storage, password, sheetNumber, true);
    }

    @MethodDoc(translations = {
            @MethodTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Realiza a leitura de um ficheiro Excel, obtém todos os dados.",
                    howToUse = { }),
            @MethodTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Reads an Excel file, obtains all data.",
                    howToUse = { })
    }, parameters = {
            @ParameterDoc(name = "input", translations = {
                    @ParameterTranslationDoc(
                            language=LanguageDoc.PT,
                            description = "Referência do ficheiro Excel."
                    ),
                    @ParameterTranslationDoc(
                            language=LanguageDoc.EN,
                            description = "Excel file reference."
                    )
            }),
            @ParameterDoc(name = "sheetNumber", translations = {
                    @ParameterTranslationDoc(
                            language=LanguageDoc.PT,
                            name = "numero",
                            description = "Número da folha de cálculos."
                    ),
                    @ParameterTranslationDoc(
                            language=LanguageDoc.EN,
                            description = "Spreadsheet number."
                    )
            }),
            @ParameterDoc(name = "hiddenSheets", translations = {
                    @ParameterTranslationDoc(
                            language=LanguageDoc.PT,
                            name = "escondidas",
                            description = "Processa também folhas de cálculos escondidas."
                    ),
                    @ParameterTranslationDoc(
                            language=LanguageDoc.EN,
                            description = "It also processes hidden spreadsheets."
                    )
            })
    }, returns = {
        @ReturnTranslationDoc(
                language = LanguageDoc.PT,
                description = "Estrutura com todos os dados obtidos através da leitura e processamento do ficheiros Excel."
        ),
        @ReturnTranslationDoc(
                language = LanguageDoc.EN,
                description = "Structure with all the data obtained by reading and processing the Excel files."
        )
    })
    public Values read(Storage storage, int sheetNumber, boolean hiddenSheets) throws IOException {
        return read(storage, "", sheetNumber, hiddenSheets);
    }
    public Values read(Storage storage, String password, int sheetNumber, boolean hiddenSheets) throws IOException {
        try (FileInputStream in = new FileInputStream(FileSystemPath.absoluteFromStorage(getProteu(), storage))) {
            return read(in, password, sheetNumber, hiddenSheets);
        }
    }
    
    public Values read(File file) throws IOException {
        return read(file, "", -1, false);
    }

    public Values read(File file, String password) throws IOException {
        return read(file, password, -1, false);
    }
    
    public Values read(File file, int sheetNumber) throws IOException {
        return read(file, "", sheetNumber, false);
    }

    public Values read(File file, String password, int sheetNumber) throws IOException {
        return read(file, password, sheetNumber, false);
    }
    
    public Values read(File file, boolean hiddenSheets) throws IOException {
        return read(file, "", -1, hiddenSheets);
    }

    public Values read(File file, String password, boolean hiddenSheets) throws IOException {
        return read(file, password, -1, hiddenSheets);
    }
    
    public Values read(File file, int sheetNumber, boolean hiddenSheets) throws IOException {
        return read(file, "", sheetNumber, hiddenSheets);
    }

    public Values read(File file, String password, int sheetNumber, boolean hiddenSheets) throws IOException {
        try (java.io.InputStream in = file.inputStream()) {
            return read(in, password, sheetNumber, hiddenSheets);
        }
    }
    
    public Values read(java.io.InputStream in) throws IOException {
        return read(in, "", -1, false);
    }

    public Values read(java.io.InputStream in, String password) throws IOException {
        return read(in, password, -1, false);
    }
    
    public Values read(java.io.InputStream in, int sheetNumber) throws IOException {
        return read(in, "", sheetNumber, false);
    }

    public Values read(java.io.InputStream in, String password, int sheetNumber) throws IOException {
        return read(in, password, sheetNumber, false);
    }
    
    public Values read(java.io.InputStream in, boolean hiddenSheets) throws IOException {
        return read(in, "", -1, hiddenSheets);
    }

    public Values read(java.io.InputStream in, String password, boolean hiddenSheets) throws IOException {
        return read(in, password, -1, hiddenSheets);
    }
    
    public Values read(java.io.InputStream in, String password, int sheetNumber, boolean hiddenSheets) throws IOException {
        Workbook workbook = null;
        if (password == null || password.isEmpty()) {
            workbook = WorkbookFactory.create(in);
        } else {
            workbook = WorkbookFactory.create(in, password);
        }
        Values sheets = new Values();
        for (int i = 0; i < workbook.getNumberOfSheets(); i++) {
            if (sheetNumber > -1 && sheetNumber != i) {
                continue;
            }
            if (!hiddenSheets && (workbook.isSheetHidden(i) || workbook.isSheetVeryHidden(i))) {
                continue;
            }
            Sheet datatypeSheet = workbook.getSheetAt(i);
            Values sheet = new Values();
            sheet.set("name", datatypeSheet.getSheetName());
            sheet.set("index", i);
            sheet.set("hidden", workbook.isSheetHidden(i));
            sheet.set("veryHidden", workbook.isSheetVeryHidden(i));
            Iterator<Row> iterator = datatypeSheet.iterator();
            Values rows = new Values();
            while (iterator.hasNext()) {
                Row currentRow = iterator.next();
                Iterator<Cell> cellIterator = currentRow.iterator();
                Values row = new Values().forceMap();
                Values columns = new Values().forceList();
                row.set("columns", columns);
                while (cellIterator.hasNext()) {
                    Cell currentCell = cellIterator.next();
                    row.set("row", currentCell.getRowIndex());
                    Values cell = getCellData(currentCell);
                    if (!cell.isEmpty()) {
                    	columns.add(cell);
                    }
                }
                if (!columns.isEmpty()) {
                	columns.sort((a, b) -> {
                        Values colA = (Values)a;
                        Values colB = (Values)b;
                        return colA.getInt("column") - colB.getInt("column");
                    });
                    rows.add(row);
                }
            }
            rows.sort((a, b) -> {
                Values rowA = (Values)a;
                Values rowB = (Values)b;
                return rowA.getInt("row") - rowB.getInt("row");
            });
            sheet.set("rows", rows);
            sheets.add(sheet);
        }
        return new Values().set("sheets", sheets);
    }
    
    @LibraryDoc(translations = {
        @LibraryTranslationDoc(
                language = LanguageDoc.PT,
                title = "XLSPosition",
                introduction = "Contém a referência da posição de uma célula na folha de cálculos, com indicação da linha e coluna da posição da célula.",
                howToUse = {}
        ),
        @LibraryTranslationDoc(
                language = LanguageDoc.EN,
                title = "XLSPosition",
                introduction = "Contains the reference of the position of a cell in the spreadsheet, with indication of the row and column of the position of the cell.",
                howToUse = {}
        )
    })
    public class XLSPosition {
        public int row = 0;
        public int col = 0;

        public XLSPosition(int row, int col) {
            this.row = row;
            this.col = col;
        }

        @MethodDoc(translations = {
            @MethodTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "O número do índice da linha relacionado com a posição.",
                    howToUse = { }),
            @MethodTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "The index number of the row related to the position.",
                    howToUse = { })
        }, parameters = { }, returns = {
            @ReturnTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "O índice da linha."
            ),
            @ReturnTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "The index of the row."
            )
        })
        public int row() {
            return row;
        }
        
        public int getRow() {
            return row;
        }

        @MethodDoc(translations = {
            @MethodTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "O número do índice da coluna relacionado com a posição.",
                    howToUse = { }),
            @MethodTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "The index number of the column related to the position.",
                    howToUse = { })
        }, parameters = { }, returns = {
            @ReturnTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "O índice da coluna."
            ),
            @ReturnTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "The index of the column."
            )
        })
        public int col() {
            return col;
        }

        public int getCol() {
            return col;
        }
    }
}
