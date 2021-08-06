
class Netuno
  config: {}
  pageLoads: []
  contentLoads: []
  navigationLoads: []

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

  addNavigationLoad: (func) ->
    @navigationLoads.push func

  navigationLoaded: () ->
    for navigationLoad in @navigationLoads
      navigationLoad()

  urlWithParams: (url, obj)->
    params = Object.keys(obj).reduce((a, k) ->
      v = encodeURIComponent(obj[k])
      a.push("#{k}=#{v}")
      return a
    , []).join('&')
    str = "#{url}?#{params}"
    return str

  service: (args) ->
    settings = {
      url: ''
      method: 'GET'
      credentials: 'include',
      headers: {
        'Content-Type': 'application/json',
        'Accept':  'application/json'
      },
      success: (data)->
      fail: (data)->
    }
    $.extend(settings, args)
    if (settings.method == 'GET' && settings.data)
      settings.url = @.urlWithParams(settings.url, settings.data)
    else if (settings.data && settings.headers['Content-Type'] == 'application/json')
      settings.body = JSON.stringify(settings.data)
    fetch(settings.url, settings).then(
      (response) ->
        if (response.ok)
          if (response.status == 204)
            settings.success()
          else
            contentType = response.headers.get("Content-Type")
            if (contentType && contentType.toLowerCase().indexOf("application/json") == 0)
              return response.json().then((data) ->
                settings.success {
                  json: data
                }
              )
            else
              return response.text().then((text) ->
                settings.success {
                  text: text
                }
              )
        else
          settings.fail {
            response: response
          }
    ).catch(
      (e)->
        settings.fail {
          error: e
        }
    )

`this.netuno = new Netuno()`