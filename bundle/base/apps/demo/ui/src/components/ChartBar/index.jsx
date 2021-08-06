import React, { Component } from 'react';
import { Bar } from 'react-chartjs-2';

import styles from './index.less';

export default class ChartBar extends Component {

    constructor(props) {
        super(props);
        this.state = {
            chartData: {}
        }
    }

    static getDerivedStateFromProps(nextProps, prevState){
        if (nextProps.chartData) {
            let colors = [
                'rgb(255, 99, 132)',
                'rgb(255, 159, 64)',
                'rgb(255, 205, 86)',
                'rgb(75, 192, 192)',
                'rgb(54, 162, 235)',
                'rgb(153, 102, 255)',
                'rgb(201, 203, 207)'
            ]

            let labels = [];
            let values = [];
            let chartDataValues = {
                labels: [],
                datasets: [
                    {
                        label: "Horas",
                        data: [],
                        backgroundColor: []
                    }
                ]
            };

            for (const i in nextProps.chartData) {
                labels.push(nextProps.chartData[i].nome);
                values.push(nextProps.chartData[i].total);
                chartDataValues.datasets[0].backgroundColor[i] = colors[i];
            }

            chartDataValues.labels = labels;
            chartDataValues.datasets[0].data = values;

            return {chartData: chartDataValues};
        }
        return null;
    }

    render() {
        return (
            <Bar
                data={this.state.chartData}
                options={{
                    title: {
                        display: this.props.displayTitle
                    },
                    legend: {
                        display: this.props.displayLegend,
                        position: this.props.legendPosition
                    },
                    scales: {
                        yAxes: [{
                            display: true,
                            ticks: {
                                suggestedMin: 0,
                            }
                        }]
                    }
                }}
            />
        );
    }
}
