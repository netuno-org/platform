const folder = require('path').dirname(require.main.filename);
const fs = require('fs');

const jarsFolders = [
  '../target/lib',
  '../../netuno-psamata/target/lib',
  '../../netuno-proteu/target/lib'
];

let jarsFiles = [];

let jars = '';

var loadJars = (jarsFolder, jarsFolderIndex) => {
  if (jarsFolderIndex == jarsFolders.length) {
    jarsFiles.forEach((jarFile) => {
        jars += (jars == '' ? '' : ':') + jarFile.folder + '/' + jarFile.name +'-'+ jarFile.version +'.jar';
    });
    console.log('');
    console.log('-libraryjars  <java.home>/lib/rt.jar:'+ jars);
    console.log('');
    //console.log(jarsFiles);
    return;
  }
  fs.readdir(folder + '/' + jarsFolder, (err, files) => {
    console.log('');
    files.forEach((fileName) => {
      if (fileName.indexOf('.jar') != fileName.length - '.jar'.length) {
        return;
      }
      let jarFile = toFile(jarsFolder, fileName);
      if (jarFile.name.indexOf('netuno-') == 0
          || jarFile.name == 'tools'
          || jarFile.name.indexOf('junit-') == 0) {
          return;
      }
      let addThisFile = true;
      jarsFiles = jarsFiles.filter((jf) => {
          if (jf.name == jarFile.name && jf.version > jarFile.version) {
              addThisFile = false;
          }
          return jf.name != jarFile.name ||
              (jf.name == jarFile.name && jf.version > jarFile.version);
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

let toFile = (folder, fileName) => {
  let fullName = fileName.substring(0, fileName.length - '.jar'.length);
  let name = fullName;
  let version = '';
  if (name == 'javax.inject-1') {
    name = 'javax.inject';
    version = '1';
  } else {
    if (name.lastIndexOf('.') > 0) {
        name = name.substring(0, name.lastIndexOf('.'));
    }
    if (name.lastIndexOf('-') > 0) {
        name = name.substring(0, name.lastIndexOf('-'));
    }
    version = fileName.substring(name.length + 1, fullName.length);
  }
  return {
    name, version, folder
  }
};
