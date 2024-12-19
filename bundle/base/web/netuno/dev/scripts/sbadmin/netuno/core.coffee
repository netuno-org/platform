
toastr.options = {
  "closeButton": true,
  "debug": false,
  "newestOnTop": true,
  "progressBar": false,
  "positionClass": "netuno-toast-top-center",
  "preventDuplicates": true,
  "onclick": null,
  "showDuration": "300",
  "hideDuration": "1000",
  "timeOut": "3000",
  "extendedTimeOut": "1000",
  "showEasing": "swing",
  "hideEasing": "linear",
  "showMethod": "fadeIn",
  "hideMethod": "fadeOut"
}

$ ->
  $('form').submit ->
    false
  return

netuno.menuDev = (menu)->
  navbarNav = $('.navbar-nav')
  navbarNav.find('[netuno-menu-form-section]').remove()
  navbarNav.find('[netuno-menu-report-section]').remove()
  navbarNav.find('[netuno-menu-form-item]').remove()
  navbarNav.find('[netuno-menu-report-item]').remove()
  containers = $('#containers')
  containers.find('[netuno-form]').remove()
  containers.find('[netuno-report]').remove()
  navMenuHtml = ''
  containersHtml = ''
  buildMenu = (type, parentUId, items, level) ->
    menuHtml = '';
    if level > 0
      menuHtml += "<ul>"
    for item in items
      expand = ""
      if item.items.length > 0
        expand = " data-toggle"
      menuHtml += "<li netuno-menu-#{ type }-item>"
      menuHtml += "<a href=\"\#\" netuno-dev-#{ type }=\"#{ item.name }\"#{ expand }>"
      menuHtml += "#{ item.text }"
      if item.items.length > 0
        menuHtml += "<i class=\"fa fa-fw fa-caret-down\"></i>"
      menuHtml += "</a>"
      if item.items.length > 0
        menuHtml += buildMenu(type, item.uid, item.items, level + 1)
      menuHtml += '</li>'
      containersHtml += "<div id=\"netuno_#{ type }_design_#{ item.name }\" netuno-#{ type } netuno-#{ type }-uid=\"#{ item.uid }\" netuno-#{ type }-name=\"#{ item.name }\">"
      containersHtml += "</div>"
    if level > 0
      menuHtml += '</ul>'
    return menuHtml
  if menu.forms.length > 0
    navMenuHtml += "<li netuno-menu-form-section class=\"nav-section-title\"><h4>&nbsp;<i class=\"fa fa-fw fa-edit\"></i>#{ netuno.lang['menu.forms'] }</h4></li>"
    navMenuHtml += buildMenu 'form', 0, menu.forms, 0
  if menu.reports.length > 0
    navMenuHtml += "<li netuno-menu-report-section class=\"nav-section-title\"><h4>&nbsp;<i class=\"fa fa-fw fa-area-chart\"></i>#{ netuno.lang['menu.reports'] }</h4></li>"
    navMenuHtml += buildMenu 'report', 0, menu.reports, 0
  navbarNav.append(navMenuHtml).show().find('ul').hide()
  containers.append(containersHtml)
  netuno.loadDevLinks(navbarNav)

netuno.loadDevMenu = () ->
  $.ajax(
    type: 'POST'
    url: "#{ netuno.config.urlAdmin }dev/Main#{ netuno.config.extension }"
    data: { 'service': 'json' }
    success: (response) ->
      netuno.menuDev(response)
  )

netuno.loadDevForm = (name) ->
  container = $("\#netuno_form_design_#{ name }")
  if container.is(':empty')
    $.ajax(
      type: 'POST'
      url: "#{ netuno.config.urlAdmin }dev/FormDesign#{ netuno.config.extension }"
      data: { 'netuno_table_uid': container.attr('netuno-form-uid') }
      success: (response) ->
        container.html(response)
        netuno.contentLoaded(container)
        containerForm = container.find("form[name=managementForm]")
        if (containerForm.length > 0)
          netuno.loadValidation(containerForm)
    )
  container.show()

netuno.loadDevFormField = (name, uid) ->
  container = $("\#netuno_form_design_#{ name }")
  $.ajax(
    type: 'POST'
    url: "#{ netuno.config.urlAdmin }dev/FormDesign#{ netuno.config.extension }"
    data: {
      'netuno_table_uid': container.attr('netuno-form-uid'),
      'uid': uid
    }
    success: (response) ->
      container.html(response);
      netuno.contentLoaded(container)
      containerForm = container.find("form[name=formDesign_#{ name }]")
      if (containerForm.length > 0)
        netuno.loadValidation(containerForm)
  )
  container.show()

netuno.loadDevReport = (name) ->
  container = $("\#netuno_report_design_#{ name }")
  if container.is(':empty')
    $.ajax(
      type: 'POST'
      url: "#{ netuno.config.urlAdmin }dev/ReportDesign#{ netuno.config.extension }"
      data: { 'netuno_table_uid': container.attr('netuno-report-uid') }
      success: (response) ->
        container.html(response);
        netuno.contentLoaded(container)
        containerForm = container.find("form[name=managementReport]")
        if (containerForm.length > 0)
          netuno.loadValidation(containerForm)
    )
  container.show()

netuno.loadDevReportField = (name, uid) ->
  container = $("\#netuno_report_design_#{ name }")
  $.ajax(
    type: 'POST'
    url: "#{ netuno.config.urlAdmin }dev/ReportDesign#{ netuno.config.extension }"
    data: {
      'netuno_table_uid': container.attr('netuno-report-uid'),
      'uid': uid
    }
    success: (response) ->
      container.html(response);
      netuno.contentLoaded(container)
      containerForm = container.find("form[name=reportDesign_#{ name }]")
      if (containerForm.length > 0)
        netuno.loadValidation(containerForm)
  )
  container.show()


netuno.loadDevLinks = (container)->
  container.find("a[netuno-dev-content],a[netuno-dev-form],a[netuno-dev-report]").off('click').on "click", ()->
    element = $(this)
    $('.navbar-toggle:visible').click()
    if element.is("[data-toggle]")
      ul = element.parent().children('ul')
      if ul.is(':hidden')
        ul.fadeIn()
        element.children('i:last-child').removeClass('fa-caret-down').addClass('fa-caret-up')
      else
        ul.fadeOut()
        element.children('i:last-child').removeClass('fa-caret-up').addClass('fa-caret-down')
    if element.is("[netuno-dev-content]")
      container = $(element.attr("href"))
      $("#containers > div").hide()
      if (container.length > 0 and container.is(":empty")) or element.is("[netuno-dev-reload]")
        container.empty()
        $.ajax
          url: element.attr('netuno-dev-content'),
          success: (response) ->
            container.html(response);
            netuno.contentLoaded(container)
      container.show()
      return false
    else if element.is("[netuno-dev-form]")
      $("#containers > div").hide()
      netuno.loadDevForm(element.attr("netuno-dev-form"))
      return false
    else if element.is("[netuno-dev-report]")
      $("#containers > div").hide()
      netuno.loadDevReport(element.attr("netuno-dev-report"))
      return false

netuno.submitDev = (containerId, formId, validation, callback) ->
  container =  $("\##{ containerId }")
  form = $("##{ formId }")
  if form.validate().valid() is false
    form.ajaxForm().submit()
  if validation is false or form.validate().valid()
    if validation is false
      form.validate().cancelSubmit = true;
    form.ajaxForm(
      url: form.attr("action")
      iframe: true
      success: (response) ->
        container.html(response)
        netuno.contentLoaded(container)
        netuno.loadValidationDev(formId)
        callback?()
    ).submit()
    if validation is false or form.validate().valid()
      return true
  return false

netuno.loadValidationDev = (id) ->
  form = $("\##{ id }")
  rules = {}
  form.find("[validation]").each ()->
    element = $(this)
    validation = S(element.attr("validation"))
    rules[element.attr("name")] = {}
    if validation.contains("required")
      rules[element.attr("name")]["required"] = true
    if validation.contains("email")
      rules[element.attr("name")]["email"] = true
    if validation.contains("sqlname")
      rules[element.attr("name")]["sqlname"] = true
  form.validate({
    "errorElement": 'span'
    "errorClass": 'help-block'
    "focusInvalid": false
    "ignore": ""
    "rules": rules
    "invalidHandler": (event, validator) ->
    "highlight": (element) ->
      $(element).closest('.form-group').addClass('has-error')
    "success": (label) ->
      label.closest('.form-group').removeClass('has-error')
      label.remove();
    "errorPlacement": (error, element) ->
      error.insertAfter(element.closest('.input-icon'))
  })

netuno.loadCodeEditor = (container) ->
  container.find("textarea[code-editor]").each ()->
    element = $(this)
    editor = CodeMirror.fromTextArea(element[0], {
      value: element[0].value,
      mode: element.attr('code-editor'),
      indentWithTabs: true,
      smartIndent: true,
      lineNumbers: true,
      matchBrackets: true,
      autofocus: true
    })
    element.data(
        editor: editor
    )

netuno.addContentLoad (container)->
  netuno.loadDevLinks(container)
  netuno.loadCodeEditor(container)
  container.find("select").select2(
    theme: "bootstrap"
    placeholder: ""
    maximumSelectionSize: 6
    allowClear: true
  )

netuno.addPageLoad ()->
  netuno.loadDevLinks($("body"))
  netuno.loadCodeEditor($("body"))

netuno.nameAutocompleteDev = {
  checkboxesIds: []
  changing: false
  load: (fieldsPrefix, requestUUID)->
    checkboxId = "dev_name_autocomplete_#{requestUUID}"
    checkbox = $("\##{checkboxId}")
    netuno.nameAutocompleteDev.clearCheckboxesIds()
    netuno.nameAutocompleteDev.checkboxesIds.push(checkboxId)
    checkbox.prop("checked", localStorage.getItem("dev:name:autocomplete") isnt "false")
    $("\##{fieldsPrefix}_displayName").on("keyup", ()->
      if localStorage.getItem("dev:name:autocomplete") is "false"
        return
      that = $(this)
      fieldName = $("\##{fieldsPrefix}_name")
      fieldName.val(S(that.val()).latinise().camelize().dasherize().slugify().replaceAll("-", "_").chompLeft("_").s)
      fieldNameVal = fieldName.val()
      if $("\##{fieldsPrefix}_type").length > 0 and $("\##{fieldsPrefix}_type").val() == "select"
        endsWithId = fieldNameVal.indexOf("_id") == fieldNameVal.length - 3;
        if !endsWithId
          fieldName.val(fieldNameVal + "_id")
    )
  clearCheckboxesIds: ()->
    netuno.nameAutocompleteDev.checkboxesIds.forEach((checkboxId, i)->
      checkbox = $("\##{checkboxId}")
      if checkbox.length is 0
        netuno.nameAutocompleteDev.checkboxesIds.splice(i, 1)
    )
  onChange: (element)->
    if netuno.nameAutocompleteDev.changing
      return
    netuno.nameAutocompleteDev.changing = true
    element = $(element)
    checked = element.is(":checked")
    localStorage.setItem("dev:name:autocomplete", "#{checked}")
    netuno.nameAutocompleteDev.checkboxesIds.forEach((checkboxId, i)->
      if checkboxId == element.prop("id")
        return
      checkbox = $("\##{checkboxId}")
      if checkbox.length > 0
        checkbox.prop("checked", checked)
        checkbox.trigger("change")
    )
    netuno.nameAutocompleteDev.changing = false
}

netuno.componentConfig = {
  load: (netunoType, netunoUid)->
    NetunoType = ''
    if netunoType == 'form'
      NetunoType = 'Form'
    if netunoType == 'report'
      NetunoType = 'Report'
    netunoContainer = $("[netuno-#{ netunoType }-uid=\"#{ netunoUid }\"]")
    netunoName = netunoContainer.attr("netuno-#{ netunoType }-name")
    fieldUid = netunoContainer.find("\#field#{ NetunoType }Design_#{ netunoName }_uid").val()
    componentType = netunoContainer.find("\#field#{ NetunoType }Design_#{ netunoName }_type").val()
    componentConfigContainer = $("\#field#{ NetunoType }Design_#{ netunoName }_componentConfiguration")
    componentConfigContainer.load("#{ netuno.config.urlAdmin }dev/ComponentConfiguration#{ netuno.config.extension }", {
      component: componentType
      uid: fieldUid
      netuno_table_uid: netunoUid
    }, ()->
      netuno.contentLoaded(componentConfigContainer)
    )
  link: {
    popup: null,
    callbackItem: null,
    report: false
    select: (netunoTableUid, callbackItem)->
      netuno.componentConfig.link.callbackItem = callbackItem
      netuno.componentConfig.link.popup = $("#componentConfig#{ netunoTableUid }LinkModalSelect")
      netuno.componentConfig.link.report = netuno.componentConfig.link.popup.attr('data-report') == ''
      tableUid = netuno.componentConfig.link.popup.attr('data-table-uid')
      if not tableUid?
        tableUid = ''
      netuno.componentConfig.link.popup.find("[data-netuno-back]").attr('data-mode', 'select').removeAttr('data-table-uid').removeAttr('data-parameter-key')
      netuno.componentConfig.link.popup.find(".modal-body").empty().load("#{ netuno.config.urlAdmin }dev/Link#{ netuno.config.extension }?mode=select&report=#{netuno.componentConfig.link.report}", netuno.componentConfig.link.contentLoaded)
      netuno.componentConfig.link.popup.find("[data-netuno-back]").hide()
      netuno.componentConfig.link.popup.modal("show")
    ,
    configure: (netunoTableUid, parameterKey, callbackItem)->
      netuno.componentConfig.link.callbackItem = callbackItem
      netuno.componentConfig.link.popup = $("#componentConfig#{ netunoTableUid }LinkModal_#{ parameterKey }")
      tableUid = netuno.componentConfig.link.popup.attr('data-table-uid')
      if not tableUid?
        tableUid = ''
      netuno.componentConfig.link.popup.find("[data-netuno-back]").attr('data-table-uid', netunoTableUid).attr('data-parameter-key', parameterKey)
      netuno.componentConfig.link.popup.find(".modal-body").empty().load("#{ netuno.config.urlAdmin }dev/Link#{ netuno.config.extension }?mode=add&netuno_table_uid=#{ netunoTableUid }&parameter_key=#{ parameterKey }&table_uid=#{ tableUid }&report=#{netuno.componentConfig.link.report}", netuno.componentConfig.link.contentLoaded)
      netuno.componentConfig.link.popup.modal("show")
    ,
    contentLoaded: ()->
      popup = netuno.componentConfig.link.popup
      if $('.component_config_link_popup_table_item').length
        popup.find("[data-netuno-back]").hide()
        $('.component_config_link_popup_table_item').on 'click', (e)->
          e.preventDefault()
          e.stopPropagation()
          item = $(this)
          netuno.componentConfig.link.openTable(item.attr('data-netuno-table-uid'), item.attr('data-parameter-key'), item.attr('data-table-uid'))
      if $('.component_config_link_popup_fields_title').length
        popup.find("[data-netuno-back]").off('click').on('click', ()->
          back = $(this);
          popup.find(".modal-body").empty().load("#{ netuno.config.urlAdmin }dev/Link#{ netuno.config.extension }?mode=#{ back.attr('data-mode') }&netuno_table_uid=#{ back.attr('data-table-uid') }&parameter_key=#{ back.attr('data-parameter-key') }&report=#{netuno.componentConfig.link.report}", netuno.componentConfig.link.contentLoaded)
        ).show()
        $('.component_config_link_popup_field_item').on 'click', (e)->
          e.preventDefault();
          e.stopPropagation();
          item = $(this);
          netuno.componentConfig.link.setField(item.attr('data-netuno-table-uid'), item.attr('data-parameter-key'), item.attr('data-table-uid'), item.attr('data-table-name'), item.attr('data-field-uid'), item.attr('data-field-name'))
        netuno.componentConfig.link.updateFields popup.attr('data-netuno-table-uid'), popup.attr('data-parameter-key'), popup.attr('data-table-uid'), popup.attr('data-table-name')        
    ,
    openTable: (netunoTableUid, parameterKey, tableUid)->
      netuno.componentConfig.link.popup.find(".modal-body").empty().load("#{ netuno.config.urlAdmin }dev/Link#{ netuno.config.extension }?netuno_table_uid=#{ netunoTableUid }&parameter_key=#{ parameterKey }&table_uid=#{ tableUid }&report=#{netuno.componentConfig.link.report}", netuno.componentConfig.link.contentLoaded)
    ,
    updateFields: (netunoTableUid, parameterKey, tableUid, table) ->
      netuno.componentConfig.link.popup.find('.component_config_link_popup_field_item').each(
        () ->
          item = $(this)
          itemSpan = item.find('span')
          itemSpan.css('font-weight', 'normal')
          icon = itemSpan.find('i')
          icon.removeClass('fa-circle')
          icon.addClass('fa-circle-o')
          item.attr('active', 'false')
      )
      val = $("\#componentConfig#{ netunoTableUid }LinkField_#{ parameterKey }").val()
      netuno.componentConfig.link.popup.find('p[data-link-fields-selected]').text('')
      if not val? or val is ''
        return
      columns = val.substring(val.indexOf(':') + 1).split(',')
      netuno.componentConfig.link.popup.find('p[data-link-fields-selected]').text(columns.join(', '))
      columns = columns.reverse()
      for column in columns
        item =  netuno.componentConfig.link.popup.find(".component_config_link_popup_field_item[data-field-name=\"#{column}\"]")
        itemSpan = item.find('span')
        itemSpan.css('font-weight', 'bold')
        icon = itemSpan.find('i')
        icon.removeClass('fa-circle-o')
        icon.addClass('fa-circle')
        item.attr('active', 'true')
        item.parent().prepend(item)
    ,
    setField: (netunoTableUid, parameterKey, tableUid, table, fieldUid, field)->
      netuno.componentConfig.link.popup.attr('data-table-uid', tableUid)
      fieldItem = netuno.componentConfig.link.popup.find(".component_config_link_popup_field_item[data-field-uid=\"#{fieldUid}\"]")
      mode = 'add'
      if fieldItem.attr('active') == 'true'
        mode = 'remove'
      value = $("\#componentConfig#{ netunoTableUid }LinkField_#{ parameterKey }").val() or ''
      if value? and (mode == 'add' or mode == 'remove')
        if mode == 'add' and
        ((value.indexOf(":") isnt -1 and value.substring(0, value.indexOf(':')) != table) or
        (((value.indexOf(":") isnt -1 and value.substring(value.lastIndexOf(":")) isnt ":#{ field }") or value.indexOf(":") is -1) and
        ((value.indexOf(",") isnt -1 and value.substring(value.lastIndexOf(",")) isnt ",#{ field }") or value.indexOf(",") is -1) and
        value.indexOf(",#{ field },") is -1))
          if (value.indexOf(':') > -1 && value.substring(0, value.indexOf(':')) == table)
            value += ',' + field
          else if ((value.indexOf(':') > -1 && value.substring(0, value.indexOf(':')) != table) || (value == ''))
            value = "#{ table }:#{ field }"
        else if (mode == 'remove')
          if (value.indexOf(',') > -1)
            value = "#{ value },"
            value = value.split(":#{ field },").join(':')
            value = value.split(",#{ field },").join(',')
            value = value.substring(0, value.length - 1)
          else if (value.indexOf(':') > -1 && value.substring(0, value.indexOf(':')) == table)
            value = ''
          else if (value.indexOf(':') == -1)
            value = ''
        $("\#componentConfig#{ netunoTableUid }LinkField_#{ parameterKey }").val(value)
        $("\#componentConfig#{ netunoTableUid }FieldShow_#{ parameterKey }").val(value)
        netuno.componentConfig.link.updateFields netunoTableUid, parameterKey, tableUid, table
      netuno.componentConfig.link.callbackItem field, fieldUid if netuno.componentConfig.link.callbackItem
    close: ()->
      if netuno.componentConfig.link.popup?
        netuno.componentConfig.link.popup.attr('data-table-uid', null)
        netuno.componentConfig.link.popup.modal('hide')
  },
  choice: {
    load: (netunoTableUid, parameterKey, defaultValue, value)->
      select = $("\#componentConfig#{ netunoTableUid }_#{ parameterKey }")
      values = defaultValue.split('|')
      values.map (v)->
        option = new Option(v, v)
        option.selected = v == value
        select.append(option)
  },
  boolean: {
    load: (netunoTableUid, parameterKey, defaultValue, value)->
      checkbox = $("\#componentConfig#{ netunoTableUid }_#{ parameterKey }")
      if (value == 'true')
        checkbox.attr('checked', 'checked')
  }
}

jQuery.validator.addMethod("sqlname", (value, element)->
    regExp = new RegExp("^[a-z_]+[a-z0-9_]*$", "");
    return regExp.test(value)
  , 'Invalid name.'
)
