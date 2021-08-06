
_out.println(_req.post().getString("key1"))

_out.println(_req.post().asValues("arr").getString(1))

_out.println(_req.post().asValues("obj").getBoolean("key"))
