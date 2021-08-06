
netuno.addPageLoad () ->
  $('#formSignIn').on 'submit', (e) ->
    e.preventDefault()
    $.ajax
      type: 'POST'
      url: "#{ netuno.config.urlAdmin }Index#{ netuno.config.extension }?action=login"
      data: {
        'username': $("#inputUserName").val()
        'password': $("#inputPassword").val()
      }
      success: (response) ->
        $('#login_result').html(response)

  if ($('#formSignIn').is('[netuno-login-auto="true"]'))
    $('#formSignIn').trigger('submit')
