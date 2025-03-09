
const port = browser.runtime.connectNative("imt_message_bridge");

// 监听native消息
port.onMessage.addListener(nativeMessage => {
    const tabId = nativeMessage["tabId"];
    if(tabId) {
         browser.tabs.sendMessage(tabId ,nativeMessage);
    } else {
        findCurrentTab((tab) => {
            if(!tab) {
                return;
            }
            nativeMessage.tabId = tab.id;
            browser.tabs.sendMessage(tab.id, nativeMessage);
        });
    }
});

function findCurrentTab(callback) {
    browser.tabs.query({}).then(function(tabs) {
        var curTab;
        if(tabs.length == 1) {
            curTab = tabs[0];
        } else {
            for(let tab of tabs) {
                if(tab.active) {
                    curTab = tab;
                    break;
                }
            }
        }
        callback(curTab);
    });
}

// 给native发消息
function sendMessageToNative(sendData) {
    port.postMessage(sendData);
}

// 接受来自 content.js 的消息
browser.runtime.onMessage.addListener((data, sender, callback) => {
   const msgType = data.msgType;
   if(msgType === "pageLoad") {
        callback(browser.tabs.query({}));
   } else if(msgType === "sendMsgToNative") {
        sendMessageToNative(data.data);
        callback();
   }
});

