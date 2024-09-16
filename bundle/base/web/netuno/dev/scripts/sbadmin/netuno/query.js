
netuno.query = { form: { history: null, stored: null, load: null } };

(function () {
    netuno.query.form.history = function () {
        var historyModal = $("#managementQueryModalHistory");
        var historyPage = 0;
        function historyModalLoadPage() {
            var historyModalBody = historyModal.find(".modal-body");
            historyModalBody.load(`${ netuno.config.urlAdmin }dev/Query${ netuno.config.extension }`, {
                action: 'history',
                page: historyPage,
            }, ()=> {
                netuno.contentLoaded(historyModalBody);
                var items = historyModalBody.children('div');
                historyModal.find('.modal-footer button[data-type="next"]').hide();
                if (items.length == 0) {
                    historyModalBody.text('No more history...');
                } else if (items.length == 10) {
                    historyModal.find('.modal-footer button[data-type="next"]').show();
                }
                if (historyPage > 0) {
                    historyModal.find('.modal-footer button[data-type="prev"]').show();
                } else {
                    historyModal.find('.modal-footer button[data-type="prev"]').hide();
                }
                items.each(function () {
                    var item = $(this);
                    item.on('click', function () {
                        var commands = $('#managementQueryCommands');
                        if (commands.data().editor.getValue().trim() == '') {
                            commands.data().editor.setValue(
                                item.children('pre').text()
                            );
                        } else {
                            commands.data().editor.setValue(
                                commands.data().editor.getValue()
                                + '\n'
                                + ';;'
                                + '\n\n'
                                + item.children('pre').text()
                            );
                        }
                        historyModal.modal('hide');
                    });
                });
                historyModal[0].scrollTo(0, 0);
            });
        }
        $('#managementQueryButtonHistory').on("click", function () {
            historyPage = 0;
            historyModal.find('.modal-footer button[data-type="prev"]').hide();
            historyModal.find('.modal-footer button[data-type="next"]').show();
            historyModalLoadPage();
        });
        historyModal.find('.modal-footer button[data-type="prev"]').on("click", function () {
            historyPage--;
            historyModalLoadPage();
        });
        historyModal.find('.modal-footer button[data-type="next"]').on("click", function () {
            historyPage++;
            historyModalLoadPage();
        });
    };
})();

(function () {
    netuno.query.form.stored = function () {
        var saveModal = $('#managementQueryModalSave');
        var saveModalButton = saveModal.find('.modal-footer button');
        var saveModalTitle = saveModal.find('.modal-title').text();
        saveModalButton.on("click", function () {
            var commandField = $('#managementQueryModalSaveForm_command');
            var nameField = $('#managementQueryModalSaveForm_name');
            var command = $('#managementQueryCommands').data().editor.getValue();
            if (nameField.val().trim() == '') {
                toastr["warning"](nameField.data().required, saveModalTitle);
                return;
            }
            if (command.trim() == '') {
                toastr["warning"](commandField.data().required, saveModalTitle);
                return;
            }
            var that = $(this);
            that.prop('disabled', true);
            $('#managementQueryModalSaveForm_command').val(command);
            netuno.submitDev('managementQueryModalSaveContainer', 'managementQueryModalSaveForm', false, function () {
                that.prop('disabled', false);
                toastr["success"]($('#managementQueryModalSaveForm').data().success, saveModalTitle);
                saveModal.modal('hide');
            });
        });

        var storedModal = $("#managementQueryModalStored");
        var storedPage = 0;
        function storedModalLoadPage() {
            var storedModalBody = storedModal.find(".modal-body");
            storedModalBody.load(`${ netuno.config.urlAdmin }dev/Query${ netuno.config.extension }`, {
                action: 'stored',
                page: storedPage,
            }, ()=> {
                netuno.contentLoaded(storedModalBody);
                var items = storedModalBody.children('div');
                storedModal.find('.modal-footer button[data-type="next"]').hide();
                if (items.length == 0) {
                    storedModalBody.text('No more query stored...');
                } else if (items.length == 10) {
                    storedModal.find('.modal-footer button[data-type="next"]').show();
                }
                if (storedPage > 0) {
                    storedModal.find('.modal-footer button[data-type="prev"]').show();
                } else {
                    storedModal.find('.modal-footer button[data-type="prev"]').hide();
                }
                items.each(function () {
                    var item = $(this);
                    item.find('pre').on('click', function () {
                        var commands = $('#managementQueryCommands');
                        if (commands.data().editor.getValue().trim() == '') {
                            commands.data().editor.setValue(
                                item.children('pre').text()
                            );
                        } else {
                            commands.data().editor.setValue(
                                commands.data().editor.getValue()
                                + '\n'
                                + ';;'
                                + '\n'
                                + item.children('pre').text()
                            );
                        }
                        storedModal.modal('hide');
                    });
                    item.find('button[data-delete]').confirmation({
                        rootSelector: item,
                        container: storedModal.parent(),
                        title: null,
                        singleton: true,
                        onConfirm: function () {
                            $.post(`${ netuno.config.urlAdmin }dev/Query${ netuno.config.extension }`, {
                                action: 'delete',
                                uid: item.data().uid,
                            }, function () {
                                storedModalLoadPage();
                            });
                        }
                    });

                    item.find('button[data-delete]').on('click', function (e) {
                        e.stopImmediatePropagation();
                        return;
                        var deleteModal = $('#managementQueryModalStored_delete');
                        deleteModal.modal('show');
                        $('#managementQueryModalStored_delete_yes').on('click', function () {
                            var that = $(this);
                            that.prop('disabled', true);
                            that.off('click');
                            $.post(`${ netuno.config.urlAdmin }dev/Query${ netuno.config.extension }`, {
                                action: 'delete',
                                uid: item.data().uid,
                            }, function () {
                                storedModalLoadPage();
                                deleteModal.modal('hide');
                                that.prop('disabled', false);
                            });
                        });
                    });
                });
                storedModal[0].scrollTo(0, 0);
            });
        }
        $('#managementQueryButtonStored').on("click", function () {
            storedPage = 0;
            storedModal.find('.modal-footer button[data-type="prev"]').hide();
            storedModal.find('.modal-footer button[data-type="next"]').show();
            storedModalLoadPage();
        });
        storedModal.find('.modal-footer button[data-type="prev"]').on("click", function () {
            storedPage--;
            storedModalLoadPage();
        });
        storedModal.find('.modal-footer button[data-type="next"]').on("click", function () {
            storedPage++;
            storedModalLoadPage();
        });
    };
})();

(function () {
    netuno.query.form.load = function () {
        netuno.query.form.history();
        netuno.query.form.stored();
    };
})();