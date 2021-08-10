import React from "react";
import ReactDOM from "react-dom";

import { IntlProvider } from "react-intl";
import flatten from 'flat';

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

import DashboardContainer from "./containers/DashboardContainer/index.jsx";

const dashboardDiv = document.getElementById("app-dashboard");

const dashboardContainer = dashboardDiv ? ReactDOM.render(
    <IntlProvider locale={locale} messages={flatten(messages[locale])}>
        <DashboardContainer />
    </IntlProvider>
    , dashboardDiv) : false;

netuno.addNavigationLoad(() => {
    $('[netuno-navigation]').find('a').on('netuno:click', (e) => {
        const link = $(e.target);
        if (dashboardContainer && link.is('[netuno-navigation-dashboard]')) {
            // Memu > Dashboard > Clicked!
            dashboardContainer.loadTrabalhadores();
        }
    });
});

netuno.addContentLoad((container) => {
    // When any content is loaded dinamically this is executed...
    if (container.is('[netuno-form-search="YOUR_FORM_NAME"]')) {
        // When search page is loaded...
    } else if (container.is('[netuno-form-edit="YOUR_FORM_NAME"]')) {
        // When form edit is loaded...
    }
});

netuno.addPageLoad(() => {
    // When page is loaded...
    let modal = $('#app-dashboard-modal-form')
    modal.on('hidden.bs.modal', () => {
        modal.find('[netuno-form-edit]').empty()
    });
    $('#app-dashboard-modal-form-button').on('click', () => {
        modal.modal('show')
        netuno.loadFormEdit(modal.find('[netuno-form]'))
    });
    modal.find('[netuno-form]').on('netuno:save', () => {
        modal.modal('hide')
    });
});
