import React, { useState, useRef } from "react";

import { Modal, Button } from 'antd';

import Editor from '@monaco-editor/react';

import { useIntl } from 'react-intl';

import "./index.less";

const messages = 'dashboardcontainer.listservices';

const langTitles = {
    groovy: "Groovy",
    javascript: "JavaScript",
    kotlin: "Kotlin",
    python: "Python",
    ruby: "Ruby"
};

const langExtensions = {
    groovy: "groovy",
    javascript: "js",
    kotlin: "kts",
    python: "py",
    ruby: "rb"
};

function ServiceSample({name, title, intro, langs, params}) {
  const intl = useIntl();

  const [showModalSource, setShowModalSource] = useState(false);
  const [modalLanguage, setModalLanguage] = useState('');
  const [modalPhysicalPath, setModalPhysicalPath] = useState('');
  const [modalSource, setModalSource] = useState('');
  const [content, setContent] = useState('');
  const editor = useRef();

  const encodeData = (data)=> {
    return Object.keys(data).map((key) => {
      return [key, data[key]].map(encodeURIComponent).join("=");
    }).join("&");
  };

  const handleEditorDidMount = (valueGetter, editor) => {
    editor.current = editor;
    editor.current.updateOptions({
      readOnly: true,
      glyphMargin: false,
      folding: false,
      automaticLayout: true,
      minimap: {
        enabled: false
      }
    });
    editor.current.onDidChangeModelContent(ev => {
      setContent(editor.current.getValue());
      //console.log(this.editor.current.getValue());
    });
  };

  const handleSourceClick = (lang, name)=> {
    const path = `${lang}/${name}.${langExtensions[lang]}`;
    netuno.service({
      url: "/services/samples/source",
      data: { path },
      success: (data) => {
        setModalLanguage(lang);
        setModalPhysicalPath(data.json.path);
        setModalSource(data.json.source);
        setShowModalSource(true);
      },
      fail: (data) => {
        console.log(`Error getting ${path}`, data);
      }
    });
  };

  const servicePrefix = '/services/samples';
  let servicePath = name;
  if (params) {
    servicePath += '?' + encodeData(params);
  }

  let modal = null;
  if (showModalSource) {
    modal = (
      <Modal
        title={`${langTitles[modalLanguage]} - ${title}`}
        onCancel={() => { setShowModalSource(false); }}
        footer={null}
        visible={true}
        width={"90vw"}
      >
        <div dangerouslySetInnerHTML={{ __html: intro }} />
        <p><b>{intl.formatMessage({ id: `${messages}.services.modal.url` })}:</b> <a href={`${servicePrefix}/${modalLanguage}/${servicePath}`} target="_blank">http://{window.location.host}{servicePrefix}/{modalLanguage}/{servicePath}</a></p>
        <p><b>{intl.formatMessage({ id: `${messages}.services.modal.path` })}:</b> {modalPhysicalPath}</p>
        <p><b>{intl.formatMessage({ id: `${messages}.services.modal.source-code` })} {langTitles[modalLanguage]}:</b></p>
        <Editor height="200px" language={modalLanguage} value={modalSource} editorDidMount={handleEditorDidMount} />
        <hr />
        <Button type="primary" href={`${servicePrefix}/${modalLanguage}/${servicePath}`} target="_blank">{intl.formatMessage({ id: `${messages}.services.modal.execute-button` })}</Button>
      </Modal>
    );
  }
  const languages = [];
  for (const lang of langs) {
    if (languages.length > 0) {
      //languages.push(<>&nbsp;</>);
    }
    languages.push(<Button key={lang} onClick={() => handleSourceClick(lang, `${name}`)}>{langTitles[lang]}</Button>);
  }
  return (
    <>
      {modal}
      <h4>{title}</h4>
      <div>
        <div dangerouslySetInnerHTML={{ __html: intro }} />
        <div className="service-sample__languages">{languages}</div>
      </div>
    </>
  );
}

export default ServiceSample;
