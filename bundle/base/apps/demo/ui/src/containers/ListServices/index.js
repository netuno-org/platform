import React, { useState } from 'react';

import { Typography, Button } from 'antd';

import { HomeOutlined, QuestionOutlined } from '@ant-design/icons';

import { useIntl } from 'react-intl';

import ServiceSample from '../../components/ServiceSample';

import './index.less';

const messages = 'dashboardcontainer.listservices';

const { Title } = Typography;

function ListServices() {
  const intl = useIntl();

  const [listServicesData, setListServicesData] = useState(null);

  return (
    <div>
      <div className="panel panel-default">
        <div className="panel-heading list-services__panel-header">
          <h3 className="panel-title">{intl.formatMessage({ id: `${messages}.introduction.title` })}</h3>
        </div>
        <div className="panel-body">
          <Title level={5}><QuestionOutlined /> {intl.formatMessage({ id: `${messages}.introduction.how-to-start.title` })}</Title>
          <div dangerouslySetInnerHTML={{ __html: intl.formatMessage({ id: `${messages}.introduction.how-to-start.content` }) }} />
          <Title level={5}><HomeOutlined /> {intl.formatMessage({ id: `${messages}.introduction.from-the-root.title` })}</Title>
          <div dangerouslySetInnerHTML={{ __html: intl.formatMessage({ id: `${messages}.introduction.from-the-root.content` }) }} />
          <Button type="dashed" onClick={() => window.open(intl.formatMessage({ id: `${messages}.introduction.button-link` }))}>doc.netuno.org</Button>
        </div>
      </div>
      <div className="panel panel-default">
        <div className="panel-heading list-services__panel-header">
          <h3 className="panel-title">{intl.formatMessage({ id: `${messages}.services.title` })}</h3>
        </div>
        <div className="panel-body">
          <div className="row">
            <div className="col-md-3">
              <ul className="list-services__list">
                <li>
                  <ServiceSample
                    title={intl.formatMessage({ id: `${messages}.services.calc-hours.title` })}
                    intro={intl.formatMessage({ id: `${messages}.services.calc-hours.intro` })}
                    name="calc-hours"
                    params={{
                      hours: 100
                    }}
                    langs={["javascript", "kotlin"]}
                  />
                </li>
                <li>
                  <ServiceSample
                    title={intl.formatMessage({ id: `${messages}.services.date-format.title` })}
                    intro={intl.formatMessage({ id: `${messages}.services.date-format.intro` })}
                    name="date-format"
                    langs={["javascript", "kotlin"]}
                  />
                </li>
                <li>
                  <ServiceSample
                    title={intl.formatMessage({ id: `${messages}.services.export-excel.title` })}
                    intro={intl.formatMessage({ id: `${messages}.services.export-excel.intro` })}
                    name="export-excel"
                    langs={["javascript", "kotlin"]}
                  />
                </li>
                <li>
                  <ServiceSample
                    title={intl.formatMessage({ id: `${messages}.services.export-pdf.title` })}
                    intro={intl.formatMessage({ id: `${messages}.services.export-pdf.intro` })}
                    name="export-pdf"
                    langs={["javascript", "kotlin"]}
                  />
                </li>
                <li>
                  <ServiceSample
                    title={intl.formatMessage({ id: `${messages}.services.user.title` })}
                    intro={intl.formatMessage({ id: `${messages}.services.user.intro` })}
                    name="user"
                    langs={["groovy", "javascript", "kotlin", "python", "ruby"]}
                  />
                </li>
                <li>
                  <ServiceSample
                    title={intl.formatMessage({ id: `${messages}.services.group.title` })}
                    intro={intl.formatMessage({ id: `${messages}.services.group.intro` })}
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
              <ul className="list-services__list">
                <li>
                  <ServiceSample
                    title={intl.formatMessage({ id: `${messages}.services.mail-send.title` })}
                    intro={intl.formatMessage({ id: `${messages}.services.mail-send.intro` })}
                    name="mail-send"
                    langs={["groovy", "javascript", "kotlin", "python", "ruby"]}
                  />
                </li>
                <li>
                  <ServiceSample
                    title={intl.formatMessage({ id: `${messages}.services.print-lines.title` })}
                    intro={intl.formatMessage({ id: `${messages}.services.print-lines.intro` })}
                    name="print-lines"
                    langs={["groovy", "javascript", "kotlin", "python", "ruby"]}
                  />
                </li>
                <li>
                  <ServiceSample
                    title={intl.formatMessage({ id: `${messages}.services.print-template.title` })}
                    intro={intl.formatMessage({ id: `${messages}.services.print-template.intro` })}
                    name="print-template"
                    langs={["groovy", "javascript", "kotlin", "python", "ruby"]}
                  />
                </li>
                <li>
                  <ServiceSample
                    title={intl.formatMessage({ id: `${messages}.services.query-parameter.title` })}
                    intro={intl.formatMessage({ id: `${messages}.services.query-parameter.intro` })}
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
                    title={intl.formatMessage({ id: `${messages}.services.db.title` })}
                    intro={intl.formatMessage({ id: `${messages}.services.db.intro` })}
                    name="db"
                    langs={["groovy", "javascript", "kotlin", "python", "ruby"]}
                  />
                </li>
              </ul>
            </div>
            <div className="col-md-3">
              <ul className="list-services__list">
                <li>
                  <ServiceSample
                    title={intl.formatMessage({ id: `${messages}.services.query-interaction.title` })}
                    intro={intl.formatMessage({ id: `${messages}.services.query-interaction.intro` })}
                    name="query-interaction"
                    params={{
                      id: 1
                    }}
                    langs={["groovy", "javascript", "kotlin", "python", "ruby"]}
                  />
                </li>
                <li>
                  <ServiceSample
                    title={intl.formatMessage({ id: `${messages}.services.query-result.title` })}
                    intro={intl.formatMessage({ id: `${messages}.services.query-result.intro` })}
                    name="query-result"
                    langs={["groovy", "javascript", "kotlin", "python", "ruby"]}
                  />
                </li>
                <li>
                  <ServiceSample
                    title={intl.formatMessage({ id: `${messages}.services.uid.title` })}
                    intro={intl.formatMessage({ id: `${messages}.services.uid.intro` })}
                    name="uid"
                    langs={["groovy", "javascript", "kotlin", "python", "ruby"]}
                  />
                </li>
                <li>
                  <ServiceSample
                    title={intl.formatMessage({ id: `${messages}.services.upload.title` })}
                    intro={intl.formatMessage({ id: `${messages}.services.upload.intro` })}
                    name="upload"
                    langs={["javascript", "kotlin"]}
                  />
                </li>
                <li>
                  <ServiceSample
                    title={intl.formatMessage({ id: `${messages}.services.remote-delete-json.title` })}
                    intro={intl.formatMessage({ id: `${messages}.services.remote-delete-json.intro` })}
                    name="remote-delete-json"
                    langs={["javascript", "kotlin"]}
                  />
                </li>
              </ul>
            </div>
            <div className="col-md-3">
              <ul className="list-services__list">
                <li>
                  <ServiceSample
                    title={intl.formatMessage({ id: `${messages}.services.remote-patch-json.title` })}
                    intro={intl.formatMessage({ id: `${messages}.services.remote-patch-json.intro` })}
                    name="remote-patch-json"
                    langs={["javascript", "kotlin"]}
                  />
                </li>
                <li>
                  <ServiceSample
                    title={intl.formatMessage({ id: `${messages}.services.remote-post-json.title` })}
                    intro={intl.formatMessage({ id: `${messages}.services.remote-post-json.intro` })}
                    name="remote-post-json"
                    langs={["javascript", "kotlin"]}
                  />
                </li>
                <li>
                  <ServiceSample
                    title={intl.formatMessage({ id: `${messages}.services.remote-put-json.title` })}
                    intro={intl.formatMessage({ id: `${messages}.services.remote-put-json.intro` })}
                    name="remote-put-json"
                    langs={["javascript", "kotlin"]}
                  />
                </li>
                <li>
                  <ServiceSample
                    title={intl.formatMessage({ id: `${messages}.services.remote-get-json.title` })}
                    intro={intl.formatMessage({ id: `${messages}.services.remote-get-json.intro` })}
                    name="remote-get-json"
                    langs={["javascript", "kotlin"]}
                  />
                </li>
                <li>
                  <ServiceSample
                    title={intl.formatMessage({ id: `${messages}.services.remote-mailjet-sms.title` })}
                    intro={intl.formatMessage({ id: `${messages}.services.remote-mailjet-sms.intro` })}
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

export default ListServices;
