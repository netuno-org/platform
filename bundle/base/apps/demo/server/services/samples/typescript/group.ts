/**
 *
 *  EN: GROUP
 *  EN: Show information about the group of user logged.
 *
 *  PT: GRUPO
 *  PT: Apresenta a informação do grupo do utilizador logado.
 *
 */
import {_group, _template, _val} from "@netuno/server-types";

const data = _val.map();

data.set("title", "This is your group...");
data.set("id", _group.id);
data.set("name", _group.name);
data.set("code", _group.code);

data.set("full", _group.data());

_template.output("samples/identity", data);
