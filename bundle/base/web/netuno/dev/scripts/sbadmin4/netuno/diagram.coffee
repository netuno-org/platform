

class NetunoDiagram
  constructor: (container) ->
    container.load ()->
      container.show()
    $("[netuno-dev-content=\"#{ netuno.config.urlBase }dev/Diagram#{ netuno.config.extension }\"]").on 'click', () ->
      container.attr( 'src', (i, val) -> val)
      container.fadeOut('slow')


`window.NetunoDiagram = NetunoDiagram`