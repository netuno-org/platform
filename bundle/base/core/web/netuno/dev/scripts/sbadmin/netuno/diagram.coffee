

class NetunoDiagram
  constructor: (container) ->
    container.on "load", ()->
      container.fadeIn("slow")

`window.NetunoDiagram = NetunoDiagram`
