import React, { useEffect, useRef } from "react";

import { IntlProvider } from "react-intl";
import flatten from 'flat';

import DashboardContainer from "./containers/DashboardContainer";

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

function App() {
  const dashboardRef = useRef();
  useEffect(() => {
    const handleNavigationLoad = () => {
      $('[netuno-navigation]').find('a').on('netuno:click', (e) => {
        const link = $(e.target);
        if (dashboardRef.current && link.is('[netuno-navigation-dashboard]')) {
          // Memu > Dashboard > Clicked!
          dashboardRef.current.loadWorkers();
        }
      });
    };
    netuno.addNavigationLoad(handleNavigationLoad);
    return () => {
        netuno.removeNavigatoinLoad(handleNavigationLoad);
    };
  }, []);    
  return (
    <IntlProvider locale={locale} messages={flatten(messages[locale])}>
      <DashboardContainer ref={dashboardRef} />
    </IntlProvider>
  );
}

export default App;
