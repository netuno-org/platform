import { h, Component } from "preact";

import CPU from '../../components/cpu';

import "./style.scss";

export default class Main extends Component {

    constructor() {
        super();
        this.state = {
        };
    }

    componentDidMount() {
    }

    render() {
        return (
            <div>
              <CPU />
            </div>
        );
    }
}
