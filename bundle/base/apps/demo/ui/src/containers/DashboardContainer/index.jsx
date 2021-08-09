import React, { Component } from "react";
import { message } from 'antd';
import ListServices from "../ListServices/index.jsx";
import DataVisualization from "../DataVisualization/index.jsx";

import { injectIntl } from 'react-intl';
const messages = 'dashboardcontainer.datavisualization';

import styles from './index.less';

class DashboardContainer extends Component {
    constructor(props) {
        super(props);
        this.state = {
            workers: []
        };
    }

    componentDidMount() {
        this.loadWorkers();
    }

    loadWorkers() {
        netuno.service({
            url: '/services/workers',
            success: (data) => {
                this.setState({
                    workers: data.json
                });
            },
            fail: (data) => {
                console.log(data);
                message.error(this.props.intl.formatMessage({ id: `${messages}.loading_error` }))
            }
        });
    }

    render() {
        return (
            <div>
                <DataVisualization data={this.state.workers} />
                <ListServices />
            </div>
        );
    }
}

export default injectIntl(DashboardContainer)