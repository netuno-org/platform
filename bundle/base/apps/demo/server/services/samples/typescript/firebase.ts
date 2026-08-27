
/**
 *
 *  EN: FIREBASE
 *  EN: Manage data on Firebase database.
 *
 *  PT: FIREBASE
 *  PT: Gere os dados da base de dados Firebase.
 *
 */

import {_firebase, _uid, _val} from "@netuno/server-types";

_firebase.setValue("netuno-sample",
    _val.map()
      .set("id", 1)
      .set("name", "Netuno Sample")
      .set("active", true)
);

// _firebase.removeValue("netuno-sample")

const uid = _uid.generate();
_firebase.setValue("netuno-sample-item", uid,
    _val.map()
      .set("name", "Netuno Sample Item")
);

// _firebase.removeValue("netuno-sample-item", uid)
