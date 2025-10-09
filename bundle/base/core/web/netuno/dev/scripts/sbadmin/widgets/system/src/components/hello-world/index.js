import { h, Component } from "preact";

import MetricsGraphics from 'react-metrics-graphics';
import 'metrics-graphics/dist/metricsgraphics.css';

import "./style.scss";

export default class App extends Component {

  constructor() {
      super();
      this.state = {
          data: []
      };
  }

  componentDidMount() {
      const chartUpdate = ()=> {
          const { data } = this.state;
          data.push({date: new Date(), value: parseInt(Math.random() * 100)});
          this.setState({ data });
          setTimeout(chartUpdate, 1000);
      };
      chartUpdate();
  }

  render(props, { data }) {
    return (
      <div>
        <MetricsGraphics
	      title="Processador"
	      description="This graphic shows a time-series of downloads."
	      data={ data }
	      width={ 600 }
	      height={ 250 }
	      x_accessor="date"
	      y_accessor="value"
        />
        <h1 style={{ color: props.color }}>Hello, World!</h1>
      </div>
    );
  }
}
