const fs = require('fs')
const path = require('path')

const publishMode = process.argv[2]

const filePOM = path.join(__dirname, '../netuno.cli/pom.xml')

let data = fs.readFileSync(filePOM).toString()

let buildNumberFormat = '{0,date,yyyy.MM}'

if (publishMode == 'testing') {
  buildNumberFormat = '{0,date,yyyy.MM.dd}-testing'
}

data = data.replace('<format>{0,date,yyyy.MM}</format>', `<format>${buildNumberFormat}</format>`)
data = data.replace('<format>{0,date,yyyy.MM.dd}-testing</format>', `<format>${buildNumberFormat}</format>`)

fs.writeFileSync(filePOM, data)
