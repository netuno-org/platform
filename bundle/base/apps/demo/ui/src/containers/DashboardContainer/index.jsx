import React, { Component } from "react";
import { message } from 'antd';
import ListServices from "../ListServices/index.jsx";
import DataVisualization from "../DataVisualization/index.jsx";

import styles from './index.less';

export default class DashboardContainer extends Component {
    constructor(props) {
        super(props);
        this.state = {
            trabalhadores: []
        };
    }

    componentDidMount() {
        this.loadTrabalhadores();
    }

    loadTrabalhadores() {
        netuno.service({
            url: '/services/trabalhadores',
            success: (data)=> {
                this.setState({
                    trabalhadores: data.json
                });
            },
            fail: (data)=> {
                console.log(data);
                message.error("Falha ao carregar a lista do total de registos dos trabalhadores.")
            }
        });
    }

    render() {
        return (
            <div>
                <DataVisualization data={this.state.trabalhadores} title={"Trabalhadores"} />
                <ListServices />
            </div>
        );
    }
}
