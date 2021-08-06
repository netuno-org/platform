
toastr.options = {
  "closeButton": true,
  "debug": false,
  "newestOnTop": true,
  "progressBar": false,
  "positionClass": "toast-top-center",
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

netuno.newUid = ()->
  s4 = () ->
    return Math.floor((1 + Math.random()) * 0x10000)
      .toString(16)
      .substring(1)
  return "#{ s4() }#{ s4() }_#{ s4() }_#{ s4() }_#{ s4() }_#{ s4() }#{ s4() }#{ s4() }"

netuno.callbacks =
  create: (func)->
    key = netuno.newUid()
    netuno.callbacks[key] = func
    return key
  remove: (key)->
    delete netuno.callbacks[key]

netuno.menu = (menu)->
  navMenuHtml = '<li class="nav-item"><a href="#netuno_dashboard" class="nav-link">Dashboard</a></li>'
  containersHtml = ''
  buildMenu = (type, parentUId, items, level) ->
    menuHtml = '';
    if level > 0
      menuHtml += '<ul>'
    for item in items
      expand = ""
      if item.items.length > 0
        expand = " data-toggle="
      menuHtml += '<li class="nav-item">'
      menuHtml += "<a class=\"nav-link\" href=\"\#\" netuno-#{ type }=\"#{ item.name }\"#{ expand }>"
      menuHtml += "#{ item.text }"
      if item.items.length > 0
        menuHtml += "<i class=\"fa fa-caret-down\"></i>"
      menuHtml += "</a>"
      if item.items.length > 0
        menuHtml += buildMenu(type, item.uid, item.items, level + 1)
      menuHtml += '</li>'
      containersHtml += "<div netuno-#{ type } netuno-#{ type }-uid=\"#{ item.uid }\" netuno-#{ type }-name=\"#{ item.name }\">"
      if type is 'form'
        containersHtml += "<div class=\"netuno-form-edit\" netuno-#{ type }-edit=\"#{ item.name }\"></div>"
        containersHtml += "<div class=\"netuno-form-search\" netuno-#{ type }-search=\"#{ item.name }\"></div>"
      containersHtml += "</div>"
    if level > 0
      menuHtml += '</ul>'
    return menuHtml
  if menu.forms.length > 0
    navMenuHtml += "<li class=\"nav-section-title\"><h4>&nbsp;<i class=\"fa fa-edit\"></i>#{ netuno.lang['menu.forms'] }</h4></li>"
    navMenuHtml += buildMenu 'form', 0, menu.forms, 0
  if menu.reports.length > 0
    navMenuHtml += "<li class=\"nav-section-title\"><h4>&nbsp;<i class=\"fa fa-area-chart\"></i>#{ netuno.lang['menu.reports'] }</h4></li>"
    navMenuHtml += buildMenu 'report', 0, menu.reports, 0
  $('.sidebar > .navbar-nav').append(navMenuHtml).show().find('ul').hide()
  $('#containers').append(containersHtml)

netuno.loadForm = (container) ->
  container = $(container) unless container instanceof jQuery
  container = if container.is("[netuno-form][netuno-form-uid]") then container else container.closest("[netuno-form][netuno-form-uid]")
  containerSearch = container.children('[netuno-form-search]')
  data = { 'netuno_table_uid': container.attr('netuno-form-uid') }
  if container.is("[netuno-form-relation]")
    mainContainer = container.parents("[netuno-form][netuno-form-uid]")
    data['netuno_relation_table_uid'] = mainContainer.find('input[name=netuno_table_uid]').val()
    data['netuno_relation_item_uid'] = mainContainer.find('input[name=netuno_item_uid]').val()
  if containerSearch.is(':empty')
    $.ajax(
      type: 'POST'
      url: "#{ netuno.config.urlBase }Search#{ netuno.config.extension }"
      data: data
      success: (response) ->
        containerSearch.html(response);
        netuno.contentLoaded(containerSearch)
    )
  container.show()

netuno.loadFormSearchDataTable = (table) ->
  table = $(table) unless table instanceof jQuery
  isButton = table.is('button')
  container = table.parents("[netuno-form][netuno-form-uid]:first")
  table = container.find("[netuno-form-search-table=#{ container.attr('netuno-form-name') }]:first") unless table.is('table')
  containerSearch = container.children('[netuno-form-search]')
  containerSearchForm = containerSearch.find("form[name=netuno_form_#{ container.attr('netuno-form-name') }_search]:first")
  tableBody = table.find('tbody')
  findDataTableTR = (e) ->
    if e.parentNode.tagName.toLowerCase() is 'tr'
      return e.parentNode
    return findDataTableTR(e.parentNode)
  tableBody.click (event) ->
    netuno.loadFormEdit(table, $(findDataTableTR(event.target)).children(':first-child').children('span').html())
  if table.find('th').length > 0
    netuno.unmask containerSearchForm if isButton
    dt = table.dataTable(
      "pagingType": "full_numbers"
      "pageLength": 25
      "processing": false
      "serverSide": true
      "destroy": true
      "ajax": {
        "url": "#{ netuno.config.urlBase }Search#{ netuno.config.extension }?netuno_action=datasource&"+ containerSearchForm.serialize()
        "type": "POST"
      }
      "drawCallback": (settings, json) ->
        netuno.mask table
      ,
      "initComplete": (settings, json) ->
        table.fadeIn()
      ,
      "language": netuno.config.dataTable,
      "dom": 'pitip',
      "tableTools": {
        "sSwfPath": "#{ netuno.config.urlScripts }/plugins/datatables-tabletools/swf/copy_csv_xls_pdf.swf"
      }
    )
    netuno.mask containerSearchForm if isButton

netuno.loadFormEdit = (container, item) ->
  container = $(container) unless container instanceof jQuery
  container = if container.is("[netuno-form][netuno-form-uid]") then container else container.parents("[netuno-form][netuno-form-uid]:first")
  container.children('[netuno-form-search]').hide()
  data = {
    'netuno_table_uid': container.attr('netuno-form-uid'),
    'netuno_item_uid': item,
    'netuno_edit_only': container.is("[netuno-form-edit-only]"),
    'netuno_zone': container.attr('netuno-zone')
  }
  if container.is("[netuno-form-relation]")
    mainContainer = container.parents("[netuno-form][netuno-form-uid]:first")
    data['netuno_relation_table_uid'] = mainContainer.find('input[name=netuno_table_uid]').val()
    data['netuno_relation_item_uid'] = mainContainer.find('input[name=netuno_item_uid]').val()
  containerEdit = container.children('[netuno-form-edit]')
  containerEdit.empty()
  containerEdit.show()
  $.ajax(
    type: 'POST'
    url: "#{ netuno.config.urlBase }Edit#{ netuno.config.extension }"
    data: data
    success: (response) ->
      containerEdit.html(response);
      netuno.contentLoaded(containerEdit)
      containerEditForm = containerEdit.find("form[name=netuno_form_#{ container.attr('netuno-form-name') }_edit]")
      if (containerEditForm.length > 0)
        netuno.loadValidation(containerEditForm)
      if typeof item == 'undefined' or item == null or item == ''
        container.trigger('netuno:new')
      else
        container.trigger('netuno:edit', [ item ])
  )

netuno.backFormEdit = (element) ->
  element = $(element) unless element instanceof jQuery
  container = if element.is("[netuno-form][netuno-form-uid]") then element else element.parents("[netuno-form][netuno-form-uid]:first")
  container.children('[netuno-form-edit]').empty().hide()
  container.children('[netuno-form-search]').show()
  netuno.loadFormSearchDataTable(container.find("[netuno-form-search-table=#{ container.attr('netuno-form-name') }]:first"))
  container.trigger('netuno:back')

netuno.saveFormEdit = (element, fromRelation) ->
  element = $(element) unless element instanceof jQuery
  container = if element.is("[netuno-form][netuno-form-uid]") then element else element.parents("[netuno-form][netuno-form-uid]:first")
  containerEdit = container.children('[netuno-form-edit]')
  containerEditForm = containerEdit.find("form[name=netuno_form_#{ container.attr('netuno-form-name') }_edit]:first")
  netuno.unmask containerEditForm
  containerEditForm.ajaxForm().submit()
  if containerEditForm.validate().valid()
    callback = netuno.callbacks.create((uid)->
      container.trigger('netuno:save', [ uid ])
    )
    containerEditForm.ajaxForm(
        url: "#{ netuno.config.urlBase }Edit#{ netuno.config.extension }?netuno_action=save&netuno_autosave=#{ typeof fromRelation != 'undefined' }&netuno_callback=#{ callback }"
        iframe: false
        success: (response) ->
          containerEdit.html(response)
          containerEditForm = containerEdit.find("form[name=netuno_form_#{ container.attr('netuno-form-name') }_edit]:first")
          if (containerEditForm.length > 0)
            netuno.contentLoaded(containerEdit)
            netuno.loadValidation(containerEditForm)
            if (typeof fromRelation != 'undefined')
              containerEditForm.find("button[netuno-form-edit-relation-button=#{ fromRelation }]:first").trigger('click')
    ).submit()
    return true
  else
    netuno.mask containerEditForm
    return false

netuno.deleteFormEdit = (element) ->
  element = $(element) unless element instanceof jQuery
  container = if element.is("[netuno-form][netuno-form-uid]") then element else element.parents("[netuno-form][netuno-form-uid]:first")
  containerEdit = container.children('[netuno-form-edit]')
  if (!containerEdit.is(':empty'))
    containerEditForm = containerEdit.find("form[name=netuno_form_#{ container.attr('netuno-form-name') }_edit]:first")
    netuno.unmask containerEditForm
    callback = netuno.callbacks.create((uid)->
      container.trigger('netuno:delete', [ uid ])
    )
    $.ajax(
      type: 'POST'
      url: "#{ netuno.config.urlBase }Edit#{ netuno.config.extension }?netuno_action=delete&netuno_callback=#{ callback }"
      data: containerEditForm.serialize()
      success: (response) ->
        containerEdit.html(response)
        netuno.contentLoaded(containerEdit)
        containerEditForm = containerEdit.find("form[name=netuno_form_#{ container.attr('netuno-form-name') }_edit]:first")
        if (containerEditForm.length > 0)
          netuno.loadValidation(containerEditForm)
    )

netuno.loadReport = (container) ->
  container = $(container) unless container instanceof jQuery
  container = if container.is("[netuno-report][netuno-report-uid]") then container else container.parents("[netuno-report][netuno-report-uid]:first")
  container.children('[netuno-report-search]').hide()
  if container.is(':empty')
    $.ajax(
      type: 'POST'
      url: "#{ netuno.config.urlBase }Report#{ netuno.config.extension }"
      data: {'netuno_report_uid': container.attr('netuno-report-uid')}
      success: (response) ->
        container.html(response);
        netuno.contentLoaded(container)
    )
  container.show()

netuno.buildReport = (report) ->
  report = $(report) unless report instanceof jQuery
  container = report.parents("[netuno-report][netuno-report-uid]:first")
  containerResult = container.children("[netuno-report-result=#{ container.attr('netuno-report-name') }]")
  containerForm = container.find("form[name=netuno_report_#{ container.attr('netuno-report-name') }_form]:first")
  containerForm.ajaxForm().submit()
  if containerForm.validate().valid()
    containerForm.ajaxForm(
      url: "#{ netuno.config.urlBase }ReportBuilder#{ netuno.config.extension }"
      iframe: false
      success: (response) ->
        containerResult.html(response)
        netuno.contentLoaded(containerResult)
    ).submit()
    return true
  else
    return false

netuno.loadValidation = (form) ->
  form = $(form) unless form instanceof jQuery
  form = if form.is("form") then form else form.closest("form")
  rules = {}
  form.find("[validation]").each ()->
    element = $(this)
    validation = S(element.attr("validation"))
    rules[element.attr("name")] = {}
    if validation.contains("required")
      rules[element.attr("name")]["required"] = true
    if validation.contains("email")
      rules[element.attr("name")]["email"] = true
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

netuno.downloadProgress = (url) ->
  time = new Date().getTime()
  $("body").append("<iframe id=\"netuno_download_#{ time }\" src=\"#{ url }&downloadToken=#{ time }\" style=\"display: none;\"></iframe>");
  if (!navigator.userAgent.match(/msie/i))
    modal = $('#netuno_download')
    modal.modal('show');
    tickStatus = () ->
      $('\#netuno_download_progress_bar').css('width', "100%").attr('aria-valuenow', 100);
      $.ajax({
        type: 'GET',
        url: "#{url }&downloadToken=#{ time }&downloadStatus=true",
        success: (response) ->
          if (response == 'done')
            iframe = $("\#netuno_download_#{ time }")
            if ($("\#netuno_download_#{ time }").length > 0)
              iframe.remove()
              window.setTimeout(() ->
                modal.modal('hide')
                $('\#netuno_download_progress_bar').css('width', "0%").attr('aria-valuenow', 0);
              , 1000);
          else
            window.setTimeout(tickStatus, 1000);
      });
    tickStatus();


netuno.keepAlive = ()->
  if typeof netuno.config.user != 'undefined'
    $.ajax {
      type: 'POST',
      url: "#{ netuno.config.urlBase }KeepAlive#{ netuno.config.extension }",
      success: (response)->
        if response == "1"
          window.setTimeout('netuno.keepAlive();', 360000)
        else
          document.location.href = "#{ netuno.config.urlBase }Index#{ netuno.config.extension }"
    }

netuno.loadLinks = (container)->
  container.find("a").on "click", ()->
    element = $(this)
    if element.parents('.sidebar').length == 1 and window.innerWidth < 768 and element.attr('data-toggle') == null
      $("\#sidebarToggle").trigger('click')
    if element.attr("rel") is "external"
      if element.attr("target") is "_blank"
        window.open(element.attr("href"))
        return false
      else
        window.location.href = element.attr("href")
        return false
    else
      if element.is("[data-toggle]")
        ul = element.parent().children('ul')
        if ul.is(':hidden')
          ul.fadeIn()
          element.children('i:last-child').removeClass('fa-caret-down').addClass('fa-caret-up')
        else
          ul.fadeOut()
          element.children('i:last-child').removeClass('fa-caret-up').addClass('fa-caret-down')
      if element.parents('.navbar-nav').length == 1
        window.scrollTo(0, 0)
      if element.is("[netuno-content]")
        container = $(element.attr("href"))
        $("#containers > div").hide()
        if container.length > 0 and container.is(":empty")
          $.ajax
            url: element.attr('netuno-content'),
            success: (response) ->
              container.html(response);
              netuno.contentLoaded(container)
        container.show()
        return false
      else if element.is("[netuno-form]")
        formName = element.attr("netuno-form")
        $("\#containers > div").hide()
        netuno.loadForm($("\#containers > div[netuno-form-name=#{ formName }]"))
        return false
      else if element.is("[netuno-report]")
        reportName = element.attr("netuno-report")
        $("#containers > div").hide()
        netuno.loadReport($("\#containers > div[netuno-report-name=#{ reportName }]"))
        return false
      else if element.attr("href") isnt "#"
        container = $(element.attr("href"))
        $("#containers > div").hide()
        container.show()
        return false

netuno.mask = (container)->
  container.find('[data-mask]').each(()->
    o = $(this)
    if (o.attr('data-mask') != null and o.attr('data-mask') != '')
      o.mask(o.attr('data-mask'), {
        reverse: o.attr('data-mask-reverse') is 'true'
        selectOnFocus: o.attr('data-mask-selectonfocus') is 'true'
      })
  )

netuno.unmask = (container)->
  container.find('[data-mask]').each(()->
    o = $(this)
    if (typeof o.data().mask isnt 'undefined' and o.data().mask isnt null)
      if o.text() isnt ''
        data = o.data()
        delete data.mask
        o.data(data)
      else
        o.unmask()
  )

netuno.addContentLoad (container)->
  netuno.loadLinks(container)

netuno.addContentLoad (container)->
  container.find("select").select2(
    theme: "bootstrap4"
    placeholder: ""
    maximumSelectionSize: 6
  )

netuno.addContentLoad (container)->
  netuno.mask(container)

netuno.openRelation = (name) ->
  container = $("\#netuno_form_#{ name }")
  containerSearch = container.children('[netuno-form-search]')
  if containerSearch.is(':empty')
    $.ajax(
      type: 'POST'
      url: "#{ netuno.config.urlBase }Search#{ netuno.config.extension }"
      data: { 'netuno_table_uid': container.attr('netuno-form-uid') }
      success: (response) ->
        containerSearch.html(response);
        netuno.contentLoaded(containerSearch)
    )
  container.show()


netuno.addPageLoad ()->
  netuno.loadLinks($("body"))

netuno.modal = {}

netuno.modal.stack = []

netuno.modal.create = (config) ->
  config = $.extend {
    name: null,
    container: $('body')
    relation: false
    callback: (modal)->
  }, config
  if (config.name == null)
    return
  key = netuno.newUid()
  $.ajax(
    type: 'POST'
    url: "#{ netuno.config.urlBase }Edit#{ netuno.config.extension }"
    dataType: 'json'
    data: {
      netuno_action: 'uid'
      netuno_table_name: config.name
    }
    success: (response) ->
      modalRelationParam = ""
      modalRelationFormParam = ""
      if (config.relation)
        modalRelationParam = "netuno-form-edit-relation-modal=\"#{ config.name }\""
        modalRelationFormParam = "netuno-form-relation"
      config.container.append("""
<div id="netuno-modal-#{ key }" #{ modalRelationParam } class="netuno-modal">
  <div class="container-fluid">
    <div netuno-form netuno-form-uid="#{ response.uid }" netuno-form-name="#{ config.name }" #{ modalRelationFormParam }>
      <div class="netuno-form-edit" netuno-form-edit></div>
      <div class="netuno-form-search" netuno-form-search></div>
    </div>
  </div>
</div>
""")
      config.callback($("\#netuno-modal-#{ key }"))
  )

netuno.modal.showAndLoad = (modal)->
  modal = $(modal) unless modal instanceof jQuery
  netuno.modal.show(modal)
  netuno.loadForm(modal.find('[netuno-form]'))

netuno.modal.show = (modal)->
  modal = $(modal) unless modal instanceof jQuery
  modal.trigger('netuno:modal:show')
  $('#netuno-modal-curtain').fadeIn()
  $('#netuno-modal-close').show()
  modal.show()
  netuno.modal.stack.push(modal)
  modal.trigger('netuno:modal:shown')

netuno.modal.hide = (modal)->
  modal = $(modal) unless modal instanceof jQuery
  modal.trigger('netuno:modal:hide')
  modal.hide()
  for i in [0 ... netuno.modal.stack.length]
    if (netuno.modal.stack[i] == modal)
      netuno.modal.stack.splice(i, 1)
  if (netuno.modal.stack.length == 0)
    $('#netuno-modal-curtain').fadeOut()
    $('#netuno-modal-close').hide()
  modal.trigger('netuno:modal:hidden')

netuno.modal.hideLast = ()->
  if (netuno.modal.stack.length > 0)
    netuno.modal.hide(netuno.modal.stack[netuno.modal.stack.length - 1])

netuno.com = {}

netuno.com['text'] =
  'html': {
    'load': (id, css)->
      tinymce.init(
        selector: "textarea\##{ id }"
        content_css: css
        theme: 'modern'
      )
  }

netuno.com['select'] =
  callbacks: []
  getConfig: (fieldId, service, ajaxParams) ->
    netuno.com.select.loadCallbacksForFieldId(fieldId)
    delayCallbacks = new Date().getTime()
    #field = $("\##{ fieldId }")
    # dropdownParent: field.parent()
    return {
      theme: "bootstrap4"
      placeholder: netuno.config.com.lang.select["defaulttext"]
      allowClear: true,
      data: [
        {
          id: '',
          text: ''
        }
      ]
      ajax: {
        url: service
        dataType: 'json'
        delay: 250
        data: (params) ->
          return $.extend(true, {
            q: params.term,
            page: params.page,
            page_limit: 10
          }, ajaxParams)
        processResults: (data, params) ->
          params.page = 1 if params.page?
          return { results: data }
      }
      templateResult: (item) ->
        return item.label
      templateSelection: (item) ->
        if (new Date().getTime() < delayCallbacks + 250)
          return item.label
        for key of netuno.com.select.callbacks[fieldId]
          callback = netuno.com.select.callbacks[fieldId][key]
          if ($.isFunction(callback))
            callback(item.id)
            delayCallbacks = new Date().getTime()
        return item.label
      escapeMarkup: (m)->
        return m
    }
  callbackForFieldId: (fieldId, key, callback)->
    netuno.com.select.loadCallbacksForFieldId(fieldId)
    netuno.com.select.callbacks[fieldId][key] = callback
  loadCallbacksForFieldId: (fieldId)->
    if fieldId? and fieldId.length > 0 and not netuno.com.select.callbacks[fieldId]?
      netuno.com.select.callbacks[fieldId] = {}
  load: (fieldId, comUid, service)->
    $("\##{ fieldId }").select2(netuno.com.select.getConfig(fieldId, service, { com_uid: comUid }))
  loadInContainer: (container)->
    container.find("select[netuno-select-uid]").each(()->
      select = $(this)
      select.select2('destroy')
      value = select.attr('value')
      comUid = select.attr('netuno-select-uid')
      service = select.attr('netuno-select-service')
      if comUid? and comUid isnt ''
        select2 = select.select2(netuno.com.select.getConfig(select.attr('id'), service, { com_uid: comUid }))
        if value? and value isnt ''
          option = $('<option selected>Loading...</option>').val(value)
          select.append(option)
          select.trigger('change')
          $.ajax({
            dataType: "jsonp"
            url: "#{ service }?com_uid=#{ comUid }&data_uid=#{ value }"
          }).then((data) ->
            if data? and data.label? and data.id?
              option.html(data.label).val(data.id)
              option.removeData()
              select.trigger('change')
              selectId = select.attr('id')
              selectContainer = $("\#select2-#{ selectId }-container");
              selectContainer.html(data.label)
              selectContainer.prepend($(document.createElement("span")).addClass('select2-selection__clear').text('×').data(data).on('click', ()->
                $("\##{ selectId }").val('')
                $(this).parent().empty()
              ))
              selectContainer.find('.select2-selection__clear').data(data)
          )
    )
    container.find("select[netuno-select-link]").each(()->
      link = $(this).attr('netuno-select-link')
      columnSeparator = $(this).attr('netuno-select-column-separator')
      maxColumnLength = $(this).attr('netuno-select-max-column-length')
      onlyActives = $(this).attr('netuno-select-only-actives')
      service = $(this).attr('netuno-select-service')
      if link? and link isnt ''
        $(this).select2(netuno.com.select.getConfig($(this).attr('id'), service, {
          link: link,
          column_separator: columnSeparator,
          max_column_length: maxColumnLength,
          only_actives: onlyActives
        }));
    )
    container.find("select[netuno-select-service]:not([netuno-select-uid],[netuno-select-link])").each(()->
      service = $(this).attr('netuno-select-service')
      if service? and service isnt ''
        select = $(this)
        select.select2('destroy')
        value = select.attr('value')
        service = select.attr('netuno-select-service')
        select2 = $(this).select2(netuno.com.select.getConfig($(this).attr('id'), service))
        if value? and value isnt ''
          option = $('<option selected>Loading...</option>').val(value)
          select.append(option).trigger('change')
          $.ajax({
            dataType: "jsonp"
            url: "#{ service }&data_uid=#{ value }"
          }).then((data) ->
            if data? and data.label? and data.id?
              option.html(data.label).val(data.id)
              option.removeData()
              select.trigger('change')
              selectId = select.attr('id')
              selectContainer = $("\#select2-#{ selectId }-container");
              selectContainer.html(data.label)
              selectContainer.prepend($(document.createElement("span")).addClass('select2-selection__clear').text('×').data(data).on('click', ()->
                $("\##{ selectId }").val('')
                $(this).parent().empty()
              ))
              selectContainer.find('.select2-selection__clear').data(data)
          )
    )
  setValue: (select, uid) ->
    select
      .empty()
      .append($('<option></option>'))
      .append(
        $('<option selected></option>')
        .val(uid)
      )
      .val(uid)
      .attr('value', uid)
    netuno.com.select.loadInContainer(select.parent())


netuno.com['date'] =
  load: (fieldId, container, callback) ->
    $("\##{ fieldId }").on('change', callback).datepicker({
      format: "yyyy-mm-dd",
      autoclose: true,
      todayHighlight: true,
      orientation: "top auto"
      container: "\##{ container }"
    })

netuno.com['time'] =
  load: (fieldId, container, callback) ->
    control = $("\##{ fieldId }").on('change', callback).clockpicker({
      autoclose: true,
      placement: 'top'
    })
    return control

netuno.com['checkbox'] =
    loadInContainer: (container)->


netuno.com['color'] =
  load: (fieldId) ->
    control = $("\##{ fieldId }").colorpicker()
    return control

netuno.com['image'] =
  load: (id)->
    if ($("\##{ id }-view").length == 0)
      $('body').append($("<div id=\"\##{ id }-view\" style=\"display: none;\"></div>"))
    if ($("\##{ id }-value").val().length > 0)
      value = $("\##{ id }-value").val()
    else
      $("\##{ id }-btView").hide()
  view: (id, url)->
    window.open(url, "_blank")
  clear: (id)->
    $("\##{ id }").val('')
    $("\##{ id }-fileFeedback").html('')
    $("\##{ id }-null").val('true')
    $("\##{ id }-preview").hide()
    $("\##{ id }-btView").hide()

netuno.com['file'] =
  load: (id)->
    if ($("\##{ id }-value").val().length == 0)
      $("\##{ id }-btView").hide()
  view: (id)->
    value = $("\##{ id }-value")
    window.open(value.attr('file-url'))
  clear: (id)->
    $("\##{ id }").val('')
    $("\##{ id }-fileFeedback").html('')
    $("\##{ id }-null").val('true')
    $("\##{ id }-btView").hide()
  restore: (id)->

$(document).ready ()->
  netuno.pageLoaded()
  if window.innerWidth < 768
    $('#sidebarToggle').trigger('click')
  netuno.addContentLoad (container) ->
    netuno.com['select'].loadInContainer(container)
  netuno.addContentLoad (container) ->
    netuno.com['checkbox'].loadInContainer(container)
  netuno.keepAlive()
  $(document).on 'show.bs.modal', (event)->
    target = $(event.target)
    parents = target.parents('.modal')
    if (parents.length > 0)
      target.detach().appendTo('body').attr('netuno-parent-modal-id', $(parents[0]).attr('id'))
      $(parents[0]).modal('hide')
  $(document).on 'hidden.bs.modal', (event)->
      target = $(event.target)
      if (target.attr('netuno-parent-modal-id'))
        target.detach().appendTo("\##{ target.attr('netuno-parent-modal-id') }")
        $("\##{ target.attr('netuno-parent-modal-id') }").modal('show')

      #$(event.relatedTarget).parents('.modal')
      #$(event.relatedTarget).parents('.modal').modal('hide')
  #$(document).on 'shown.bs.modal', (event) ->
  #  if ($('body').hasClass('modal-open') == false)
  #    $('body').addClass('modal-open')
