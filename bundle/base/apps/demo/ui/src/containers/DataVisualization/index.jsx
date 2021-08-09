import React, { Component } from 'react';

import ChartBar from "../../components/ChartBar/index.jsx";
import TableList from "../../components/TableList/index.jsx";

import styles from './index.less';

import { FormattedMessage } from 'react-intl';
const messages = 'dashboardcontainer.datavisualization';

class DataVisualization extends Component {

    constructor(props) {
        super(props);
        this.state = {
            data: []
        };
    }

    static getDerivedStateFromProps(nextProps, prevState) {
        if (nextProps.data) {
            return {
                data: nextProps.data
            };
        }
        return null;
    }

    render() {

        return (
            <div>
                <div className="row">
                    <div className="col-lg-6">
                        <div className="panel panel-default">
                            <div className={`panel-heading ${styles.panel}`}>
                                <h3 className="panel-title">
                                    <FormattedMessage id={`${messages}.table.title`} />
                                </h3>
                            </div>
                            <div className="panel-body">
                                <TableList tableData={this.props.data} />
                            </div>
                        </div>
                    </div>
                    <div className="col-lg-6">
                        <div className="panel panel-default">
                            <div className={`panel-heading ${styles.panel}`}>
                                <h3 className="panel-title">
                                    <FormattedMessage id={`${messages}.chart.title`} />
                                </h3>
                            </div>
                            <div className="panel-body">
                                <ChartBar chartData={this.props.data} displayLegend="false" legendPosition="top" />
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        );
    }
}

export default DataVisualization;
