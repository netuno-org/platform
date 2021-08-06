import React, { Component } from 'react';

import { Button, Typography } from 'antd';

import { HomeOutlined, QuestionOutlined } from '@ant-design/icons';

import ServiceSample from '../../components/ServiceSample/index.jsx';

import styles from './index.less';

const { Title } = Typography;

export default class ListServices extends Component {

    constructor(props) {
        super(props);
        this.state = {
            listServicesData: {}
        };
    }

    render() {
        return (
            <div>
              <div className="panel panel-default">
                <div className={ `panel-heading ${styles.panelHeader}` }>
                  <h3 className="panel-title">Introdução</h3>
                </div>
                <div className="panel-body">
                  <p><Title level={5}><QuestionOutlined /> Como Iniciar</Title></p>
                  <p>
                    Se é novo no Netuno, <a href="https://doc.netuno.org/docs/pt-PT/academy/start/demonstration/form/" target="_blank">inicie criando o seu primeiro formulário</a>.
                  </p>
                  <p><Title level={5}><HomeOutlined /> A partir da raíz do Netuno</Title></p>
                  <p>Pasta da App: <b>apps/demo/</b></p>
                  <p>Configurações da App: <b>apps/demo/config/</b></p>
                  <p>Código fonte dos serviços: <b>apps/demo/server/services</b></p>
                  <p>Código fonte dos templates: <b>apps/demo/server/templates</b></p>
                  <p><a href="https://doc.netuno.org/docs/pt-PT/library/app-folders/app-folder-structure/" target="_blank">+ informações sobre a estrutura de pastas.</a></p>
                  <Button type="dashed" onClick={() => window.open('https://doc.netuno.org/pt-PT/')}>doc.netuno.org</Button>
                </div>
              </div>
              <div className="panel panel-default">
                <div className={ `panel-heading ${styles.panelHeader}` }>
                  <h3 className="panel-title">Exemplos de Serviços</h3>
                </div>
                <div className="panel-body">
                  <div className="row">
                    <div className="col-md-3">
                      <ul className={ styles.list }>
                        <li>
                          <ServiceSample
                            title="Calcular Horas"
                            intro={
                                <p>Encontra a hora futura ao somar 100 horas sobre a hora atual, pode alterar o número de horas no parâmetro da URL para realizar outros cálculos.</p>
                            }
                            name="calc-hours"
                            params={{
                                hours: 100
                            }}
                            langs={["javascript", "kotlin"]}
                          />
                        </li>
                        <li>
                          <ServiceSample
                            title="Formatar Data e Hora"
                            intro={
                                <p>Ao trabalhar com datas e horas muitas vezes é preciso realizar a conversão para string e vice-versa, alguns exemplos de como pode ser feito.</p>
                            }
                            name="date-format"
                            langs={["javascript", "kotlin"]}
                          />
                        </li>
                        <li>
                          <ServiceSample
                            title="Exportar para Excel"
                            intro={
                                <p>Demonstração de como é possível exportar os seus dados para o Excel.</p>
                            }
                            name="export-excel"
                            langs={["javascript", "kotlin"]}
                          />
                        </li>
                        <li>
                          <ServiceSample
                            title="Exportar para PDF"
                            intro={
                                <p>Demonstração de como exportar para o formato PDF a sua informação.</p>
                            }
                            name="export-pdf"
                            langs={["javascript", "kotlin"]}
                          />
                        </li>
                        <li>
                          <ServiceSample
                            title="User"
                            intro={
                                <p>Apresenta a informação do utilizador atualmente logado nesta aplicação.</p>
                            }
                            name="user"
                            langs={["groovy", "javascript", "kotlin", "python", "ruby"]}
                          />
                        </li>
                        <li>
                          <ServiceSample
                            title="Grupo"
                            intro={
                                <p>Apresenta a informação do grupo do utilizador atualmente logado nesta aplicação.</p>
                            }
                            name="group"
                            langs={["groovy", "javascript", "kotlin", "python", "ruby"]}
                          />
                        </li>
                      </ul>
                    </div>
                    <div className="col-md-3">
                      {/*<li>
                         <h4>LOOP Infinito</h4>
                         <div>
                         <p>Aiii meu deuuusss! Não se preocupe! Relaxa...</p>
                         <a href="/services/samples/javascript/infinite-loop" target="_blank">JavaScript</a>
                         </div>
                         </li>*/}
                      <ul className={ styles.list }>
                        <li>
                          <ServiceSample
                            title="Enviar Mail"
                            intro={
                                <p>Exemplo de como enviar e-mail utilizando uma conta Google/GMail, troque no código e na configuração da App em <i>config/</i>, onde os <b>*****</b> (asteriscos) devem ser substituídos pelas informações da respectivas da conta.</p>
                            }
                            name="mail-send"
                            langs={["groovy", "javascript", "kotlin", "python", "ruby"]}
                          />
                        </li>
                        <li>
                          <ServiceSample
                            title="Linhas de Texto"
                            intro={
                                <p>Envia linhas de texto como output.</p>
                            }
                            name="print-lines"
                            langs={["groovy", "javascript", "kotlin", "python", "ruby"]}
                          />
                        </li>
                        <li>
                          <ServiceSample
                            title="Templates"
                            intro={
                                <p>Serve para gerar HTML e manter o código limpo e organizado, verifique o conteúdo dos templates em <i>server/templates/samples</i>.</p>
                            }
                            name="print-template"
                            langs={["groovy", "javascript", "kotlin", "python", "ruby"]}
                          />
                        </li>
                        <li>
                          <ServiceSample
                            title="Parâmetros em Consulta de Dados"
                            intro={
                                <p>Retorna em <i>JSON</i> os registos encontrados em base de dados a partir dos filtros passados em parâmetros no endereço (URL).</p>
                            }
                            name="query-parameter"
                            params={{
                                id: 1,
                                name: 'atriz'
                            }}
                            langs={["groovy", "javascript", "kotlin", "python", "ruby"]}
                          />
                        </li>
                        <li>
                          <ServiceSample
                            title="Operações de Base de Dados"
                            intro={
                                <p>Demonstra como realizar operações de base de dados via código como inserts, updates, deletes, e selects.</p>
                            }
                            name="db"
                            langs={["javascript", "kotlin"]}
                          />
                        </li>
                      </ul>
                    </div>
                    <div className="col-md-3">
                      <ul className={ styles.list }>
                        <li>
                          <ServiceSample
                            title="Iteração de Dados"
                            intro={
                                <p>Retorna em <i>JSON</i> os registos filtrados e carregados através de um loop do tipo <i>for</i>.</p>
                            }
                            name="query-interaction"
                            params={{
                                id: 1
                            }}
                            langs={["groovy", "javascript", "kotlin", "python", "ruby"]}
                          />
                        </li>
                        <li>
                          <ServiceSample
                            title="Simples Obtenção de Dados"
                            intro={
                                <p>Retorna em <i>JSON</i> diretamente os registos encontrados pela query.</p>
                            }
                            name="query-result"
                            langs={["groovy", "javascript", "kotlin", "python", "ruby"]}
                          />
                        </li>
                        <li>
                          <ServiceSample
                            title="UUID"
                            intro={
                                <p>Gera um uuid/guid que é uma string aleatório de identificação única.</p>
                            }
                            name="uid"
                            langs={["groovy", "javascript", "kotlin", "python", "ruby"]}
                          />
                        </li>
                        <li>
                          <ServiceSample
                            title="Upload"
                            intro={
                                <p>Demonstra como é realizado o upload de ficheiros.</p>
                            }
                            name="upload"
                            langs={["javascript", "kotlin"]}
                          />
                        </li>
                        <li>
                          <ServiceSample
                            title="Remote Delete JSON"
                            intro={
                                <p>Realiza um pedido remoto HTTP (request) do tipo <i>delete</i> que normalmente serve para eliminar uma entidade especifica do servidor.</p>
                            }
                            name="remote-delete-json"
                            langs={["javascript", "kotlin"]}
                          />
                        </li>
                      </ul>
                    </div>
                    <div className="col-md-3">
                      <ul className={ styles.list }>
                        <li>
                          <ServiceSample
                            title="Remote Patch JSON"
                            intro={
                                <p>Realiza um pedido remoto HTTP (request) do tipo <i>patch</i> que normalmente serve para enviar alterações de dados.</p>
                            }
                            name="remote-patch-json"
                            langs={["javascript", "kotlin"]}
                          />
                        </li>
                        <li>
                          <ServiceSample
                            title="Remote Post JSON"
                            intro={
                                <p>Realiza um pedido remoto HTTP (request) do tipo <i>post</i> que normalmente serve para criar ou inserir novas estruturas de dados.</p>
                            }
                            name="remote-post-json"
                            langs={["javascript", "kotlin"]}
                          />
                        </li>
                        <li>
                          <ServiceSample
                            title="Remote Put JSON"
                            intro={
                                <p>Realiza um pedido remoto HTTP (request) do tipo <i>put</i> que normalmente serve para definir os dados, cria caso não exista ou altera caso exista.</p>
                            }
                            name="remote-put-json"
                            langs={["javascript", "kotlin"]}
                          />
                        </li>
                        <li>
                          <ServiceSample
                            title="Remote Get JSON"
                            intro={
                                <p>Realiza um pedido remoto HTTP (request) do tipo <i>get</i> que normalmente serve para obter dados.</p>
                            }
                            name="remote-get-json"
                            langs={["javascript", "kotlin"]}
                          />
                        </li>
                        <li>
                          <ServiceSample
                            title="SMS"
                            intro={
                                <p>Demonstra como enviar SMS através de um serviço externo, neste caso é o serviço do <i>Mailjet</i>, necessita configurar o <b>YOUR_TOKEN</b> e o <b>YOUR_INTERNATIONAL _PHONE_NUMBER</b>.</p>
                            }
                            name="remote-mailjet-sms"
                            langs={["javascript", "kotlin"]}
                          />
                        </li>
                      </ul>
                    </div>
                  </div>
                </div>
              </div>
            </div>
        );
    }
}
