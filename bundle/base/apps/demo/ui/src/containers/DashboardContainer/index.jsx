import React, { Component } from "react";

import message from 'antd/lib/message';
import Spin from 'antd/lib/spin';

import ListServices from "../ListServices/index.jsx";
import DataVisualization from "../DataVisualization/index.jsx";

import { injectIntl } from 'react-intl';
const messages = 'dashboardcontainer.datavisualization';

import styles from './index.less';

class DashboardContainer extends Component {
    constructor(props) {
        super(props);
        this.state = {
            workers: [],
            loading: false
        };
        this.loadWorkers = this.loadWorkers.bind(this);
    }

    componentDidMount() {
        this.loadWorkers();
    }

    loadWorkers() {
        this.setState({ workers: [], loading: true });
        netuno.service({
            url: this.props.intl.locale.indexOf('pt') == 0 ? '/services/trabalhadores' : '/services/workers',
            success: (data) => {
                this.setState({
                    workers: data.json,
                    loading: false
                });
            },
            fail: (data) => {
                this.setState({ loading: false });
                console.log(data);
                message.error(this.props.intl.formatMessage({ id: `${messages}.loading_error` }));
            }
        });
    }

    render() {
        const { loading } = this.state;
        return (
            <div>
              { loading == false ?
                <DataVisualization data={this.state.workers} />
                : <Spin/>
              }
              <ListServices />
            </div>
        );
    }
}

export default injectIntl(DashboardContainer, {forwardRef: true});
