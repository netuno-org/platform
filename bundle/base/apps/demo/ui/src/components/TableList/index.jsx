import React from 'react';

import { FormattedMessage } from 'react-intl';

import {Table} from 'antd';

import './index.less';

const messages = 'dashboardcontainer.datavisualization.table';

function TableList({data = []}) {
  const columns = [
      {
        title: <FormattedMessage id={`${messages}.workers`} />,
        dataIndex: 'name',
      },
      {
        title: <FormattedMessage id={`${messages}.total`} />,
        dataIndex: 'total',
      }
  ];
  return (
    <Table columns={columns} dataSource={data} pagination={false} bordered={true} />
  );
}

export default TableList;
