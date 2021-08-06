
/**
 *
 *  EN: FIREBASE
 *  EN: Manage data on Firebase database.
 *
 *  PT: FIREBASE
 *  PT: Gere os dados da base de dados Firebase.
 *
 */

_firebase.setValue("netuno-sample",
    _val.init()
    .set("id", 1)
    .set("name", "Netuno Sample")
    .set("active", true)
)

// _firebase.removeValue("netuno-sample")

var uid = _uid.generate()
_firebase.setValue("netuno-sample-item", uid,
    _val.init()
    .set("name", "Netuno Sample Item")
)

// _firebase.removeValue("netuno-sample-item", uid)
