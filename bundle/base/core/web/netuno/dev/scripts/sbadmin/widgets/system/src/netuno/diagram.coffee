

class NetunoDiagram
  constructor: (container) ->
    console.log($("[netuno-dev-content=\"#{ netuno.config.urlAdmin }dev/Diagram#{ netuno.config.extension }\"]"))
    container.load ()->
      container.show()
    $("[netuno-dev-content=\"#{ netuno.config.urlAdmin }dev/Diagram#{ netuno.config.extension }\"]").on 'click', () ->
      container.attr( 'src', (i, val) -> val)
      container.fadeOut('slow')


`window.NetunoDiagram = NetunoDiagram`