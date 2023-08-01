import React, { useState, useEffect, useRef } from "react";

import MyButton from "../../components/MyButton";

import "./index.less";

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
    <div className="my-dashboard">
      <div ref={refButton} className="my-dashboard__button">
        <MyButton text={`ReactJS ⚡ Ant.Design 👉 Click me! ${counter}`} onClick={onClick} />
      </div>
    </div>
  );
}

export default DashboardContainer;
