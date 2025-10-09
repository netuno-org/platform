
netuno.addPageLoad () ->
  $('#formSignIn').on 'submit', (e) ->
    e.preventDefault()
    $.ajax
      type: 'POST'
      url: "#{ netuno.config.urlBase }Index#{ netuno.config.extension }?action=login"
      data: {
        'netuno_user': $("#inputUserName").val()
        'netuno_pass': $("#inputPassword").val()
      }
      success: (response) ->
        $('#login_result').html(response)

  if ($('#formSignIn').is('[netuno-login-auto="true"]'))
    $('#formSignIn').trigger('submit')
