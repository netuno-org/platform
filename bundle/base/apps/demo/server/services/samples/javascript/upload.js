/**
 *
 *  EN: UPLOAD
 *  EN: Receives a file upload and stores it in the filesystem,
 *  EN: content is extracted if it is an XLSX or PDF.
 *
 *  PT: UPLOAD
 *  PT: Recebe um arquivo carregado e o armazena no sistema de arquivos,
 *  PT: o conteúdo é extraído se for um arquivo XLSX ou PDF.
 *
 */
import {_out, _pdf, _req, _storage, _template, _val, _xls} from "@netuno/server-types";

const data = _val.init();

if (_req.hasKey("fileUpload")) {
    const file = _req.getFile("fileUpload");
    const storage = _storage.filesystem("server", "samples", file.getName());
    file.save(storage);
    data.set("filePath", storage.fullPath());

    if (["xls", "xlsx"].find((ext) => file.isExtension(ext))) {
        const xlsInfo = _xls.read(file);
        data.set('extra', xlsInfo.toJSON(true, 4));
    } else if (file.isExtension("pdf")) {
        const pdfInfo = _pdf.extract(storage);
        data.set('extra', pdfInfo.getString('content'));
    }
}

if (_req.getBoolean("ajax")) {
    _out.json(data);
} else {
    _template.output("samples/upload", data);
}
