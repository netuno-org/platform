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
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.ss.util.WorkbookUtil;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.netuno.library.doc.*;
import org.netuno.proteu.Proteu;
import org.netuno.psamata.Values;
import org.netuno.psamata.io.InputStream;
import org.netuno.psamata.io.File;
import org.netuno.tritao.config.Hili;
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
                introduction = "Criar e ler ficheiros _Excel_, suporta para ficheiros _XLS_ como ficheiros _XLSX_.\n"+
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
                introduction = "Create and read _Excel_ files, support for _XLS_ files as _XLSX_ files.\n"+
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

    public HSSFWorkbook workbook = null;

    public HSSFSheet sheet = null;

    public XLS(Proteu proteu, Hili hili) {
        super(proteu, hili);
    }

    public XLS(Proteu proteu, Hili hili, HSSFWorkbook workbook, HSSFSheet activeSheet) {
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
        HSSFWorkbook workbook = new HSSFWorkbook();
        return new XLS(getProteu(), getHili(), workbook, workbook.createSheet());
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
    public HSSFWorkbook workbook() {
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
    public HSSFSheet sheet() {
        return sheet;
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
    public HSSFSheet getSheet(String name) {
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
    public HSSFSheet getSheet(int index) {
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
    public HSSFSheet createSheet() {
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
    public HSSFSheet createSheet(String name) {
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
                description = "Folha de cálculos ativa."
        ),
        @ReturnTranslationDoc(
                language = LanguageDoc.EN,
                description = "Active spreadsheet."
        )
    })
    public HSSFSheet activeSheet(int index) {
        workbook.setActiveSheet(index);
        this.sheet = workbook.getSheetAt(index);
        return this.sheet;
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
    public HSSFRow row(int rowIndex) {
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
    public HSSFRow row(HSSFSheet sheet, int rowIndex) {
        HSSFRow row = sheet.getRow(rowIndex);
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
    public HSSFCell cell(int rowIndex, int colIndex) {
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
    public HSSFCell cell(HSSFSheet sheet, int rowIndex, int colIndex) {
        HSSFRow row = sheet.getRow(rowIndex);
        if (row == null) {
            row = sheet.createRow(rowIndex);
        }
        HSSFCell cell = row.getCell(colIndex);
        if (cell == null) {
            cell = row.createCell(colIndex);
        }
        return cell;
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
        HSSFSheet sheet = this.sheet;
        if (conf.hasKey("sheet")) {
            sheet = (HSSFSheet)config.get("sheet");
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
    public XLSPosition addDataTable(HSSFSheet sheet, int rowIndex, int colIndex, List data, boolean vertical) {
        return addDataTable(sheet, rowIndex, colIndex, new Values(data), vertical);
    }
    
    public XLSPosition addDataTable(HSSFSheet sheet, int rowIndex, int colIndex, Values data, boolean vertical) {
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
                        HSSFCell cell = cell(sheet, rowCount, colCount);
                        add(cell, values);
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
                        HSSFCell cell = cell(sheet, rowCount, colCount);
                        add(cell, values);
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
    public XLS add(int row, int col, Map data) {
        return add(cell(row, col), data);
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
    public XLS add(HSSFCell cell, Map data) {
        return add(cell, new Values(data));
    }
    
    public XLS add(HSSFCell cell, Values values) {
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
            } else if (val instanceof Date) {
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
            cell.setCellStyle((HSSFCellStyle) values.get("style"));
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
                            description = "Nome do tipo de alinhamento horizontal, suporta:<br>"+
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
                            description = "Horizontal alignment type name, supports:<br>"+
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
                            description = "Nome do tipo de alinhamento vertical, suporta:<br>"+
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
                            description = "Vertical alignment type name, supports:<br>"+
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
                            description = "Tipo do padrão de preenchimento, suporta:<br>"+
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
                            description = "Fill pattern type, supports:<br>"+
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
                            description = "Nome do tipo de estilo de bordas, suporta:<br>"+
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
                            description = "Name of the border style type, supports:<br>"+
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
                            description = "Nome do tipo de âncora, suporta:<br>"+
                                    "<ul>"+
                                    "<li>move-and-resize</li>"+
                                    "<li>dont-move-do-resize</li>"+
                                    "<li>move-dont-resize</li>"+
                                    "<li>dont-move-and-resize</li>"+
                                    "</ul>"
                    ),
                    @ParameterTranslationDoc(
                            language=LanguageDoc.EN,
                            description = "Anchor type name, supports:<br>"+
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
                            description = "Nome da cor, suporta:<br>"+
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
                            description = "Color name, supports:<br>"+
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
    public short color(String color) {
        return HSSFColor.HSSFColorPredefined.valueOf(color.toUpperCase().replace("-", "_")).getIndex();
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
    public void output(Storage storage) throws IOException {
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
        anchor.setRow2(row);
        anchor.setCol2(column + 1);
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
    public Picture insertPicture(HSSFSheet sheet, Storage storage, ClientAnchor anchor) throws IOException {
        Drawing drawing = sheet.createDrawingPatriarch();

        int pictureIndex = workbook.addPicture(
                InputStream.readAllBytesFromFile(
                        FileSystemPath.absoluteFromStorage(getProteu(), storage)
                ), Workbook.PICTURE_TYPE_PNG
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
                description = "Referência da posição da última célula com dados inserida."
        ),
        @ReturnTranslationDoc(
                language = LanguageDoc.EN,
                description = "Reference of the position of the last cell with data entered."
        )
    })
    public XLSPosition position(int rowIndex, int colIndex) {
        return new XLSPosition(rowIndex, colIndex);
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
        return read(storage, -1, hiddenSheets);
    }
    
    public Values read(Storage storage) throws IOException {
        return read(storage, -1, false);
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
        return read(storage, sheetNumber, true);
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
        try (FileInputStream in = new FileInputStream(FileSystemPath.absoluteFromStorage(getProteu(), storage))) {
            return read(in, sheetNumber, hiddenSheets);
        }
    }
    
    public Values read(File file) throws IOException {
        return read(file, -1, false);
    }
    
    public Values read(File file, int sheetNumber) throws IOException {
        return read(file, sheetNumber, false);
    }
    
    public Values read(File file, boolean hiddenSheets) throws IOException {
        return read(file, -1, hiddenSheets);
    }
    
    public Values read(File file, int sheetNumber, boolean hiddenSheets) throws IOException {
        try (java.io.InputStream in = file.inputStream()) {
            return read(in, sheetNumber, hiddenSheets);
        }
    }
    
    public Values read(InputStream in) throws IOException {
        return read(in, -1, false);
    }
    
    public Values read(InputStream in, int sheetNumber) throws IOException {
        return read(in, sheetNumber, false);
    }
    
    public Values read(InputStream in, boolean hiddenSheets) throws IOException {
        return read(in, -1, hiddenSheets);
    }
    
    public Values read(InputStream in, int sheetNumber, boolean hiddenSheets) throws IOException {
        return read(in, sheetNumber, hiddenSheets);
    }
    
    public Values read(java.io.InputStream in) throws IOException {
        return read(in, -1, false);
    }
    
    public Values read(java.io.InputStream in, int sheetNumber) throws IOException {
        return read(in, sheetNumber, false);
    }
    
    public Values read(java.io.InputStream in, boolean hiddenSheets) throws IOException {
        return read(in, -1, hiddenSheets);
    }
    
    public Values read(java.io.InputStream in, int sheetNumber, boolean hiddenSheets) throws IOException {
        Workbook workbook = new XSSFWorkbook(in);
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
                    Values cell = new Values();
                    row.set("row", currentCell.getRowIndex());
                    cell.set("column", currentCell.getColumnIndex());
                    cell.set("row", currentCell.getRowIndex());
                    cell.set("address", currentCell.getAddress().formatAsString());
                    if (currentCell.getCellType() == CellType.STRING) {
                        cell.set("type", "string");
                        cell.set("value", currentCell.getStringCellValue());
                        cell.set("richValue", currentCell.getRichStringCellValue());
                    } else if (currentCell.getCellType() == CellType.NUMERIC) {
                        cell.set("type", "numeric");
                        cell.set("value", currentCell.getNumericCellValue());
                    } else if (currentCell.getCellType() == CellType.BOOLEAN) {
                        cell.set("type", "boolean");
                        cell.set("value", currentCell.getBooleanCellValue());
                    } else if (currentCell.getCellType() == CellType.BLANK) {
                        cell.set("type", "blank");
                    } else if (currentCell.getCellType() == CellType.FORMULA) {
                        cell.set("type", "formula");
                        cell.set("formula", currentCell.getCellFormula());
                        switch (currentCell.getCachedFormulaResultType()) {
                            case NUMERIC:
                                cell.set("value", currentCell.getNumericCellValue());
                                break;
                            case STRING:
                                cell.set("value", currentCell.getStringCellValue());
                                cell.set("richValue", currentCell.getRichStringCellValue());
                                break;
                        }
                    } else if (currentCell.getCellType() == CellType.ERROR) {
                        cell.set("type", "error");
                        cell.set("value", currentCell.getErrorCellValue());
                    }
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
