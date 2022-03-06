import React, { useState, useEffect } from 'react';
import { Bar } from 'react-chartjs-2';
import { Chart, registerables } from 'chart.js';

import { useIntl } from 'react-intl';

import './index.less';

const messages = 'dashboardcontainer.datavisualization.chart';

Chart.register(...registerables);

function ChartBar({data}) {
  const intl = useIntl();
  
  const [chartData, setChartData] = useState(null);
  
  useEffect(() => {
    const colors = [
      'rgb(255, 99, 132)',
      'rgb(255, 159, 64)',
      'rgb(255, 205, 86)',
      'rgb(75, 192, 192)',
      'rgb(54, 162, 235)',
      'rgb(153, 102, 255)',
      'rgb(201, 203, 207)'
    ];
    const labels = [];
    const values = [];
    const chartDataNew = {
      labels: [],
      datasets: [
        {
          label: intl.formatMessage({ id: `${messages}.label` }),
          data: [],
          backgroundColor: []
        }
      ]
    };
    for (const i in data) {
      labels.push(data[i].name);
      values.push(data[i].total);
      chartDataNew.datasets[0].backgroundColor.push(colors[(i + 1) % 7]);
    }
    chartDataNew.labels = labels;
    chartDataNew.datasets[0].data = values;

    setChartData(chartDataNew);
  }, [data]);
  
  if (chartData == null) {
    return null;
  }
  
  return (
    <Bar data={chartData} />
  );
}

export default ChartBar;
