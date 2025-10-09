var XLSX = require('xlsx');
var fs = require('fs');
var properties = require('java-properties');
var Entities = require('html-entities').AllHtmlEntities;

var htmlEntities = new Entities();
//var execSync = require('child_process').execSync;

// Files Paths 

var fileEN = properties.of('../sbadmin_en_us.properties');
var filePT = properties.of('../sbadmin_pt_pt.properties'); 
var fileES = properties.of('../sbadmin_es_es.properties');
var fileBR = properties.of('../sbadmin_pt_br.properties');
var xlsxPath = 'output/translations.xlsx';

//Check if diretory exists
var dir = './output';

if (!fs.existsSync(dir)) {
    fs.mkdirSync(dir);
}

var workbook = { SheetNames: [], Sheets: {} };

var worksheet = {};

worksheet['A1'] = { v: 'KEY', t: 's' };
worksheet['B1'] = { v: 'EN', t: 's' };
worksheet['C1'] = { v: 'PT', t: 's' };
worksheet['D1'] = { v: 'ES', t: 's' };
worksheet['E1'] = { v: 'BR', t: 's' };

var html = [];


// Getting all the key
var langPT = filePT.getKeys();


var line = 0;
var filesParse = [];
var filesParseES = [];
var filesParseBR = [];
var htmlParsePT = [];

// Regular Expressions 
var regEx = new RegExp('\&([#a-zA-Z0-9]+)\;', 'gm'); // Html
var regEx2 = new RegExp('([\\u00C0-\\u024F\\u1E00-\\u1EFF])', 'gm'); // Unicode

// Data for Excel
for (var keyPT in langPT) {


    var keyValue = langPT[keyPT];

    var valuePT = filePT.get(keyValue);

    var valueEN = fileEN.get(keyValue);

    var valueES = fileES.get(keyValue);

    var valueBR = fileBR.get(keyValue);

    if (valuePT !== undefined) {
        if (valuePT.includes('&')) {
            html = valuePT.match(regEx);
            if (html != null) {
            
                var parsetype = {
                    "key": keyValue,
                    "type": "Html"
                }
                if (!(keyValue in filesParse) && !(keyValue in htmlParsePT)) {
                    filesParse.push(parsetype);
                    htmlParsePT.push(parsetype);
                }

                for (var j = 0; j < html.length + 1; j++) {

                    decodeValue = htmlEntities.decode(html[j]);

                    valuePT = valuePT.replace(html[j], decodeValue);
                }

            }
        } else {
            var chars = valuePT.match(regEx2);
            if (chars != null) {
                
                var parsetype = {
                    "key": keyValue,
                    "type": "Unicode"
                }
                      
                if (!(keyValue in filesParse)) {
                    filesParse.push(parsetype);
                }
            }
        
        }
    }

    if (valueES !== undefined) {
        if (valueES.includes('&')) {
             html = valueES.match(regEx);

            if (html != null) {
            
                var parsetype = {
                    "key": keyValue,
                    "type": "Html"
                }
                if (!(keyValue in filesParseES)) {
                    filesParseES.push(parsetype);
                }

                for (var j = 0; j < html.length + 1; j++) {

                    decodeValue = htmlEntities.decode(html[j]);

                    valueES = valueES.replace(html[j], decodeValue);
                }
                
            }

        } else {
            var chars = valueES.match(regEx2);
            if (chars != null) {
                var parsetype = {
                    "key": keyValue,
                    "type": "Unicode"
                }
                if (!(keyValue in filesParseES)) {
                    filesParseES.push(parsetype);
                }
            }
        }
    }

    
    if (valueBR !== undefined) {
        if (valueBR.includes('&')) {
             html = valueBR.match(regEx);

            if (html != null) {
            
                var parsetype = {
                    "key": keyValue,
                    "type": "Html"
                }
                if (!(keyValue in filesParseBR)) {
                    filesParseBR.push(parsetype);
                }

                for (var j = 0; j < html.length + 1; j++) {

                    decodeValue = htmlEntities.decode(html[j]);

                    valueBR = valueBR.replace(html[j], decodeValue);
                }
                
            }
        } else {
            var chars2 = valueBR.match(regEx2);
            if (chars2 != null) {
                var parsetype = {
                    "key": keyValue,
                    "type": "Unicode"
                }
                if (!(keyValue in filesParseBR)) {
                    filesParseBR.push(parsetype);
                }
            }
        }
    }
    // 
    htmlParsePT.forEach(function (element) {
        filesParseES.forEach(function (element2) {
            if (element.key === element2.key && element.type != element2.type) {        
                element2.type = element.type;
            }
        });
    });

    htmlParsePT.forEach(function (element) {
        filesParseBR.forEach(function (element2) {
            if (element.key === element2.key && element.type != element2.type) {        
                element2.type = element.type;
            }
        });
    });


   line++;

   worksheet[XLSX.utils.encode_cell({ c: 0, r: line })] = { v: keyValue, t: 's' };
   worksheet[XLSX.utils.encode_cell({ c: 1, r: line })] = { v: valueEN, t: 's' };
   worksheet[XLSX.utils.encode_cell({ c: 2, r: line })] = { v: valuePT, t: 's' };
   worksheet[XLSX.utils.encode_cell({ c: 3, r: line })] = { v: valueES, t: 's' };
   worksheet[XLSX.utils.encode_cell({ c: 4, r: line })] = { v: valueBR, t: 's' };
}



worksheet['!ref'] = XLSX.utils.encode_range({s: { c: 0, r: 0 }, e: { c: 4, r: line }});

workbook.SheetNames.push('Netuno Lang');
workbook.Sheets['Netuno Lang'] = worksheet;


// Creation of the Jsons of the Translations
fs.writeFile(__dirname + '/output/ConvertionTypes-pt.json', JSON.stringify(filesParse, null, 1), function (err) {
    if (err) {
        return console.log(err);
    }
});

fs.writeFile(__dirname + '/output/ConvertionTypes-es.json', JSON.stringify(filesParseES, null, 1), function (err) {
    if (err) {
        return console.log(err);
    }
});

fs.writeFile(__dirname + '/output/ConvertionTypes-br.json', JSON.stringify(filesParseBR, null, 1), function (err) {
    if (err) {
        return console.log(err);
    }
});



XLSX.writeFile(workbook, xlsxPath);

console.log("The transltations file was created");