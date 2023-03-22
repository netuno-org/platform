import React, { useState, useEffect, useRef } from "react";

import { ConfigProvider, theme } from "antd";

import antLocale_enGB from "antd/lib/locale/en_GB";
import antLocale_enUS from "antd/lib/locale/en_US";
import antLocale_esES from "antd/lib/locale/es_ES";
import antLocale_ptBR from "antd/lib/locale/pt_BR";
import antLocale_ptPT from "antd/lib/locale/pt_PT";

import MyButton from "../../components/MyButton";

import "./index.less";

const antdVariables = {
  colorPrimary: '#5b5ce1',
  colorLink: '#5b5ce1',
  borderRadius: 5,
};

function DashboardContainer() {
  const [counter, setCounter] = useState(0);

  const refButton = useRef();

  useEffect(() => {
    $(refButton.current).fadeOut(250).fadeIn(250);
  }, [counter]);

  const onClick = () => {
    setCounter(counter + 1);
  };

  return (
    <ConfigProvider
      theme={{
        token: antdVariables,
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
      <div className="my-dashboard">
        <div ref={refButton} className="my-dashboard__button">
          <MyButton text={`ReactJS ⚡ Ant.Design 👉 Click me! ${counter}`} onClick={onClick} />
        </div>
      </div>
    </ConfigProvider>
  );
}

export default DashboardContainer;
