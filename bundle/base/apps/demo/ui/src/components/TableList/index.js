import React from 'react';

import { FormattedMessage } from 'react-intl';

import './index.less';

const messages = 'dashboardcontainer.datavisualization.table';

function TableList({data = []}) {
  return (
    <table className="table-list">
      <thead>
        <tr>
          <th><FormattedMessage id={`${messages}.workers`} /></th>
          <th><FormattedMessage id={`${messages}.total`} /></th>
        </tr>
      </thead>
      <tbody>
        {
          data.map(
            (record, i) =>
            <tr key={i}>
              <td>{record.name}</td>
              <td>{record.total}</td>
            </tr>
          )
        }
      </tbody>
    </table>
  );
}

export default TableList;
