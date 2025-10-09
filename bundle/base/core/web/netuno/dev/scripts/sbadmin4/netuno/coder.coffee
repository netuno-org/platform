
class NetunoCoder
  constructor: (container) ->
    @containers = {
      filesystem: {
        base: container.find("[netuno-coder-filesystem]"),
        notifications: container.find("[netuno-coder-notifications]"),
        tree: null,
        modal: null
      }
    }
    @initFileSystem()

  initFileSystem: () ->
    $.ajax
      url: "#{ netuno.config.urlBase }dev/coder/FileSystem#{ netuno.config.extension }",
      success: (response) =>
        @containers.filesystem.base.html(response);
        @containers.filesystem.tree = @containers.filesystem.base.find("[netuno-coder-filesystem-tree]")
        @containers.filesystem.createModal = @containers.filesystem.base.find("[netuno-coder-filesystem-create-modal]")
        @containers.filesystem.modal = @containers.filesystem.base.find("[netuno-coder-filesystem-modal]")

        @initFileSystemTree()

  initFileSystemTree: () ->
    $.ajax
      url: "#{ netuno.config.urlBase }dev/coder/FileSystem#{ netuno.config.extension }?action=tree",
      dataType: "json",
      success: (data) =>
        previousNodeClicked = null
        @containers.filesystem.tree.tree({
          data: data,
          autoOpen: true,
          dragAndDrop: true,
          selectable: true,
          onCreateLi: (node, $li) =>
            if (node.children.length > 0)
              $li
                .find('.jqtree-element')
                .append(
                  """ &middot; <a href="#node-#{ node.id }" class="create" data-node-id="#{node.id }"><i class="fa fa-plus-circle" /></a>"""
                )
        })
        @containers.filesystem.tree.find('.create').on('click', (e) =>
          e.preventDefault()
          a = $(e.currentTarget)
          node = @containers.filesystem.tree.tree('getNodeById', a.data().nodeId)
          @containers.filesystem.modal.find('.modal-title').text(node.name)
          @containers.filesystem.createModal.find('[name="folder"]').bootstrapSwitch {
            offText: 'NEW FILE',
            onText: 'NEW FOLDER'
          }
          @containers.filesystem.createModal.modal('show')

          if typeof @containers.filesystem.createModal.find('[type="file"]').data().blueimpFileupload != 'undefined'
            @containers.filesystem.createModal.find('[type="file"]').fileupload('destroy')
          @containers.filesystem.createModal.find('[type="file"]').fileupload({
            url: "#{ netuno.config.urlBase }dev/coder/FileSystem#{ netuno.config.extension }?action=upload&path=#{ a.data().nodeId }",
            dataType: 'json',
            done: (e, data) =>
              $.each(data.result.files, (index, file) =>
                $("<p/>").text(file.name).appendTo(document.body)
              )
            progressall: (e, data) =>
              progress = parseInt(data.loaded / data.total * 100, 10)
              @containers.filesystem.createModal.find('[class="progress"]').css(
                'width',
                "#{ progress }%"
              )
          })

          false
        )
        @containers.filesystem.tree.on 'tree.click', (e)=>
          e.preventDefault()
          console.log('node_clicked', e.node)
          @containers.filesystem.tree.tree('selectNode', e.node)
          if previousNodeClicked != null && e.node.id == previousNodeClicked.id
            if typeof e.node.parent.id != 'undefined'
              @containers.filesystem.modal.find('.netuno-dev-coder-filesystem-modal-path').text(e.node.parent.id).show()
            else
              @containers.filesystem.modal.find('.netuno-dev-coder-filesystem-modal-path').hide()
            @containers.filesystem.modal.find('.modal-title').text(e.node.name)
            @containers.filesystem.modal.find('[name="name"]').val(e.node.name)
            @containers.filesystem.modal.modal('show')
            @containers.filesystem.modal.find('.btn-primary').off('click').on 'click', ()=>
              $.ajax
                url: "#{ netuno.config.urlBase }dev/coder/FileSystem#{ netuno.config.extension }?action=rename",
                data: {
                  folder: e.node.parent.id,
                  from: e.node.name,
                  to: @containers.filesystem.modal.find('[name="name"]').val()
                }
                success: (response) =>
                  if response.result == true
                    e.node.id = "#{ e.node.parent.id }/#{ response.name }"
                    e.node.name = "#{ response.name }"
                    @containers.filesystem.tree.tree('moveNode', e.node, e.node.parent, 'inside')
                  @containers.filesystem.notifications.html(response.output)
                  @containers.filesystem.modal.modal('hide')
                fail: () =>
                  debugger
          previousNodeClicked = e.node
        @containers.filesystem.tree.on 'tree.select', (e)=>
          ###
          for node in @containers.filesystem.tree.tree('getSelectedNodes')
            console.log 'remove-node', node
            @containers.filesystem.tree.tree('removeFromSelection', node)
          console.log('node_selected', e.node)
          @containers.filesystem.tree.tree('selectNode', e.node)
          ###
        @containers.filesystem.tree.bind 'tree.move', (e)=>
          e.preventDefault()
          $.ajax
            url: "#{ netuno.config.urlBase }dev/coder/FileSystem#{ netuno.config.extension }?action=move",
            data: {
              from: e.move_info.moved_node.id,
              to: e.move_info.target_node.id
            }
            success: (response) =>
              if response.result == true
                e.move_info.moved_node.id = "#{ e.move_info.target_node.id }/#{ e.move_info.moved_node.name }"
                @containers.filesystem.tree.tree('moveNode', e.move_info.moved_node, e.move_info.target_node, 'inside');
              @containers.filesystem.notifications.html(response.output);
            fail: () =>
              debugger
      fail: () =>
        debugger

`window.NetunoCoder = NetunoCoder`