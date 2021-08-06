let poly = require("preact-cli/lib/lib/webpack/polyfills");

import { h } from "preact";
import habitat from "preact-habitat";

//import Widget from "./components/hello-world";
import Main from "./containers/main";

let _habitat = habitat(Main);

_habitat.render({
  selector: '[data-widget-host="habitat"]',
  clean: true
});
