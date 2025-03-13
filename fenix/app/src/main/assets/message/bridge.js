
// 运行在 html webpage
(function() {
    if(window.ImtMessageBridge) {
        return;
    }
    ImtMessageBridge = {};
    // 监听content.js发来的消息
    window.addEventListener("message", (event) => {
        // 只接受来自当前页面的消息，避免安全问题
        if (event.source !== window || !event.data.type) return;
        if (event.data.pageId !== window.imt_pageId) return;

        if (event.data.type === "imt_bridge_content") {
            const nativeMessage = event.data.data;
            const msgType = nativeMessage.msgType;
            if(msgType === "request") {
                // native request
                handleNativeRequest(nativeMessage);
            } else if(msgType === "response") {
                // native response
                handleNativeResponse(nativeMessage);
            }
        }
    });

    const messageHandlers = {};
    const jsCallbacks = {};

    // 给native发消息
    function sendMessageToNative(sendData) {
        const data = {
            type: "imt_bridge_page",
            data: sendData,
            pageId: window.imt_pageId
        };
        window.postMessage(data, "*");
    }

    // 处理native消息
    function handleNativeRequest(nativeMessage) {
        const tabId = nativeMessage.tabId;
        const sessionId = nativeMessage.sessionId;
        const messageId = nativeMessage.messageId;
        const handlerName = nativeMessage.handlerName;
        const data = nativeMessage.data;
        const responseHandler = messageHandlers[handlerName];
        if(!responseHandler) {
            const returnData = {
                sessionId: sessionId,
                handlerName: handlerName,
                tabId: tabId,
                messageId: messageId,
                msgType: "response",
                data: {
                    message: "JS handler no register."
                }
            };
            sendMessageToNative(returnData);
            return;
        }
        // 调用js handler，然后回调数据
        responseHandler(data, function(responseData) {
            if (!responseData) {
                //return;
                responseData = {};
            }
            const callbackData = {
                sessionId: sessionId,
                handlerName: handlerName,
                tabId: tabId,
                messageId: messageId,
                msgType: "response",
                data: responseData
            };
            sendMessageToNative(callbackData);
        });
    }

    // 处理native回调
    function handleNativeResponse(nativeResponse) {
        const messageId = nativeResponse.messageId;
        const callbackObj = jsCallbacks[messageId];
        if (!callbackObj) {
            return;
        }
        const callback = callbackObj["responseCallback"];
        if(!callback) {
            return;
        }
        callback(nativeResponse);
    }

    // 注册线程 往数组里面添加值
    function registerHandler(handlerName, handler) {
        messageHandlers[handlerName] = handler;
    }

    // 调用线程
    function callHandler(handlerName, data, responseCallback) {
        if (!data) {
            data = {};
        }
        // 保存 callback
        const messageId = Math.random().toString(36).substr(2);
        const curTabId = window.imt_tab.id;
        const requestData = {
            handlerName: handlerName,
            tabId: curTabId,
            messageId: messageId,
            msgType: "request",
            data: data
        };
        const callback = {
            responseCallback: responseCallback
        };
        jsCallbacks[messageId] = Object.assign({}, requestData, callback);
        // 给native发消息
        sendMessageToNative(requestData);
    }

    ImtMessageBridge.registerHandler = registerHandler;
    ImtMessageBridge.callHandler = callHandler;
    ImtMessageBridge.isReady = true;
})();

(function() {
    if(window.WebViewJavascriptBridge) {
        return;
    }
    WebViewJavascriptBridge = {};

    function send(data, responseCallback) {
        const handlerName = data.type;
        delete data.type;
        callHandler(handlerName, data, responseCallback);
    }

    function callHandler(handlerName, data, responseCallback) {
        ImtMessageBridge.callHandler(handlerName, data, function(response) {
            responseCallback(response.data);
        });
    }

    function registerHandler(handlerName, handler) {
        //ImtMessageBridge.registerHandler(handlerName, handler);
        ImtMessageBridge.registerHandler(handlerName, function(data, responseCallback) {
            handler(data, function(responseData) {
                responseCallback(responseData);
            });
        });
    }

    WebViewJavascriptBridge.doSend = send;
    WebViewJavascriptBridge.registerHandler = registerHandler;
    WebViewJavascriptBridge.callHandler = callHandler;
    WebViewJavascriptBridge.inited = true;

    // 原生与插件交互，实现翻译以及菜单面板调用
    const documentMessageTypeIdentifierForThirdPartyTell = "immersiveTranslateDocumentMessageThirdPartyTell";
    const documentMessageTypeIdentifierForTellThirdParty = "immersiveTranslateDocumentMessageTellThirdParty";

    const messageHandler = (event) => {
          if (!event.detail) {
            return;
          }
          const message = JSON.parse(event.detail);
          if (message.type === "updatePageStatus") {
            window.WebViewJavascriptBridge.doSend({
                type: "updateTranslateState",
                pageTranslated: message.payload == "Translated"
            }, function(data) {});
          }
    };

    document.addEventListener(
      documentMessageTypeIdentifierForTellThirdParty,
      messageHandler,
    );

    function sendMessage(type, data) {
      document.dispatchEvent(
        new CustomEvent(documentMessageTypeIdentifierForThirdPartyTell, {
          detail: JSON.stringify({
            type: type,
            data: data,
          }),
        }),
      );
    }

    function sendAsyncMessage(type, data) {
      return new Promise((resolve, reject) => {
        const messageId = Math.random().toString(36).substr(2);
        const messageHandler = (event) => {
          if (!event.detail) {
            return;
          }
          const message = JSON.parse(event.detail);
          if (message.id === messageId) {
            document.removeEventListener(
              documentMessageTypeIdentifierForTellThirdParty,
              messageHandler,
            );
            resolve(message.payload);
          }
        };
        document.addEventListener(
          documentMessageTypeIdentifierForTellThirdParty,
          messageHandler,
        );
        document.dispatchEvent(
          new CustomEvent(documentMessageTypeIdentifierForThirdPartyTell, {
            detail: JSON.stringify({
              type: type,
              data: data,
              id: messageId,
            }),
          }),
        );
      });
    }

    // 打开菜单
    WebViewJavascriptBridge.registerHandler("openMenu", function(data, responseCallback) {
        // openPopup
        var menuStyle = "right: unset; bottom: unset; left: 50%; top: 0; transform: translateX(-50%);";
        if(window.innerWidth > 450) {
            menuStyle = "left: unset; top: 0; right: 20px; bottom: unset; transform: none;";
        }
        sendMessage("togglePopup", {
            style : menuStyle,
            isSheet: false,
            overlayStyle: "background-color: transparent;"
        });
        responseCallback({
            result: true
        });
    });

    // 翻译页面
    WebViewJavascriptBridge.registerHandler("translatePage", function(data, responseCallback) {
        sendMessage("translatePage",{});
        responseCallback({
            result: true
        });
    });

    // 恢复页面
    WebViewJavascriptBridge.registerHandler("restorePage", function(data, responseCallback) {
        sendMessage("restorePage",{});
        responseCallback({
            result: true
        });
    });

    // 获取页面翻译状态
    WebViewJavascriptBridge.registerHandler("getPageStatus", async function(data, responseCallback) {
        const status = await sendAsyncMessage("getPageStatusAsync", {});
        responseCallback({
            result: true,
            pageTranslated: status == "Translated"
        });
    });

    // 执行javascript
    WebViewJavascriptBridge.registerHandler("evalJavaScript", function(data, responseCallback) {
        const responseData = {};
        try {
            responseData.result = true;
            responseData.evalResult = window.eval(data.javascript);
        } catch(e) {
            responseData.result = false;
            responseData.error = e.toString();
        }
        responseCallback(responseData);
    });

    // 页面加载成功，自动同步页面翻译状态
    /*window.addEventListener("load", async function () {
        setTimeout(async function (){
            const status = await sendAsyncMessage("getPageStatusAsync", {});
            window.WebViewJavascriptBridge.doSend({
                type: "updateTranslateState",
                pageTranslated: status == "Translated"
            }, function(data) {});
        }, 1000);
    });*/

    // jsBridge 初始化成功 消息
    const readyEvent = document.createEvent('Events');
    readyEvent.initEvent('WebViewJavascriptBridgeReady');
    readyEvent.bridge = WebViewJavascriptBridge;
    document.dispatchEvent(readyEvent);
})();

