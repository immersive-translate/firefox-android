//notation: js file can only use this kind of comments
//since comments will cause error when use in webview.loadurl,
//comments will be remove by java use regexp

// 隐藏悬浮球
function insertHiddenPagePopupMeta() {
  try {
    // 如果已存在相同的meta标签，直接返回
    if (document.querySelector('meta[name="immersive-translate-show-page-popup"]')) {
      return;
    }
    const meta = document.createElement('meta');
    meta.name = "immersive-translate-show-page-popup";
    meta.content = "no";
    // 确保DOM已经准备好
    if (document.head) {
      document.head.appendChild(meta);
    } else if (document.body) {
      document.body.appendChild(meta);
    } else {
      // 如果DOM还没准备好，等待DOM加载完成
      document.addEventListener('DOMContentLoaded', () => {
        (document.head || document.body).appendChild(meta);
      });
    }
  } catch (error) {
    console.error('Failed to insert meta tag:', error);
  }
}
// insertHiddenPagePopupMeta();

(function() {
    if (window.WebViewJavascriptBridge && window.WebViewJavascriptBridge.inited) {
        return;
    }

    var receiveMessageQueue = [];
    var messageHandlers = {};
    var sendMessageQueue = [];

    var responseCallbacks = {};
    var uniqueId = 1;

    var lastCallTime = 0;
    var stoId = null;
    var FETCH_QUEUE_INTERVAL = 20;
    var messagingIframe;
    var CUSTOM_PROTOCOL_SCHEME = "yy";
    var QUEUE_HAS_MESSAGE = "__QUEUE_MESSAGE__";

    // 创建消息index队列iframe
    function _createQueueReadyIframe() {
        messagingIframe = document.createElement('iframe');
        messagingIframe.style.display = 'none';
        messagingIframe.src = CUSTOM_PROTOCOL_SCHEME + '://' + QUEUE_HAS_MESSAGE;
        document.documentElement.appendChild(messagingIframe);
    }
    //创建消息体队列iframe
    function _createQueueReadyIframe4biz() {
        bizMessagingIframe = document.createElement('iframe');
        bizMessagingIframe.style.display = 'none';
        document.documentElement.appendChild(bizMessagingIframe);
    }
    //set default messageHandler  初始化默认的消息线程
    function init(messageHandler) {
        if (WebViewJavascriptBridge._messageHandler) {
            throw new Error('WebViewJavascriptBridge.init called twice');
        }
        WebViewJavascriptBridge._messageHandler = messageHandler;
        var receivedMessages = receiveMessageQueue;
        receiveMessageQueue = null;
        for (var i = 0; i < receivedMessages.length; i++) {
            _dispatchMessageFromNative(receivedMessages[i]);
        }
    }

    // 发送
    function send(data, responseCallback) {
        _doSend('', data, responseCallback);
    }

    // 注册线程 往数组里面添加值
    function registerHandler(handlerName, handler) {
        messageHandlers[handlerName] = handler;
    }

    function removeHandler(handlerName, handler) {
        delete messageHandlers[handlerName];
    }

    // 调用线程
    function callHandler(handlerName, data, responseCallback) {
        // 如果方法不需要参数，只有回调函数，简化JS中的调用
        if (arguments.length == 2 && typeof data == 'function') {
			responseCallback = data;
			data = null;
		}
        _doSend(handlerName, data, responseCallback);
    }

    //sendMessage add message, 触发native处理 sendMessage
    function _doSend(handlerName, message, responseCallback) {
        var callbackId;
        var msg = {
            handlerName: handlerName,
        };
        if(!message) {
            message = {};
        }
        if(typeof responseCallback === 'string'){
            callbackId = responseCallback;
            msg.responseId = callbackId;
            msg.responseData = message;
        } else if (responseCallback) {
            callbackId = 'cb_' + (uniqueId++) + '_' + new Date().getTime();
            responseCallbacks[callbackId] = responseCallback;
            // message.callbackId = callbackId;
            msg.callbackId = callbackId;
            msg.data = message;
        }else{
            callbackId = '';
        }
        try {
             var fn = eval('WebViewJavascriptBridge.' + handlerName);
         } catch(e) {
             console.log(e);
         }
         if (typeof fn === 'function'){
             var responseData = fn.call(WebViewJavascriptBridge, JSON.stringify(message), callbackId);
             if(responseData){
                 responseCallback = responseCallbacks[callbackId];
                 if (!responseCallback) {
                     return;
                  }
                 responseCallback(responseData);
                 delete responseCallbacks[callbackId];
             }
         }
        sendMessageQueue.push(msg);
        //messagingIframe.src = CUSTOM_PROTOCOL_SCHEME + '://' + QUEUE_HAS_MESSAGE;
        var messageString = CUSTOM_PROTOCOL_SCHEME + '://' + QUEUE_HAS_MESSAGE;
        alert(messageString);
    }

    // 提供给native调用,该函数作用:获取sendMessageQueue返回给native,由于android不能直接获取返回的内容,所以使用url shouldOverrideUrlLoading 的方式返回内容
    function _fetchQueue() {
        // 空数组直接返回
        if (sendMessageQueue.length === 0) {
          return;
        }

        // _fetchQueue 的调用间隔过短，延迟调用
        if (new Date().getTime() - lastCallTime < FETCH_QUEUE_INTERVAL) {
          if (!stoId) {
            stoId = setTimeout(_fetchQueue, FETCH_QUEUE_INTERVAL);
          }
          return;
        }

        lastCallTime = new Date().getTime();
        stoId = null;
        var messageQueueString = JSON.stringify(sendMessageQueue);
        sendMessageQueue = [];
        //android can't read directly the return data, so we can reload iframe src to communicate with java
        //bizMessagingIframe.src = CUSTOM_PROTOCOL_SCHEME + '://return/_fetchQueue/' + encodeURIComponent(messageQueueString);
        var messageString = CUSTOM_PROTOCOL_SCHEME + '://return/_fetchQueue/' + encodeURIComponent(messageQueueString);
        alert(messageString);
    }

    //提供给native使用,
    function _dispatchMessageFromNative(messageJSON) {
        setTimeout(function() {
            var message = JSON.parse(messageJSON);
            var responseCallback;
            //java call finished, now need to call js callback function
            if (message.responseId) {
                responseCallback = responseCallbacks[message.responseId];
                if (!responseCallback) {
                    return;
                }
                responseCallback(message.responseData);
                delete responseCallbacks[message.responseId];
            } else {
                //直接发送
                if (message.callbackId) {
                    var callbackResponseId = message.callbackId;
                    responseCallback = function(responseData) {
                        _doSend('response', responseData, callbackResponseId);
                    };
                }

                var handler = WebViewJavascriptBridge._messageHandler;
                if (message.handlerName) {
                    handler = messageHandlers[message.handlerName];
                }
                //查找指定handler
                try {
                    handler(message.data, responseCallback);
                } catch (exception) {
                    if (typeof console != 'undefined') {
                        console.log("WebViewJavascriptBridge: WARNING: javascript handler threw.", message, exception);
                    }
                }
            }
        });
    }

    //提供给native调用,receiveMessageQueue 在会在页面加载完后赋值为null,所以
    function _handleMessageFromNative(messageJSON) {
        if (receiveMessageQueue) {
            receiveMessageQueue.push(messageJSON);
        }
        _dispatchMessageFromNative(messageJSON);

    }

    _createQueueReadyIframe();
    _createQueueReadyIframe4biz();

    WebViewJavascriptBridge = {};
    WebViewJavascriptBridge.init = init;
    WebViewJavascriptBridge.doSend = send;
    WebViewJavascriptBridge.registerHandler = registerHandler;
    WebViewJavascriptBridge.callHandler = callHandler;
    WebViewJavascriptBridge._handleMessageFromNative = _handleMessageFromNative;
    WebViewJavascriptBridge._fetchQueue = _fetchQueue;

    var readyEvent = document.createEvent('Events');
    var jobs = window.WVJBCallbacks || [];
    readyEvent.initEvent('WebViewJavascriptBridgeReady');
    readyEvent.bridge = WebViewJavascriptBridge;
    window.WVJBCallbacks = [];
    jobs.forEach(function (job) {
        job(WebViewJavascriptBridge)
    });
    document.dispatchEvent(readyEvent);

    WebViewJavascriptBridge.inited = true;

})();

(function() {
    if (window.ImmTranslateBridge && window.ImmTranslateBridge.inited) {
        return;
    }
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
            // document.querySelector("#pageStatus").innerText = message.payload;
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
    window.WebViewJavascriptBridge.registerHandler("openMenu", function(data, responseCallback) {
        // openPopup
        sendMessage("togglePopup", {
            style : "right: unset; bottom: unset; left: 50%; top: 0; transform: translateX(-50%);",
            isSheet: false,
            overlayStyle: "background-color: transparent;"
        });
        responseCallback({
            result: true
        });
    });

    // 翻译页面
    window.WebViewJavascriptBridge.registerHandler("translatePage", function(data, responseCallback) {
        sendMessage("translatePage",{});
        responseCallback({
            result: true
        });
    });

    // 恢复页面
    window.WebViewJavascriptBridge.registerHandler("restorePage", function(data, responseCallback) {
        sendMessage("restorePage",{});
        responseCallback({
            result: true
        });
    });

    // 获取页面翻译状态
    window.WebViewJavascriptBridge.registerHandler("getPageStatus", async function(data, responseCallback) {
        const status = await sendAsyncMessage("getPageStatusAsync", {});
        responseCallback({
            result: true,
            pageTranslated: status == "Translated"
        });
    });

    ImmTranslateBridge = {};
    ImmTranslateBridge.inited = true;
})();
