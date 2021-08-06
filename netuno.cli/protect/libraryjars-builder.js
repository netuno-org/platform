const folder = require('path').dirname(require.main.filename);
const fs = require('fs');

const jarsFolders = [
  '../target/lib',
  '../../netuno-psamata/target/lib'
];

let allFiles = [];

let jars = '';

var loadJars = (jarsFolder, jarsFolderIndex) => {
  if (jarsFolderIndex == jarsFolders.length) {
    console.log('');
    console.log('-libraryjars  <java.home>/lib/rt.jar:'+ jars);
    console.log('');
    return;
  }
  fs.readdir(folder + '/' + jarsFolder, (err, files) => {
    console.log('');
    files.forEach((file, index) => {
      if (file.indexOf('.jar') == file.length - '.jar'.length) {
        if (allFiles.filter((allFile) => {  return allFile == file }).length == 0) {
          jars += (jars == '' ? '' : ':') + jarsFolder +'/'+ file;
          allFiles.push(file);
        }
      }
    });
    jarsFolderIndex++;
    loadJars(jarsFolders[jarsFolderIndex], jarsFolderIndex);
  });
}
loadJars(jarsFolders[0], 0);
