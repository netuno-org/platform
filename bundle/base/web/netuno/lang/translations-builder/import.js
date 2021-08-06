var XLSX = require('xlsx');
var fs = require('fs');
var Entities = require('html-entities').AllHtmlEntities;

var htmlEntities = new Entities();

var langEN = {};
var langPT = {};
var langES = {};
var langBR = {};

// File Paths of the Properties files
var propPTPath = "../sbadmin_pt_pt.properties";
var propENPath = "../sbadmin_en_us.properties";
var propESPath = "../sbadmin_es_es.properties";
var propBRPath = "../sbadmin_pt_br.properties";


var typeJsonPT = require("./output/ConvertionTypes-pt.json");
var typeJsonES = require("./output/ConvertionTypes-es.json");
var typeJsonBR = require("./output/ConvertionTypes-br.json");


var xlsxPath = 'output/translations.xlsx';


// Regular Expressions 
var regEx = new RegExp('\&([#a-zA-Z0-9]+)\;', 'gm'); // Html
var regEx2 = new RegExp('([\\u00C0-\\u024F\\u1E00-\\u1EFF])', 'gm'); // Unicode

// Reading form data form excel
var workbook = XLSX.readFile(xlsxPath);
var sheetNames = workbook.SheetNames;
sheetNames.forEach(function(sheetName) {
    var worksheet = workbook.Sheets[sheetName];
    var currentKey = null;
    var langENIndexColumn = -1;
    var langPTIndexColumn = -1;
    var langESIndexColumn = -1;
    var langBRIndexColumn = -1;
    for (var cell in worksheet) {
        if (cell[0] === '!') continue;
        var cellPosition = XLSX.utils.decode_cell(cell);
        var cellValue = worksheet[cell].v;
        if (cellPosition.c == 0) {
            currentKey = cellValue;
        } else if (currentKey == 'KEY') {
            if (cellValue == 'EN') {
                langENIndexColumn = cellPosition.c;
            } else if (cellValue == 'PT') {
                langPTIndexColumn = cellPosition.c;
            } else if (cellValue == 'ES') {
                langESIndexColumn = cellPosition.c;
            } else if (cellValue == 'BR') {
                langBRIndexColumn = cellPosition.c;
            }
        } else if (cellPosition.c == langENIndexColumn) {
            langEN[currentKey] = cellValue;
        } else if (cellPosition.c == langPTIndexColumn) {
            langPT[currentKey] = cellValue;
        } else if (cellPosition.c == langESIndexColumn) {
            langES[currentKey] = cellValue;
        } else if (cellPosition.c == langBRIndexColumn) {
            langBR[currentKey] = cellValue;
        }
    }
});


// adding data form excel to properties files

    let wstreamPT = fs.createWriteStream(propPTPath);
    let wstreamEN = fs.createWriteStream(propENPath);
    let wstreamES = fs.createWriteStream(propESPath);
    let wstreamBR = fs.createWriteStream(propBRPath);
    
    for(var keyPT in langPT){
        var keyValue = langPT[keyPT];
    
        
        var dataPT = keyPT + ' = ' + keyValue;
        
        //Convert accents 
        for (var index in typeJsonPT) {

            if (typeJsonPT[index].key == keyPT) {
                var convertTypePT = typeJsonPT[index].type;

                if (convertTypePT == 'Unicode') {
                    var charsPT = dataPT.match(regEx2);

                    for (var index = 0; index < charsPT.length + 1; index++){
                        var escapecodePT = escape(charsPT[index]).replace("%", '\\u00');

                        dataPT = dataPT.replace(charsPT[index], escapecodePT);
                    }

                }
                if (convertTypePT == 'Html') {
                    var charsHtmlPT = dataPT.match(regEx2);

                    for (var indexHtml = 0; indexHtml < charsHtmlPT.length + 1; indexHtml++){
                        encodeValuePT = htmlEntities.encode(charsHtmlPT[indexHtml]);

                        dataPT = dataPT.replace(charsHtmlPT[indexHtml], encodeValuePT);

                    }
                    
                }

            }
        }

        wstreamPT.write(dataPT + "\n", (err)=>{
            if(err){
                console.log(err.message);
            }
        });

        for (var keyEN in langEN) {
            if (keyPT == keyEN) {
                keyValueEN = langEN[keyPT];

                var dataEN = keyPT + ' = ' + keyValueEN; 

                

                wstreamEN.write(dataEN + "\n", (errEN)=>{
                    if(errEN){
                        console.log("EN : " + errEN.message + "\n");
                    }
                });
            }
        }
        for (var keyES in langES) {
            if (keyPT == keyES) {
                keyValueES = langES[keyPT];

                var dataES = keyPT + ' = ' + keyValueES; 

                //Convert accents 
                for (var indexES in typeJsonES) {
                    if (typeJsonES[indexES].key == keyPT) {
                        var convertTypeES = typeJsonES[indexES].type;
                        if (convertTypeES == 'Unicode') {
                            var charsES = dataES.match(regEx2);

                            for (var index = 0; index < charsES.length + 1; index++){
                                var escapecodeES = escape(charsES[index]).replace("%", '\\u00');

                                dataES = dataES.replace(charsES[index], escapecodeES);
                            }
                        }
                        if (convertTypeES == 'Html') {
                            var charsHtmlES = dataES.match(regEx2);

                            for (var indexHtml = 0; indexHtml < charsHtmlES.length + 1; indexHtml++){
                                encodeValueES = htmlEntities.encode(charsHtmlES[indexHtml]);
                                dataES = dataES.replace(charsHtmlES[indexHtml], encodeValueES);
                            }
                        }
                    }
                }
                
                wstreamES.write(dataES + "\n", (errES)=>{
                    if(errES){
                        console.log("ES : " + errES.message + "\n");
                    }
                });
            }
        }

        for (var keyBR in langBR) {
            if (keyPT == keyBR) {
                keyValueBR = langBR[keyPT];

                var dataBR = keyPT + ' = ' + keyValueBR; 

                //Convert accents 
                for (var indexBR in typeJsonBR) {
                    if (typeJsonBR[indexBR].key == keyPT) {
                        var convertTypeBR = typeJsonBR[indexBR].type;
                        if (convertTypeBR == 'Unicode') {
                            var charsBR = dataBR.match(regEx2);
                
                            for (var index = 0; index < charsBR.length + 1; index++){
                                var escapecodeBR = escape(charsBR[index]).replace("%", '\\u00');
                
                                dataBR = dataBR.replace(charsBR[index], escapecodeBR);
                            }
                        }
                        if (convertTypeBR == 'Html') {
                            var charsHtmlBR = dataBR.match(regEx2);
                
                            for (var indexHtml = 0; indexHtml < charsHtmlBR.length + 1; indexHtml++){
                                encodeValueBR = htmlEntities.encode(charsHtmlBR[indexHtml]);
                                dataBR = dataBR.replace(charsHtmlBR[indexHtml], encodeValueBR);
                            }
                        }
                    }
                }
                                

                wstreamBR.write(dataBR + "\n", (errBR)=>{
                    if(errBR){
                        console.log("BR : " + errBR.message);
                    }
                });
            }
        }
    }

    wstreamPT.end();
    wstreamEN.end();
    wstreamES.end();
    wstreamBR.end();

    wstreamPT.on('finish', () => {
        console.log("The file sbadmin_pt_pt.properties  were created \n");
      });
      wstreamEN.on('finish', () => {
        console.log("The file sbadmin_en_us.properties were created \n");
      });
      wstreamES.on('finish', () => {
        console.log("The file sbadmin_es_es.properties were created \n");
      });
      wstreamBR.on('finish', () => {
        console.log("The file sbadmin_pt_br.properties were created \n");
      });
      
      



      