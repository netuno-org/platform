import React, { Component } from "react";

import { Modal, Button } from 'antd';

import Editor from '@monaco-editor/react';

import styles from "./index.less";

const langTitles = {
    groovy: "Groovy",
    javascript: "JavaScript",
    kotlin: "Kotlin",
    python: "Python",
    ruby: "Ruby"
}

const langExtensions = {
    groovy: "groovy",
    javascript: "js",
    kotlin: "kts",
    python: "py",
    ruby: "rb"
}

export default class ServiceSample extends Component {

    constructor(props) {
        super(props);
        this.state = {
            showModalSource: false,
            modalLanguage: '',
            modalPhysicalPath: '',
            modalSource: ''
        }
        this.editor = React.createRef();
        this.handleEditorDidMount = this.handleEditorDidMount.bind(this);
    }

    encodeData(data) {
        return Object.keys(data).map((key) => {
            return [key, data[key]].map(encodeURIComponent).join("=");
        }).join("&");
    }

    handleEditorDidMount(valueGetter, editor) {
        this.editor.current = editor;
        this.editor.current.updateOptions({
            readOnly: true,
            glyphMargin: false,
            folding: false,
            automaticLayout: true,
            minimap: {
		        enabled: false
	        }
        });
        this.editor.current.onDidChangeModelContent(ev => {
            this.setState({content: this.editor.current.getValue()});
            //console.log(this.editor.current.getValue());
        });
    }

    handleSourceClick(lang, name) {
        const path = `${lang}/${name}.${langExtensions[lang]}`;
        netuno.service({
            url: "/services/samples/source",
            data: { path },
            success: (data) => {
                this.setState({showModalSource: true, modalLanguage: lang, modalPhysicalPath: data.json.path, modalSource: data.json.source});
            },
            fail: (data) => {
                console.log(`Error getting ${path}`, data);
            }
        })
    }

    render() {
        const { showModalSource, modalLanguage, modalPhysicalPath, modalSource } = this.state;

        const servicePrefix = '/services/samples';
        let servicePath = this.props.name;
        if (this.props.params) {
            servicePath += '?'+ this.encodeData(this.props.params);
        }
        
        let modal = null;
        if (showModalSource) {
            modal = (
                <Modal
                  title={`${langTitles[modalLanguage]} - ${this.props.title}`}
                  onCancel={() => { this.setState({showModalSource: false}) }}
                  footer={null}
                  visible={true}
                  width={"90vw"}
                >
                  {this.props.intro}
                  <p><b>Endereço (URL):</b> <a href={`${servicePrefix}/${modalLanguage}/${servicePath}`} target="_blank">http://{window.location.host}{servicePrefix}/{modalLanguage}/{servicePath}</a></p>
                  <p><b>Caminho (Disco/HD):</b> {modalPhysicalPath}</p>
                  <p><b>Código Fonte {langTitles[modalLanguage]}:</b></p>
                  <Editor height="200px" language={modalLanguage} value={modalSource} editorDidMount={this.handleEditorDidMount} />
                  <hr/>
                  <Button type="primary" onClick={() => { window.open(`${servicePrefix}/${modalLanguage}/${servicePath}`) }}>Executar</Button>
                </Modal>
            );
        }
        const languages = [];
        for (const lang of this.props.langs) {
            if (languages.length > 0) {
                //languages.push(<>&nbsp;</>);
            }
            languages.push(<Button onClick={() => this.handleSourceClick(lang, `${this.props.name}`)}>{langTitles[lang]}</Button>);
        }
        return (
            <>
              {modal}
              <h4>{this.props.title}</h4>
              <div>
                {this.props.intro}
                <div className={ styles.serviceSampleButtons }>{languages}</div>
              </div>
            </>
        );
    }
}

