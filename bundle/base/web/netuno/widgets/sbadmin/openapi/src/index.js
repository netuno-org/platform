import React from "react";
import ReactDOM from "react-dom";

import SwaggerUI from "swagger-ui-react";
import "swagger-ui-react/swagger-ui.css";

const App = () => (
  <SwaggerUI
    url="_openapi.json"
  />
);


const rootElement = document.getElementById("root");
ReactDOM.render(<App />, rootElement);
