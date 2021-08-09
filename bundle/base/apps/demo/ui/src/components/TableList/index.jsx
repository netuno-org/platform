import React, { Component } from 'react';

import styles from './index.less';

import { FormattedMessage } from 'react-intl';
const messages = 'dashboardcontainer.datavisualization.table';

class TableList extends Component {

    constructor(props) {
        super(props);
        this.state = {
            tableData: []
        };
    }

    static getDerivedStateFromProps(nextProps, prevState) {
        if (nextProps.tableData) {
            return {
                tableData: nextProps.tableData
            };
        }
        return null;
    }

    render() {
        const tableFinalData = this.state.tableData.map(
            (record, i) =>
                <tr key={i}>
                    <td>{record.name}</td>
                    <td>{record.total}</td>
                </tr>
        );

        return (
            <table className={styles.table}>
                <thead>
                    <tr>
                        <th><FormattedMessage id={`${messages}.workers`} /></th>
                        <th><FormattedMessage id={`${messages}.total`} /></th>
                    </tr>
                </thead>
                <tbody>
                    {tableFinalData}
                </tbody>
            </table>
        );
    }
}

export default TableList;
