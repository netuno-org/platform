import React, { useState, useEffect, useImperativeHandle } from "react";

import _service from '@netuno/service-client';

import { Spin, message } from 'antd';

import ListServices from "../ListServices";
import DataVisualization from "../DataVisualization";

import { useIntl } from 'react-intl';

import './index.less';

const messages = 'dashboardcontainer.datavisualization';

function DashboardContainer({}, ref) {
  const intl = useIntl();
  const [workers, setWorkers] = useState([]);
  const [loading, setLoading] = useState(false);

  useEffect(() => {
    loadWorkers();
  }, []);

  const loadWorkers = () => {
    setWorkers([]);
    setLoading(true);
    _service({
      url: intl.locale.indexOf('pt') == 0 ? '/services/trabalhadores' : '/services/workers',
      success: (response) => {
        setWorkers(response.json);
        setLoading(false);
      },
      fail: (e) => {
        setLoading(false);
        console.error('Workers service failed.', e);
        message.error(intl.formatMessage({ id: `${messages}.loading_error` }));
      }
    });
  };
  
  useImperativeHandle(ref, () => ({
    loadWorkers
  }));
  
  return (
    <div ref={ref}>
      { loading == false ?
        <DataVisualization data={workers} />
        : <Spin/>
      }
      <ListServices />
    </div>
  );
}

export default React.forwardRef(DashboardContainer);
