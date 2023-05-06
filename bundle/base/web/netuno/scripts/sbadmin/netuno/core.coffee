
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
  navMenuHtml = "<li><a netuno-navigation-dashboard href=\"#netuno_dashboard\"><i class=\"fa fa-fw fa-dashboard\"></i> #{ netuno.lang['menu.dashboard'] }</a></li>"
  containersHtml = ''
  buildMenu = (type, parentUId, items, level) ->
    menuHtml = '';
    if level > 0
      menuHtml += '<ul>'
    for item in items
      expand = ""
      if item.items.length > 0
        expand = " data-toggle="
      menuHtml += '<li>'
      menuHtml += "<a netuno-navigation-#{ type }-item.name href=\"\#\" netuno-#{ type }=\"#{ item.name }\"#{ expand }>"
      menuHtml += "#{ item.text }"
      if item.items.length > 0
        menuHtml += "<i class=\"fa fa-fw fa-caret-down\"></i>"
      menuHtml += "</a>"
      if item.items.length > 0
        menuHtml += buildMenu(type, item.uid, item.items, level + 1)
      menuHtml += '</li>'
      containersHtml += "<div netuno-container-#{ type }-#{ item.name } netuno-#{ type } netuno-#{ type }-uid=\"#{ item.uid }\" netuno-#{ type }-name=\"#{ item.name }\">"
      if type is 'form'
        containersHtml += "<div class=\"netuno-form-edit\" netuno-#{ type }-edit=\"#{ item.name }\"></div>"
        containersHtml += "<div class=\"netuno-form-search\" netuno-#{ type }-search=\"#{ item.name }\"></div>"
      containersHtml += "</div>"
    if level > 0
      menuHtml += '</ul>'
    return menuHtml
  if menu.forms.length > 0
    navMenuHtml += "<li class=\"nav-section-title\"><h4>&nbsp;<i class=\"fa fa-fw fa-edit\"></i>#{ netuno.lang['menu.forms'] }</h4></li>"
    navMenuHtml += buildMenu 'form', 0, menu.forms, 0
  if menu.reports.length > 0
    navMenuHtml += "<li class=\"nav-section-title\"><h4>&nbsp;<i class=\"fa fa-fw fa-area-chart\"></i>#{ netuno.lang['menu.reports'] }</h4></li>"
    navMenuHtml += buildMenu 'report', 0, menu.reports, 0
  navbar = $('[netuno-navigation]')
  navbar.append(navMenuHtml).show().find('ul').hide()
  navbar.trigger('netuno:loaded')
  containers = $('[netuno-containers]')
  containers.append(containersHtml)
  containers.trigger('netuno:loaded')
  netuno.navigationLoaded()

netuno.loadForm = (container) ->
  container = $(container) unless container instanceof jQuery
  container = if container.is("[netuno-form][netuno-form-uid]") then container else container.closest("[netuno-form][netuno-form-uid]")
  containerSearch = container.children('[netuno-form-search]')
  data = { 'netuno_table_uid': container.attr('netuno-form-uid') }
  if container.is("[netuno-form-relation]")
    data['netuno_relation_table_uid'] = container.attr('netuno-table-uid')
    data['netuno_relation_item_uid'] = container.attr('netuno-item-uid')
  if containerSearch.is(':empty')
    containerSearch.html("<div class=\"netuno-loader\">Loading...</div>")
    $.ajax(
      type: 'POST'
      url: "#{ netuno.config.urlAdmin }Search#{ netuno.config.extension }"
      data: data
      success: (response) ->
        containerSearch.html(response)
        netuno.contentLoaded(containerSearch)
    )
  container.show()

netuno.formSearchDataTableBulk = {}

netuno.formSearchDataTableBulkActive = (tableId, callback) ->
  netuno.formSearchDataTableBulk[tableId] = {
    active: true,
    callback: callback,
    uids: []
  }
  table = $("\##{tableId}")
  searchContainer = table.parents("[netuno-form-search]:first")
  searchContainer.find("[netuno-form-search-buttons]").find("button").prop('disabled', true).fadeTo("slow", 0.5)
  searchContainer.find("h1.page-header").find('button').prop('disabled', true).fadeTo("slow", 0.5)


netuno.formSearchDataTableBulkInactive = (tableId) ->
  if netuno.formSearchDataTableBulk[tableId]?
    delete netuno.formSearchDataTableBulk[tableId]
  table = $("\##{tableId}")
  tableBody = table.find('tbody')
  tableBody.find('tr').each () ->
    tr = $(this)
    tr.removeClass('selected')
  searchContainer = table.parents("[netuno-form-search]:first")
  searchContainer.find("[netuno-form-search-buttons]").find("button").prop('disabled', false).fadeTo("slow", 1)
  searchContainer.find("h1.page-header").find('button').prop('disabled', false).fadeTo("slow", 1)

netuno.executeFormSearchBulk = (settings) ->
  settings = $.extend {
    action: null
    tableId: null
    callback: (resut)->
  }, settings
  table = $("\##{settings.tableId}")
  container = table.parents("[netuno-form][netuno-form-uid]:first")
  container.data({
    pageNumber: 0,
    pageLength: 0,
    rowIndex: 0
  })
  uids = netuno.formSearchDataTableBulk[settings.tableId].uids
  $.ajax(
    type: 'POST'
    url: "#{ netuno.config.urlAdmin }Search#{ netuno.config.extension }"
    dataType: 'json'
    contentType : 'application/json',
    data: JSON.stringify({
      netuno_action: "bulk-#{settings.action}"
      netuno_table_name: container.attr('netuno-form-name'),
      netuno_items_uids: uids
    })
    success: (response) ->
      settings.callback(response)
  )

netuno.loadFormSearchDataTable = (table) ->
  table = $(table) unless table instanceof jQuery
  isButton = table.is('button')
  container = table.parents("[netuno-form][netuno-form-uid]:first")
  table = container.find("[netuno-form-search-table=#{ container.attr('netuno-form-name') }]:first") unless table.is('table')
  tableId = table.attr('id')
  netuno.formSearchDataTableBulkInactive tableId
  containerSearch = container.children('[netuno-form-search]')
  containerSearchForm = containerSearch.find("form[name=netuno_form_#{ container.attr('netuno-form-name') }_search]:first")
  tableBody = table.find('tbody')
  tableBody.empty()
  dt = null
  findDataTableTR = (e) ->
    if e.tagName.toLowerCase() is 'tr'
      return e
    if e.parentNode.tagName.toLowerCase() is 'tr'
      return e.parentNode
    return findDataTableTR(e.parentNode)
  if table.find('th').length > 0
    netuno.unmask containerSearchForm if isButton
    dt = table.dataTable(
      "pagingType": "full_numbers"
      "pageLength": 25
      "processing": false
      "serverSide": true
      "destroy": true
      "order": if container.data().sorting then container.data().sorting else []
      "displayStart": if container.data().pageNumber > 0 then container.data().pageNumber * 25 else null
      "ajax": {
        "url": "#{ netuno.config.urlAdmin }Search#{ netuno.config.extension }?netuno_action=datasource&"+ containerSearchForm.serialize()
        "type": "POST"
      }
      "preDrawCallback": (settings) ->
        tableBody.html("<tr><td colspan=\"1000\" style=\"background-color: #ffffff;\"><div style=\"width: 100%;\"><div class=\"netuno-loader\">Loading...</div></div></td></tr>")
      "drawCallback": (settings, json) ->
        netuno.mask table
        pageNumber = Math.ceil(settings._iDisplayStart / settings._iDisplayLength);
        tableBody.find('tr').data({ pageNumber, pageLength:  tableBody.find('tr').length})
        tableBody.find('tr').each((index) ->
          tr = $(this)
          tr.data({ rowIndex: index })
          tr.off('click').on 'click', (event) ->
            tr = $(findDataTableTR(event.target))
            uid = tr.children(':first-child').children('span').html()
            if netuno.formSearchDataTableBulk[tableId]? and netuno.formSearchDataTableBulk[tableId].active
              if tr.hasClass('selected')
                tr.removeClass('selected')
                netuno.formSearchDataTableBulk[tableId].uids = netuno.formSearchDataTableBulk[tableId].uids.filter (i) ->
                  i isnt uid
              else
                tr.addClass('selected')
                netuno.formSearchDataTableBulk[tableId].uids.push(uid)
              netuno.formSearchDataTableBulk[tableId].callback(netuno.formSearchDataTableBulk[tableId])
            else
              window.scrollTo(container.offset().left, container.offset().top - 70)
              container.data({
                pageNumber: tr.data().pageNumber,
                pageLength: tr.data().pageLength,
                rowIndex: tr.data().rowIndex,
                sorting: table.dataTableSettings[0].aaSorting
              })
              netuno.loadFormEdit(table, uid)
        )
        if netuno.formSearchDataTableBulk[tableId]? and netuno.formSearchDataTableBulk[tableId].active
          tableBody.find('tr').each () ->
            tr = $(this)
            trUID = tr.children(':first-child').children('span').html()
            if netuno.formSearchDataTableBulk[tableId].uids.find((uid) -> uid == trUID)
              tr.addClass('selected')
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
    data['netuno_relation_table_uid'] = container.attr('netuno-table-uid')
    data['netuno_relation_item_uid'] = container.attr('netuno-item-uid')
  containerEdit = container.children('[netuno-form-edit]')
  containerEdit.empty()
  containerEdit.html("<div class=\"netuno-loader\">Loading...</div>")
  containerEdit.show()
  $.ajax(
    type: 'POST'
    url: "#{ netuno.config.urlAdmin }Edit#{ netuno.config.extension }"
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

netuno.backFormEdit = (element, disableSearchTableRefresh) ->
  element = $(element) unless element instanceof jQuery
  container = if element.is("[netuno-form][netuno-form-uid]") then element else element.parents("[netuno-form][netuno-form-uid]:first")
  container.children('[netuno-form-edit]').empty().hide()
  container.children('[netuno-form-search]').show()
  if (disableSearchTableRefresh != true)
    table = container.find("[netuno-form-search-table=#{ container.attr('netuno-form-name') }]:first");
    table.hide()
    netuno.loadFormSearchDataTable(table)
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
        url: "#{ netuno.config.urlAdmin }Edit#{ netuno.config.extension }?netuno_action=save&netuno_autosave=#{ typeof fromRelation != 'undefined' }&netuno_callback=#{ callback }"
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
      url: "#{ netuno.config.urlAdmin }Edit#{ netuno.config.extension }?netuno_action=delete&netuno_callback=#{ callback }"
      data: containerEditForm.serialize()
      success: (response) ->
        if (container.data().pageNumber > 0 && container.data().pageLength == 1)
          container.data({pageNumber: container.data().pageNumber - 1})
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
      url: "#{ netuno.config.urlAdmin }Report#{ netuno.config.extension }"
      data: {'netuno_report_uid': container.attr('netuno-report-uid')}
      success: (response) ->
        container.html(response);
        netuno.contentLoaded(container)
        containerForm = container.find("form[name=netuno_report_#{ container.attr('netuno-report-name') }_form]")
        if (containerForm.length > 0)
          netuno.loadValidation(containerForm)
    )
  container.show()

netuno.buildReport = (report) ->
  report = $(report) unless report instanceof jQuery
  container = report.parents("[netuno-report][netuno-report-uid]:first")
  containerResult = container.children("[netuno-report-result=#{ container.attr('netuno-report-name') }]")
  containerForm = container.find("form[name=netuno_report_#{ container.attr('netuno-report-name') }_form]:first")
  netuno.unmask containerForm
  containerForm.ajaxForm().submit()
  if containerForm.validate().valid()
    netuno.mask containerForm
    containerForm.ajaxForm(
      url: "#{ netuno.config.urlAdmin }ReportBuilder#{ netuno.config.extension }"
      iframe: false
      success: (response) ->
        containerResult.html(response)
        netuno.contentLoaded(containerResult)
    ).submit()
    return true
  else
    netuno.mask containerForm
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
        type: 'POST',
        url: "#{url }&downloadToken=#{ time }&downloadStatus=true",
        success: (response) ->
          if (response == 'done')
            iframe = $("\#netuno_download_#{ time }")
            if ($("\#netuno_download_#{ time }").length > 0)
              window.setTimeout(() ->
                modal.modal('hide')
                $('\#netuno_download_progress_bar').css('width', "0%").attr('aria-valuenow', 0)
                iframe.remove()
              , 1000);
          else
            window.setTimeout(tickStatus, 1000);
      });
    tickStatus();


netuno.keepAlive = ()->
  if typeof netuno.config.user != 'undefined'
    $.ajax {
      type: 'POST',
      url: "#{ netuno.config.urlAdmin }KeepAlive#{ netuno.config.extension }",
      success: (response)->
        if response == "1"
          window.setTimeout('netuno.keepAlive();', 360000)
        else
          document.location.href = "#{ netuno.config.urlAdmin }Index#{ netuno.config.extension }"
    }

netuno.loadLinks = (container)->
  container.find("a").on "click", ()->
    element = $(this)
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
        if ($('body > div.dev').length == 0 || element.parents('.nav-management').length == 0)
          $('.navbar-toggle:visible').click()
        element.trigger('netuno:click')
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
      if (o.attr('data-type') is 'textfloat')
        value = if o.is(':input') then o.val() else o.text()
        maskDecimals = o.attr('data-mask').match(/0[.,](0+)/)
        if maskDecimals?
          maskDecimalsLen = maskDecimals[1].length
          decimalsMultiple = 10
          for i in [1 .. maskDecimalsLen - 1]
            decimalsMultiple *= 10
          o.attr('data-mask-clean-value', value)
          if value.indexOf('.') > 0
            valFloat = parseFloat(value)
            valFloat = Math.round(valFloat * decimalsMultiple) / decimalsMultiple
            val = "#{valFloat}"
            for j in [(val.length - (if val.indexOf('.') > 0 then val.indexOf('.') + 1 else val.length)) .. maskDecimalsLen]
              if j < maskDecimalsLen
                val += '0'
            if o.is(':input')
              o.val(val)
            else
              o.text(val)
          else if value isnt ''
            val = value
            val += '0' for j in [1 .. maskDecimalsLen]
            if o.is(':input')
              o.val(val)
            else
              o.text(val)
      o.mask(o.attr('data-mask'), {
        reverse: o.attr('data-mask-reverse') is 'true'
        selectOnFocus: o.attr('data-mask-selectonfocus') is 'true'
        onChange: (val)->
          if (o.attr('data-type') is 'textfloat')
            decimals = o.attr('data-mask').match(/0[.,](0+)/)
            if decimals?
              decimalsLen = decimals[1].length
              val = o.cleanVal()
              val = "#{val.substring(0, val.length - decimalsLen)}.#{val.substring(val.length - decimalsLen)}"
              o.attr('data-mask-clean-value', val)
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
      if (o.attr('data-mask-clean-value')? && o.attr('data-mask-clean-value') isnt '')
        o.val(o.attr('data-mask-clean-value'))
  )

netuno.addContentLoad (container)->
  netuno.loadLinks(container)

netuno.addContentLoad (container)->
  container.find("select").select2(
    theme: "bootstrap"
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
      url: "#{ netuno.config.urlAdmin }Search#{ netuno.config.extension }"
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
    url: "#{ netuno.config.urlAdmin }Edit#{ netuno.config.extension }"
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
        modalRelationFormParam = "netuno-form-relation netuno-table-uid=\"#{ config.relation.table_uid }\" netuno-item-uid=\"#{ config.relation.item_uid }\""
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
      summernote = $("\##{ id }").summernote({
        callbacks: {
          onImageUpload: (files) ->
            upload = (file) ->
              comUid = summernote.attr('netuno-texthtml-uid')
              formData = new FormData()
              formData.append "com_uid", comUid
              formData.append "file", file, file.name
              $.ajax({
                method: "POST"
                url: "#{ netuno.config.urlAdmin }com/TextHtml#{ netuno.config.extension }"
                contentType: false
                cache: false
                processData: false
                data: formData
                dataType: "json"
                success: (data) ->
                  summernote.summernote("insertImage", data.url)
                error: (jqXHR, textStatus, errorThrown) ->
                  console.error("#{ textStatus } #{ errorThrown }")
              })
            upload(file) for file in files
        },
        height: 200
      })
  }
  'md': {
    'load': (id) ->
      container = $("\##{ id }")
      textarea = container.find("[netuno-textmd-uid]")
      comUid = textarea.attr('netuno-textmd-uid')
      window.setTimeout(()->
        editor = editormd(id, {
            width: "100%",
            height: "400px",
            path: "/netuno/scripts/sbadmin/plugins/editor.md/lib/",
            codeFold : true,
            emoji: true,
            taskList: true,
            tocm: true,
            tex: true,
            flowChart: true,
            sequenceDiagram: true,
            imageUpload: true,
            imageFormats: ["jpg", "jpeg", "gif", "png", "svg", "webp"],
            imageUploadURL: "#{ netuno.config.urlAdmin }com/TextMD#{ netuno.config.extension }?com_uid=#{ comUid }",
            onfullscreen: ()->
              editor.editor.css('z-index', 10000)
            onfullscreenExit: ()->
              editor.editor.css('z-index', 0)
            onload: ()->
              this.unwatch()
              this.previewed()
            lang   : {
              name : "en",
              description : "Open source online Markdown editor.",
              tocTitle    : "Table of Contents",
              toolbar : {
                undo             : "Undo(Ctrl+Z)",
                redo             : "Redo(Ctrl+Y)",
                bold             : "Bold",
                del              : "Strikethrough",
                italic           : "Italic",
                quote            : "Block quote",
                ucwords          : "Words first letter convert to uppercase",
                uppercase        : "Selection text convert to uppercase",
                lowercase        : "Selection text convert to lowercase",
                h1               : "Heading 1",
                h2               : "Heading 2",
                h3               : "Heading 3",
                h4               : "Heading 4",
                h5               : "Heading 5",
                h6               : "Heading 6",
                "list-ul"        : "Unordered list",
                "list-ol"        : "Ordered list",
                hr               : "Horizontal rule",
                link             : "Link",
                "reference-link" : "Reference link",
                image            : "Image",
                code             : "Code inline",
                "preformatted-text" : "Preformatted text / Code block (Tab indent)",
                "code-block"     : "Code block (Multi-languages)",
                table            : "Tables",
                datetime         : "Datetime",
                emoji            : "Emoji",
                "html-entities"  : "HTML Entities",
                pagebreak        : "Page break",
                watch            : "Unwatch",
                unwatch          : "Watch",
                preview          : "HTML Preview (Press Shift + ESC exit)",
                fullscreen       : "Fullscreen (Press ESC exit)",
                clear            : "Clear",
                search           : "Search",
                help             : "Help",
                info             : "About "
              },
              buttons : {
                enter  : "Enter",
                cancel : "Cancel",
                close  : "Close"
              },
              dialog : {
                link : {
                  title    : "Link",
                  url      : "Address",
                  urlTitle : "Title",
                  urlEmpty : "Error: Please fill in the link address."
                },
                referenceLink : {
                  title    : "Reference link",
                  name     : "Name",
                  url      : "Address",
                  urlId    : "ID",
                  urlTitle : "Title",
                  nameEmpty: "Error: Reference name can't be empty.",
                  idEmpty  : "Error: Please fill in reference link id.",
                  urlEmpty : "Error: Please fill in reference link url address."
                },
                image : {
                  title    : "Image",
                  url      : "Address",
                  link     : "Link",
                  alt      : "Title",
                  uploadButton     : "Upload",
                  imageURLEmpty    : "Error: picture url address can't be empty.",
                  uploadFileEmpty  : "Error: upload pictures cannot be empty!",
                  formatNotAllowed : "Error: only allows to upload pictures file, upload allowed image file format:"
                },
                preformattedText : {
                  title             : "Preformatted text / Codes", 
                  emptyAlert        : "Error: Please fill in the Preformatted text or content of the codes.",
                  placeholder       : "coding now...."
                },
                codeBlock : {
                  title             : "Code block",         
                  selectLabel       : "Languages: ",
                  selectDefaultText : "select a code language...",
                  otherLanguage     : "Other languages",
                  unselectedLanguageAlert : "Error: Please select the code language.",
                  codeEmptyAlert    : "Error: Please fill in the code content.",
                  placeholder       : "coding now...."
                },
                htmlEntities : {
                  title : "HTML Entities"
                },
                help : {
                  title : "Help"
                }
              }
            }
        })
      , 250)
  }

netuno.com['select'] =
  callbacks: []
  getConfig: (fieldId, service, ajaxParams) ->
    netuno.com.select.loadCallbacksForFieldId(fieldId)
    delayCallbacks = new Date().getTime()
    field = $("\##{ fieldId }")
    placeholder = netuno.config.com.lang.select["defaulttext"]
    if field.attr("multiple")
      placeholder = $('<div />').html(placeholder).text()
    # dropdownParent: field.parent()
    return {
      theme: "bootstrap"
      placeholder: placeholder
      allowClear: true,
      data: [ ]
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
        $("\##{ fieldId }").parent().parent().removeClass("has-error")
        if (new Date().getTime() < delayCallbacks + 250)
          if item.label
            return item.label
          else
            return item.text
        for key of netuno.com.select.callbacks[fieldId]
          callback = netuno.com.select.callbacks[fieldId][key]
          if ($.isFunction(callback))
            callback(item.id)
            delayCallbacks = new Date().getTime()
        if item.label
          return item.label
        else
          return item.text
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
    select2 = $("\##{ fieldId }").select2(netuno.com.select.getConfig(fieldId, service, { com_uid: comUid }))
    return select2
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
          option = $("<option selected>#{netuno.config.com.lang.select["searching"]}</option>").val(value)
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
              selectContainer = $("\#select2-#{ selectId }-container")
              selectContainer.contents().filter(()->
                this.nodeType == 3
              )[0].nodeValue = $('<div />').html(data.label).text()
              #selectContainer.html(data.label)
              #selectContainer.prepend($(document.createElement("span")).addClass('select2-selection__clear').text('×').data(data).on('click', (e)->
              #  select = $("\##{ selectId }")
              #  select.attr("value", "")
              #  select.empty()
              #  netuno.com.select.loadInContainer(select.parent())
              #  e.preventDefault()
              #))
              #selectContainer.find('.select2-selection__clear').data(data)
            else
              selectId = select.attr('id')
              selectContainer = $("\#select2-#{ selectId }-container")
              selectContainer.contents().filter(()->
                this.nodeType == 3
              )[0].nodeValue = $('<div />').html(netuno.config.com.lang.select["defaulttext"]).text()
          )
    )
    container.find("select[netuno-select-link]").each(()->
      link = $(this).attr('netuno-select-link')
      columnSeparator = $(this).attr('netuno-select-column-separator')
      maxColumnLength = $(this).attr('netuno-select-max-column-length')
      onlyActives = $(this).attr('netuno-select-only-actives')
      service = $(this).attr('netuno-select-service')
      if link? and link isnt ''
        select2 = $(this).select2(netuno.com.select.getConfig($(this).attr('id'), service, {
          link: link,
          column_separator: columnSeparator,
          max_column_length: maxColumnLength,
          only_actives: onlyActives
        }))
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
          option = $("<option selected>#{netuno.config.com.lang.select["searching"]}</option>").val(value)
          select.append(option)
          select.trigger('change')
          $.ajax({
            dataType: "jsonp"
            url: "#{ service }&data_uid=#{ value }"
          }).then((data) ->
            if data? and data.label? and data.id?
              option.html(data.label).val(data.id)
              option.removeData()
              select.trigger('change')
              selectId = select.attr('id')
              selectContainer = $("\#select2-#{ selectId }-container")
              selectContainer.contents().filter(()->
                this.nodeType == 3
              )[0].nodeValue = $('<div />').html(data.label).text()
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

netuno.com['multiselect'] =
  loadInContainer: (container)->
    container.find("select[netuno-multiselect-uid]").each(()->
      input = $(this)
      input.select2('destroy')
      value = input.attr('value')
      comUid = input.attr('netuno-multiselect-uid')
      service = input.attr('netuno-multiselect-service')
      if comUid? and comUid isnt ''
        select2 = input.select2(netuno.com.multiselect.getConfig(input.attr('id'), service, { com_uid: comUid }))
    )
  ,
  load: (fieldId, designId, referenceId) ->
    $("\##{fieldId}").select2(
      formatNoMatches: ()->
        return netuno.config.com.lang.multiselect["noresults"]
      ,
      formatSearching: ()->
        return netuno.config.com.lang.multiselect["searching"]
      ,
      placeholder: $('<div />').html(netuno.config.com.lang.multiselect["defaulttext"]).text(),
      ajax: {
        url: "com/MultiSelect.netuno",
        dataType: 'jsonp',
        data: (term, page)->
          return {
            id: designId,
            q: term,
            page: page,
            page_limit: 10
          }
        ,
        results: (data, page)->
          return {results: data}
      },
      id: (item)->
        return item.id
      ,
      initSelection: (element, callback)->
        dataIds = $(element).val()
        if (dataIds != "")
          $.ajax("com/MultiSelect.netuno", {
              data: {
                  id: designId,
                  dataids: dataIds,
                  referenceid: referenceId
              },
              dataType: "jsonp"
          }).done((data)->
            callback(data)
          )
      ,
      formatResult: (item)->
        return item.label
      ,
      formatSelection: (item)->
        return item.label
      ,
      escapeMarkup: (m)->
        return m
      ,
      multiple: true,
      tokenSeparators: [",", " "]
    )

netuno.com['date'] =
  load: (fieldId, container, callback) ->
    $("\##{ fieldId }").on('change', callback).datepicker({
      format: "yyyy-mm-dd",
      autoclose: true,
      todayHighlight: true,
      orientation: "auto"
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
      container.find("[type=\"checkbox\"]").each ()->
        $(this).bootstrapSwitch(
          size: "small"
          onText: "<i class=\"fa fa-check\"></i>"
          offText: "<i class=\"fa fa-close\"></i>"
        )

netuno.com['color'] =
  load: (fieldId) ->
    control = $("\##{ fieldId }").colorpicker()
    return control

netuno.com['image'] =
  load: (id)->
    upload =  $("\##{ id }")
    if ($("\##{ id }-value").val().length > 0)
      value = $("\##{ id }-value").val()
    else
      $("\##{ id }-btView").hide()
    upload.on("change", ()->
        input = $(this)
        numFiles = if input.get(0).files then input.get(0).files.length else 1
        label = input.val().replace(/\\/g, '/').replace(/.*\//, '')
        input.trigger('fileselect', [numFiles, label])
    )
    upload.on('fileselect', (event, numFiles, label) ->
        $("\##{ id }-null").val('false')
        input = $(this).parents('.input-group').find(':text')
        log = if numFiles > 1 then "#{numFiles } files selected" else label
        if (input.length > 0)
            input.val(log)
    )
  view: (id, url)->
    window.open(url, "_blank")
  clear: (id)->
    $("\##{ id }").val('').parents('.input-group').find(':text').val('')
    $("\##{ id }-fileFeedback").html('')
    $("\##{ id }-null").val('true')
    $("\##{ id }-preview").hide()
    $("\##{ id }-btView").hide()

netuno.com['file'] =
  load: (id)->
    upload =  $("\##{ id }")
    if ($("\##{ id }-value").val().length == 0)
      $("\##{ id }-btView").hide()
    upload.on("change", ()->
        input = $(this)
        numFiles = if input.get(0).files then input.get(0).files.length else 1
        label = input.val().replace(/\\/g, '/').replace(/.*\//, '')
        input.trigger('fileselect', [numFiles, label])
    )
    upload.on('fileselect', (event, numFiles, label) ->
        $("\##{ id }-null").val('false')
        input = $(this).parents('.input-group').find(':text')
        log = if numFiles > 1 then "#{numFiles } files selected" else label
        if (input.length > 0)
            input.val(log)
    )
  view: (id)->
    value = $("\##{ id }-value")
    window.open(value.attr('file-url'))
  clear: (id)->
    $("\##{ id }").val('').parents('.input-group').find(':text').val('')
    $("\##{ id }-fileFeedback").html('')
    $("\##{ id }-null").val('true')
    $("\##{ id }-btView").hide()
  restore: (id)->

$(document).ready ()->
  netuno.pageLoaded()
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
