const webfont = require('webfont').default;
const fs = require('fs');
const path = require('path');

const webfontConfig = {
  files: 'src/icons/*.svg',
  dest: 'src/styles/font/',
  formats: ['ttf', 'eot', 'woff', 'woff2'],
  fontName: 'summernote',
  template: 'src/icons/templates/summernote-icons.css',
  destTemplate: 'src/styles/summernote-icons.css',
  templateFontName: 'summernote',
  templateClassName: 'note-icon',
  templateFontPath: './font/',
  fixedWidth: false,
  normalize: true,
};

console.log('Building fonts...');

webfont(webfontConfig).then(result => {
  Object.keys(result).map(type => {
    if (
      type === 'config' ||
      type === 'usedBuildInTemplate' ||
      type === 'glyphsData'
    ) {
      return;
    }

    const content = result[type];
    let file = null;

    if (type !== 'template') {
      file = path.resolve(path.join(webfontConfig['dest'], webfontConfig['fontName'] + '.' + type));
    } else {
      file = path.resolve(webfontConfig['destTemplate']);
    }
    console.log('Writing ', file);

    fs.writeFileSync(file, content);
  });
}).catch(error => {
  console.log(error);
  throw error;
});
