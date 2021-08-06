import React, { Component } from "react";

import MyButton from "../../components/MyButton/index.jsx";

import "./index.less";

export default class DashboardContainer extends Component {
    constructor(props) {
        super(props);
        this.state = {
            counter: 0
        };
        this.button = React.createRef();
        this.click = this.click.bind(this);
    }

    componentDidUpdate(prevProps, prevState, snapshot) {
        $(this.button.current).fadeOut(250).fadeIn(250);
    }

    click() {
        this.setState({ counter: this.state.counter + 1 });
    }

    render() {
        const { counter } = this.state;
        return (
            <div className="my-dashboard">
                <div className="my-dashboard__button">
                    <MyButton mainRef={ this.button } text={ `ReactJS ⚡ Ant.Design 👉 Click me! ${ counter }` } click={ this.click } />
                </div>
            </div>
        );
    }
}
