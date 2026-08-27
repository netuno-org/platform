
/**
 *
 *  EN: RETURN LINES AS TEXT
 *
 *  PT: RETORNA LINHAS COMO TEXTO
 *
 */

import {_header, _out} from "@netuno/server-types";

_header.contentType("text/plain");

_out.println("line number: 1");

_out.print("line number: ");
_out.print(2);
_out.println();
