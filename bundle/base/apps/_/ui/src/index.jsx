import React from "react";
import ReactDOM from "react-dom/client";
import _service from '@netuno/service-client';
import DashboardContainer from "./containers/DashboardContainer";

import { ConfigProvider, theme } from "antd";
import antLocale_enGB from "antd/lib/locale/en_GB";
import antLocale_enUS from "antd/lib/locale/en_US";
import antLocale_esES from "antd/lib/locale/es_ES";
import antLocale_ptBR from "antd/lib/locale/pt_BR";
import antLocale_ptPT from "antd/lib/locale/pt_PT";

_service.config({
  prefix: netuno.config.urlServices
});

const dashboardDiv = document.getElementById("app-dashboard");

const dashboardContainer = dashboardDiv ? ReactDOM.createRoot(dashboardDiv) : false;

if (dashboardContainer) {
  dashboardContainer.render(
    <ConfigProvider
      theme={{
        token: {
          colorPrimary: '#5b5ce1',
          colorLink: '#5b5ce1',
          borderRadius: 5,
        },
        algorithm: theme.darkAlgorithm
      }}
      locale={
        {
          'en_us': antLocale_enUS,
          'en_gb': antLocale_enGB,
          'es_es': antLocale_esES,
          'pt_br': antLocale_ptBR,
          'pt_pt': antLocale_ptPT
        }[netuno.config.langCode]
      }
    >
      <DashboardContainer/>
    </ConfigProvider>
  );
}

netuno.addNavigationLoad(() => {
  $('[netuno-navigation]').find('a').on('netuno:click', (e)=> {
    const link = $(e.target);
    if (dashboardContainer && link.is('[netuno-navigation-dashboard]')) {
      // Menu > Dashboard > Clicked!
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
  let modal = $('#app-dashboard-modal-form');
  modal.on('hidden.bs.modal', ()=> {
    modal.find('[netuno-form-edit]').empty();
  });
  $('#app-dashboard-modal-form-button').on('click', ()=> {
    modal.modal('show');
    netuno.loadFormEdit(modal.find('[netuno-form]'));
  });
  modal.find('[netuno-form]').on('netuno:save', ()=> {
    modal.modal('hide');
  });
});
