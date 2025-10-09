
class Netuno
  config: {}
  pageLoads: []
  contentLoads: []

  addPageLoad: (func) ->
    @pageLoads.push func

  pageLoaded: () ->
    for pageLoad in @pageLoads
      pageLoad()

  addContentLoad: (func) ->
    @contentLoads.push func

  contentLoaded: (container) ->
    for contentLoad in @contentLoads
      contentLoad(container)

`this.netuno = new Netuno()`