
(function() {
  const style = {
    table: {
      empty: {
        textAlign: 'center',
        padding: '10px 10px',
        backgroundColor: '#f1f1f1',
      },
      tr: {
        ':hover': {
          backgroundColor: '#f1f1f1',
        },
        '& > td': {
          padding: '10px 10px',
          verticalAlign: 'middle'
        },
        '& > td > pre': {
          margin: '0',
          padding: '0 5px',
          minHeight: '10px',
        }
      },
      watch: {
        value: {
          paddingRight: '10px',
        },
      },
      execution: {
        code: {
          paddingRight: '10px',
          '> div:first-child': {
            display: 'flex',
            alignItems: 'center',
            gap: '5px',
            marginBottom: '5px',
            '> pre': {
              margin: '0',
              padding: '0 5px'
            }
          },
          '> div:last-child': {
            height: '80px'
          }
        }
      }
    },
    iconButton: {
      stepOver: {
        color: '#507B58',
        cursor: 'pointer',
        ':hover': {
          color: '#507B58',
          textShadow: '0 0 5px #00AA00'
        }
      },
      watch: {
        color: '#FB952F',
        cursor: 'pointer',
        ':hover': {
          color: '#FB952F',
          textShadow: '0 0 5px #FFAA00'
        }
      },
      code: {
        color: '#32466F',
        cursor: 'pointer',
        ':hover': {
          color: '#32466F',
          textShadow: '0 0 5px #0000FF'
        }
      },
      trash: {
        color: '#AB3131',
        cursor: 'pointer',
        ':hover': {
          color: '#AB3131',
          textShadow: '0 0 5px #FF4444'
        }
      }
    }
  };

  netuno.debug = function () {
    const getColumn = {
      id: (id) => {
        return { td: { _: id } };
      },
      script: (script) => {
        let name = script.file;
        if (name.indexOf('/') >= 0) {
          name = name.substring(name.lastIndexOf('/') + 1);
        }
        return { td: {
          _: { div: {
              class: 'hint--top ',
              'aria-label': script.file + '.' + script.extension,
              _: name + '.' + script.extension
          } }
        } };
      },
      moment: (moment) => {
        return { td: {
          _: { div: {
            class: 'hint--top ',
            'aria-label': moment,
            _: moment.substring(moment.indexOf(' ') + 1)
          } }
        } };
      }
    }

    const ContextItem = com(function ContextItem({lang, context, onRemove}) {
      this.view(() => {
        return [
          getColumn.id(context.id),
          getColumn.script(context.script),
          getColumn.moment(context.moment),
          { td: { $: [
            { a: {
              class: 'hint--top '+ css(style.iconButton.stepOver),
              'aria-label': lang.actions.stepOver,
              onClick: ()=> {
                trigger('ws:debug:send:step-over', context.id);
                onRemove(context.id);
              },
              _: { i: { class: 'fa-solid fa-forward' } }
            } },
            ' &nbsp; ',
            { a: {
              class: 'hint--top '+ css(style.iconButton.watch),
              'aria-label': lang.actions.watch,
              onClick: ()=> {
                const modal = $('#debugWatchModal');
                modal.modal('show');
                modal.data({id: context.id});
              },
              _: { i: { class: 'fa-solid fa-magnifying-glass' } }
            } },
            ' &nbsp; ',
            { a: {
              class: 'hint--top '+ css(style.iconButton.code),
              'aria-label': lang.actions.executeCode,
              onClick: ()=> {
                const modal = $('#debugCodeExecutionModal');
                modal.modal('show');
                modal.data({id: context.id});
              },
              _: { i: { class: 'fa-solid fa-terminal' } }
            } },
          ] } }
        ];
      });
    });

    const ContextList = com(function ContextList({lang}) {
      const contexts = this.state([]);
      this.mount(()=> {
        trigger.add('ws:debug:receive:contexts', (_contexts)=> {
          if (_contexts) {
            contexts.$val = _contexts;
          }
        });
        trigger.add('ws:debug:receive:new-context', (data)=> {
          contexts.$val = [...contexts.val, data.context];
        });
      });
      const onRemove = (id) => {
        contexts.$val = contexts.val.filter((c)=> c.id !== id);
      };
      this.view(() => {
        if (contexts.val.length === 0) {
          return { td: {
            class: css(style.table.empty),
            colspan: '4',
            _: lang.empty
          } };
        }
        const trs = [];
        for (const context of contexts.val) {
          trs.push({ tr: {
              class: css(style.table.tr),
              _: { [ContextItem]: { lang, context, onRemove } }
            } });
        }
        return trs;
      });
    });

    (function WatchModal() {
      const modal = $('#debugWatchModal');
      const look = modal.find('.modal-footer button');
      const name = modal.find('.modal-body input');
      look.on("click", ()=> {
        trigger('ws:debug:send:watch', modal.data().id, name.val());
        name.val('');
        modal.modal('toggle');
      });
    })();

    const WatchItem = com(function WatchItem({lang, item, onRemove}) {
      console.log('watch list', lang)
      this.view(() => {
        return [
          getColumn.id(item.id),
          getColumn.script(item.script),
          getColumn.moment(item.moment),
          { td: { _: item.watch } },
          { td: {
            class: css(style.table.watch.value),
            _: { pre: { _: item.value } }
          } },
          { td: { $: [
            { a: {
              class: 'hint--top '+ css(style.iconButton.trash),
              'aria-label': lang.actions.remove,
              onClick: ()=> {
                onRemove(item);
              },
              _: { i: { class: 'fa-solid fa-trash' } }
            } },
          ] } }
        ];
      });
    });

    const WatchList = com(function WatchList({lang}) {
      const list = this.state([]);
      this.mount(()=> {
        trigger.add('ws:debug:receive:watch', (data)=> {
          if (data) {
            list.$val = [data, ...list.val];
          }
        });
      });
      const onRemove = ({id, watch, moment}) => {
        list.$val = list.val.filter(({id: _id, watch: _watch, moment: _moment}) => {
          return _id !== id || _watch !== watch || _moment !== moment;
        });
      };
      this.view(() => {
        if (list.val.length === 0) {
          return { td: {
              class: css(style.table.empty),
              colspan: '6',
              _: lang.empty
            } };
        }
        const trs = [];
        for (const item of list.val) {
          trs.push({ tr: {
              class: css(style.table.tr),
              _: { [WatchItem]: { lang, item, onRemove } }
            } });
        }
        return trs;
      });
    });

    (function CodeExecutionModal() {
      const modal = $('#debugCodeExecutionModal');
      const run = modal.find('.modal-footer button');
      const editor = $('#debugCodeExecutionModal_editor');
      run.on("click", ()=> {
        trigger('ws:debug:send:execute', modal.data().id, editor.data().editor.getValue());
        editor.data().editor.setValue('');
        modal.modal('toggle');
      });
    })();

    const CodeExecutionItem = com(function CodeExecutionItem({lang, item, onRemove}) {
      console.log('code execution', lang)
      const editor = this.ref();
      let aceEditor = null;
      this.changes([editor], () => {
        aceEditor = ace.edit(editor.current);
        aceEditor.setTheme("ace/theme/dracula");
        aceEditor.session.setMode("ace/mode/javascript");
        aceEditor.session.setUseWorker(false);
        aceEditor.setShowPrintMargin(false);
        aceEditor.setReadOnly(true);
      });
      this.unmount(() => {
        aceEditor.container.remove();
      });
      this.view(() => {
        return [
          getColumn.id(item.id),
          getColumn.script(item.script),
          getColumn.moment(item.moment),
          { td: {
            class: css(style.table.execution.code),
            _: [
              { div: {
                $: [
                  { i: {
                    class: 'fa-solid '+ (item.error ? 'fa-square-xmark' : 'fa-square-check')
                        +' '+ css({color: item.error ? '#AB3131' : '#507B58'})
                  } },
                  item.error ? { pre: { _: item.message } } : ''
                ]
              } },
              editor.set({ div: { _: item.code } })
            ]
          } },
          { td: { $: [
            { a: {
              class: 'hint--top '+ css(style.iconButton.trash),
              'aria-label': lang.actions.remove,
              onClick: ()=> {
                onRemove(item);
              },
              _: { i: { class: 'fa-solid fa-trash' } }
            } },
          ] } }
        ];
      });
    });

    const CodeExecutionList = com(function CodeExecutionList({lang}) {
      const list = this.state([]);
      this.mount(()=> {
        trigger.add('ws:debug:receive:execute', (data)=> {
          if (data) {
            list.$val = [data, ...list.val];
          }
        });
      });
      const onRemove = ({id, code, moment}) => {
        list.$val = list.val.filter(({id: _id, code: _code, moment: _moment}) => {
          return _id !== id || _code !== code || _moment !== moment;
        });
      };
      this.view(() => {
        if (list.val.length === 0) {
          return { td: {
              class: css(style.table.empty),
              colspan: '5',
              _: lang.empty
            } };
        }
        const trs = [];
        for (const item of list.val) {
          trs.push({ tr: {
              class: css(style.table.tr),
              _: { [CodeExecutionItem]: { lang, item, onRemove } }
            } });
        }
        return trs;
      });
    });

    const elemContextsList = document.getElementById('debugContexts');
    com.create(
        elemContextsList,
        ContextList,
        {
          lang: {
            empty: elemContextsList.getAttribute('data-lang-empty'),
            actions: {
              stepOver: elemContextsList.getAttribute('data-lang-actions-step_over'),
              watch: elemContextsList.getAttribute('data-lang-actions-watch'),
              executeCode: elemContextsList.getAttribute('data-lang-actions-execute_code'),
            }
          }
        }
    );

    const elemWatchList = document.getElementById('debugWatchList');
    com.create(
        elemWatchList,
        WatchList,
        {
          lang: {
            empty: elemWatchList.getAttribute('data-lang-empty'),
            actions: {
              remove: elemWatchList.getAttribute('data-lang-actions-remove'),
            }
          }
        }
    );

    const elemCodeExecutionList = document.getElementById('debugCodeExecutionList');
    com.create(
        elemCodeExecutionList,
        CodeExecutionList,
        {
          lang: {
            empty: elemCodeExecutionList.getAttribute('data-lang-empty'),
            actions: {
              remove: elemCodeExecutionList.getAttribute('data-lang-actions-remove'),
            }
          }
        }
    );

    let prefixURL = netuno.config.urlAdmin;
    if (prefixURL.lastIndexOf('/') === prefixURL.length - 1) {
      prefixURL = prefixURL.substring(0, prefixURL.lastIndexOf('/'));
    }

    const socket = new WebSocket((location.protocol === "https:" ? "wss:" : "ws:") + "//" + location.host + prefixURL + "/dev/ws/");

    socket.addEventListener("open", (event) => {
      trigger('ws:debug:send:contexts');
    });

    socket.addEventListener("message", (event) => {
      const data = JSON.parse(event.data);
      if (data.section === 'debug') {
        if (data.action === 'contexts') {
          trigger('ws:debug:receive:contexts', data.contexts);
        } else if (data.action === 'new-context') {
          trigger('ws:debug:receive:new-context', data);
        } else if (data.action === 'watch') {
          trigger('ws:debug:receive:watch', data);
        } else if (data.action === 'execute') {
          trigger('ws:debug:receive:execute', data);
        }
      }
    });

    trigger.add('ws:debug:send:contexts', ()=> {
      socket.send(JSON.stringify({
        section: 'debug',
        action: 'contexts'
      }));
    });

    trigger.add('ws:debug:send:step-over', (id)=> {
      socket.send(JSON.stringify({
        section: 'debug',
        action: 'step-over',
        id
      }));
    });

    trigger.add('ws:debug:send:watch', (id, watch) => {
      socket.send(JSON.stringify({
        section: 'debug',
        action: 'watch',
        id,
        watch
      }));
    });

    trigger.add('ws:debug:send:execute', (id, code) => {
      socket.send(JSON.stringify({
        section: 'debug',
        action: 'execute',
        id,
        code
      }));
    });

    (() =>  {
      const divEditor = $('#debugCodeExecutionModal_editor');
      const editor = ace.edit(divEditor[0]);
      editor.setTheme("ace/theme/dracula");
      editor.session.setMode("ace/mode/javascript");
      divEditor.data({editor});
    })()
  };
})();
