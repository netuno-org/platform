const fs = require('fs-extra');
const path = require('path');
const AdmZip = require("adm-zip");
const { exec, execSync } = require('child_process')


let config = {
    netuno: {
        cli: "../netuno.cli/target/"
    }
}

if(process.platform === "win32"){
    fs.readdir(config.netuno.cli, (err, files) => {
        if (err) {
            console.log(err)
            return
        }
        files.forEach(file => {
            if(file.startsWith('netuno-cli-') && file.endsWith('-jar-with-dependencies.jar')){
                let cmd = "jar xf ../" + config.netuno.cli + file + " META-INF/MANIFEST.MF"
                exec(cmd, { cwd: __dirname + "/dist", maxBuffer: 1024 * 10000 }, (err, stdout, stderr) => {
                    if (err) {
                        console.log(err)
                        return
                    }
                    console.log(`stdout: ${stdout}`);
                    console.log(`stderr: ${stderr}`);
                    fs.readFile(__dirname + "/dist/META-INF/MANIFEST.MF", (error, data) => {
                        if(error) {
                            throw error;
                        }
                        let lines = data.toString().split(/\r?\n/) || [];
                        lines.forEach(line => {
                            if(line.startsWith('Build-Number:')){
                                let version = line.split(': ')[1];
                                fs.writeFile(__dirname + "/dist/netuno.json", '{"version":"'+version+'","type":"'+process.argv[2].split('=')[1]+'"}\n', 'utf8', function (err) {
                                    if (err) {
                                        console.log("An error occured while writing JSON Object to File.");
                                        return console.log(err);
                                    }
                                    fs.copyFile(__dirname + "/dist/netuno.zip", __dirname + "/dist/netuno-v7-"+version+".zip", (err) => {
                                        if (err) throw err;
                                    });
                                    console.log("JSON file has been saved.");
                                    fs.unlink(__dirname + "/dist/META-INF/MANIFEST.MF");
                                });

                            }
                    
                        })
                    });
                });
            }
        });
    });

}