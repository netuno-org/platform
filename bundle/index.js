
const fs = require('fs-extra')

const { exec, execSync } = require('child_process')
const path = require('path');
const folder = require('path').dirname(require.main.filename)
const AdmZip = require("adm-zip");

const config = {
    base: {
        root: 'base',
        apps: 'base/apps',
        web: {
            lib: 'base/web/WEB-INF/lib'
        }
    },
    output: {
        root: 'out',
        bundle: 'out/netuno',
        bundleName: 'netuno'
    },
    netuno: {
        web: {
            lib: {
                tritao: '../netuno.tritao/target/lib',
                proteu: '../netuno.proteu/target/lib',
                psamata: '../netuno.psamata/target/lib'
            },
            bundle: '../netuno.tritao/protect/out/proguard/netuno-web.jar'
        },
        cli: {
            bundle: '../netuno.cli/protect/out/proguard/netuno.jar',
            resources: {
                app: '../netuno.cli/src/main/resources/org/netuno/cli/app',
            }
        },
        apps: '../netuno.apps/apps'
    }
}

fs.emptyDirSync(config.base.web.lib)
fs.emptyDirSync(config.output.bundle)
fs.removeSync(config.output.bundle +'.zip')

fs.copySync(config.base.root, config.output.bundle, {
    filter: (src, dest) => {
        return src.indexOf('graalvm') < 0
            && src.indexOf('node_modules') < 0
            && src.indexOf('classes') < 0
            && src.endsWith('~') == false
            && src.endsWith('#') == false
            && src.endsWith('.swp') == false
            && src.endsWith('.log') == false
    }
})


// TODO: Verificar se é windows e se for adicionar função de eleminar todos os ficheiros .json 
// execSync('c *.json', { cwd: config.output.bundle +'/apps/', maxBuffer: 1024 * 10000 })

fs.readdir(config.output.bundle +'/apps/', (err, files) => {
    files.forEach(file => {
        if (file.includes('.json')) {
            fs.unlink(config.output.bundle + '/apps/' + file, function (err) {
                if (err) {
                    console.log('Cant delete this file ' + file)
                };
                console.log('File deleted! ' + file);
            });
        }
    });
});



fs.removeSync(config.output.bundle +'/apps/_')
fs.removeSync(config.output.bundle +'/web/WEB-INF/classes')

fs.copySync(config.netuno.cli.bundle, config.output.bundle +'/netuno.jar')
fs.copySync(config.netuno.web.bundle, config.output.bundle +'/web/WEB-INF/lib/netuno-web.jar')

const jarsFolders = [
    config.netuno.web.lib.tritao,
    config.netuno.web.lib.proteu,
    config.netuno.web.lib.psamata
];

let jarsFiles = []
let jars = ''

var loadJars = (jarsFolder, jarsFolderIndex) => {
    if (jarsFolderIndex == jarsFolders.length) {

        jarsFiles.forEach((jarFile) => {
            fs.copySync(jarFile.folder +'/'+ jarFile.file, config.base.web.lib +'/'+ jarFile.file)
            jars += (jars == '' ? '' : ':') + jarFile.folder + '/' + jarFile.name +'-'+ jarFile.version +'.jar'
        });


        console.log('');
        console.log('JARS:', jars);
        console.log('');


        //console.log(jarsFiles);

        //<trash>
        //fs.copySync(config.output.bundle +'/web/', config.base.root +'/netuno.jar')
        //</trash>

        /*
        ;['system'].forEach((devWidgetName) => {
            try{
                fs.copySync(
                    config.output.bundle +'/web/netuno/dev/scripts/sbadmin/widgets/'+ devWidgetName +'/build/bundle.js',
                    config.output.bundle +'/web/netuno/dev/scripts/sbadmin/widgets/'+ devWidgetName +'.js'
                )
                fs.removeSync(config.output.bundle +'/web/netuno/dev/scripts/sbadmin/widgets/'+ devWidgetName)
            }catch(e){
                console.clear();
                console.log(" ERROR 1111");
                console.log(e);
                process.exit(0)
            }

        });
        */

        fs.copySync(config.base.web.lib, config.output.bundle +'/web/WEB-INF/lib');



        //<trash>
        //fs.copySync(config.base.root, config.output.bundle)
        //</trash>
        fs.removeSync(config.output.bundle +'/web/netuno/.git')
        fs.removeSync(config.output.bundle +'/web/netuno/.gitignore')
	    fs.removeSync(config.output.bundle +'/web/netuno/lang/translations-builder')
        
        //<trash>
        //fs.copySync(config.output.bundle +'/web/', config.base.root +'/netuno.jar')
        //</trash>

        // Sync CLI APP
        fs.removeSync(config.netuno.cli.resources.app)
        fs.copySync(config.base.apps +'/_', config.netuno.cli.resources.app, {
          filter: path => {
            return path.indexOf('node_modules') < 0
          }
        })

        fs.removeSync(config.netuno.cli.resources.app + '/.git')
        fs.removeSync(config.netuno.cli.resources.app + '/config/_development.json')
        fs.removeSync(config.netuno.cli.resources.app + '/config/_production.json')
	    fs.removeSync(config.netuno.cli.resources.app + '/config/config-sample.json')
        
        //fs.moveSync(config.netuno.cli.resources.app +'/dbs/demo.mv.db', config.netuno.cli.resources.app +'/dbs/app.mv.db')
        //fs.moveSync(config.netuno.cli.resources.app +'/dbs/demo.trace.db', config.netuno.cli.resources.app +'/dbs/app.trace.db')

        // Clear Demo App
        fs.removeSync(config.output.bundle + '/apps/demo/.git')
        fs.removeSync(config.output.bundle + '/apps/demo/.gitignore')
        fs.removeSync(config.output.bundle + '/apps/demo/ui/node_modules')


        //fs.moveSync(config.output.bundle +'/apps/demo/dbs/demo.mv.db', config.output.bundle +'/dbs/demo.mv.db')
        //fs.moveSync(config.output.bundle +'/apps/demo/dbs/demo.trace.db', config.output.bundle +'/dbs/demo.trace.db')
        //fs.removeSync(config.output.bundle +'/apps/demo/dbs')

        let files = [];
        const getFilesRecursively = (directory) => {
          const filesInDirectory = fs.readdirSync(directory);
          for (const file of filesInDirectory) {
            const absolute = path.join(directory, file);
            if (fs.statSync(absolute).isDirectory()) {
                getFilesRecursively(absolute);
            } else {
                files.push(absolute);
            }
          }
        };
        const directoryPath = path.join(config.output.bundle + '/apps/');
        getFilesRecursively(directoryPath);
        for(const dir of files){
            if(dir.includes('.DS_Store') || dir.includes('.sass-cache$')){
                fs.unlink(dir, function (err) {
                    if (err) {
                        console.log('Cant delete this file ' + dir)
                    };
                    console.log('File deleted!' + dir);
                });
            }
          
        }

        cmd = 'java -jar netuno.jar install checksum yes'
        console.log('$ '+ cmd)
        exec(cmd, { cwd: config.output.bundle, maxBuffer: 1024 * 10000 }, (err, stdout, stderr) => {
            if (err) {
                console.log(err)
                return
            }

            console.log(`stdout: ${stdout}`);
            console.log(`stderr: ${stderr}`);

            fs.removeSync(config.output.bundle +'/logs/netuno.log')

            try {
                const zip = new AdmZip();
                const outputFile = config.output.root + "/" +config.output.bundleName + ".zip";
                zip.addLocalFolder(config.output.root + "/");
                zip.writeZip(outputFile);
                console.log(`Created ${outputFile} successfully`);
            } catch (e) {
                console.log(config.output.bundleName)
                console.log(`Something went wrong. ${e}`);
            }
        });
        
/* -> OLD
        let cmd = 'find '+ config.output.bundleName +' -name ".DS_Store" | xargs trash-put';
        console.log('$ '+ cmd)
        exec(cmd, { cwd: config.output.root, maxBuffer: 1024 * 10000 }, (err, stdout, stderr) => {
            if (err) {
                console.log(err)
            }

            console.log(`stdout: ${stdout}`);
            console.log(`stderr: ${stderr}`);

            //TODO: ENCONTRA O FICHEIRO E O APAGA.
            cmd = 'find '+ config.output.bundleName +' -type d | grep "\.sass-cache$" | xargs trash-put';
            console.log('$ '+ cmd)
            exec(cmd, { cwd: config.output.root, maxBuffer: 1024 * 10000 }, (err, stdout, stderr) => {
                if (err) {
                    console.log(err)
                }
                console.log(`stdout: ${stdout}`);
                console.log(`stderr: ${stderr}`);
                cmd = 'java -jar netuno.jar install checksum yes'
                console.log('$ '+ cmd)
                exec(cmd, { cwd: config.output.bundle, maxBuffer: 1024 * 10000 }, (err, stdout, stderr) => {
                    if (err) {
                        console.log(err)
                        return
                    }

                    console.log(`stdout: ${stdout}`);
                    console.log(`stderr: ${stderr}`);

                    fs.removeSync(config.output.bundle +'/logs/netuno.log')

                    cmd = 'zip -qr '+ config.output.bundleName +'.zip '+ config.output.bundleName
                    console.log('$ '+ cmd)
                    exec(cmd, { cwd: config.output.root, maxBuffer: 1024 * 10000 }, (err, stdout, stderr) => {
                        if (err) {
                            console.log(err)
                            return;
                        }
                        console.log(`stdout: ${stdout}`);
                        console.log(`stderr: ${stderr}`);
                    });
                });
            });
        });*/

        return;
    }

    fs.readdir(folder + '/' + jarsFolder, (err, files) => {
        if (err) {
            console.log(err)
            return
        }
        files.forEach((fileName) => {
            if (fileName.indexOf('.jar') != fileName.length - '.jar'.length) {
                return;
            }
            let jarFile = toFile(jarsFolder, fileName);
            if (jarFile.name.indexOf('netuno-') == 0
                || jarFile.name == 'tools'
                || jarFile.name.indexOf('junit-') == 0
                || jarFile.name.indexOf('graal-') == 0
                || jarFile.name.indexOf('slf4j-') == 0
                || jarFile.name.indexOf('oshi-') == 0
                || jarFile.name.indexOf('jakarta.activation') == 0
                || jarFile.name.indexOf('jakarta.mail') == 0) {
                return;
            }
            let addThisFile = true;
            jarsFiles = jarsFiles.filter((jf) => {
                if (jf.name == jarFile.name && jf.version > jarFile.version 
                    && jf.name.indexOf('annotations') != 0 && jarFile.name.indexOf('annotations') != 0) {
                    addThisFile = false;
                }
                return jf.name != jarFile.name ||
                (jf.name == jarFile.name && jf.version > jarFile.version
                 && jf.name.indexOf('annotations') != 0 && jarFile.name.indexOf('annotations') != 0)
                || jf.name.indexOf('annotations') == 0 && jarFile.name.indexOf('annotations') == 0;
            });
            if (addThisFile) {
                jarsFiles.push(jarFile);
            }
        });
        jarsFolderIndex++;
        loadJars(jarsFolders[jarsFolderIndex], jarsFolderIndex);
    });
    
};
loadJars(jarsFolders[0], 0);

let toFile = (folder, file) => {
    let fullName = file.substring(0, file.length - '.jar'.length)
    let name = fullName
    let version = ''
    if (name == 'javax.inject-1') {
        name = 'javax.inject'
        version = '1'
    } else {
        if (name.lastIndexOf('.') > 0) {
            name = name.substring(0, name.lastIndexOf('.'))
        }
        if (name.lastIndexOf('-') > 0) {
            name = name.substring(0, name.lastIndexOf('-'))
        }
        version = file.substring(name.length + 1, fullName.length)
    }
    return {
        file, name, version, folder
    }
};

