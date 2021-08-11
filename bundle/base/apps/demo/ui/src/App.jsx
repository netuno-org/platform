import React, { Component } from "react";

import { IntlProvider } from "react-intl";
import flatten from 'flat';

import DashboardContainer from "./containers/DashboardContainer/index.jsx";

import messages_en_gb from "../lang/en_GB.json";
import messages_en_us from "../lang/en_US.json";
import messages_pt_br from "../lang/pt_BR.json";
import messages_pt_pt from "../lang/pt_PT.json";

const messages = {
    'en-gb': messages_en_gb,
    'en-us': messages_en_us,
    'pt-br': messages_pt_br,
    'pt-pt': messages_pt_pt
};

const locale = (netuno.config.langCode).replace("_", "-");

export default class App extends Component {
    constructor(props) {
        super(props);
        this.state = {
        };
        this.dashboardRef = null;
    }

    componentDidMount() {
        this.navigationLoad = () => {
            $('[netuno-navigation]').find('a').on('netuno:click', (e) => {
                const link = $(e.target);
                if (this.dashboardRef && link.is('[netuno-navigation-dashboard]')) {
                    // Memu > Dashboard > Clicked!
                    this.dashboardRef.loadWorkers();
                }
            });
        };
        netuno.addNavigationLoad(this.navigationLoad);
    }

    componentWillUnmount() {
        netuno.removeNavigatoinLoad(this.this.navigationLoad);
    }
    
    render() {
        return (
            <IntlProvider locale={locale} messages={flatten(messages[locale])}>
              <DashboardContainer ref={ref => this.dashboardRef = ref} />
            </IntlProvider>
        );
    }
}
