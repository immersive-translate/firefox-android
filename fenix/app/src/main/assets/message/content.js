
// 运行在插件环境
var curTab;
var sessionId;
const pageId = Math.random().toString(36).substr(2);

// 监听background消息
browser.runtime.onMessage.addListener((nativeMessage, sender) => {
    if(curTab.id != nativeMessage.tabId) {
        return;
    }
    if(nativeMessage["sessionId"]) {
        sessionId = nativeMessage["sessionId"];
    }
    window.postMessage({
        type: "imt_bridge_content",
        data: nativeMessage,
        pageId: pageId
    }, "*");
});

// 监听webpage发来的消息
window.addEventListener("message", (event) => {
    // 只接受来自当前页面的消息，避免安全问题
    if (event.source !== window || !event.data.type) return;
    if (event.data.pageId !== pageId) return;

    if (event.data.type === "imt_bridge_page") {
        const responseData = event.data.data;
        if(!responseData["sessionId"] && sessionId) {
            responseData["sessionId"] = sessionId;
        }
        sendMessageToNative(event.data.data);
    }
});

// 给native发消息
function sendMessageToNative(sendData) {
    const data = {
        msgType: "sendMsgToNative",
        data: sendData
    };
    browser.runtime.sendMessage(data, function() {});
}

browser.runtime.sendMessage({
    msgType: "pageLoad"
}, function(tabs) {
    if(tabs.length == 1) {
        curTab = tabs[0];
    } else {
        for(let tab of tabs) {
            if(tab.url == window.location.href && tab.active) {
                curTab = tab;
                break;
            }
        }
    }
    if(curTab) {
        window.wrappedJSObject.imt_tab = cloneInto(curTab, window, {cloneFunctions: true});
        window.wrappedJSObject.imt_pageId = cloneInto(pageId, window, {cloneFunctions: true});
    }
});

// 注入 bridge.js 到 webpage
const script = document.createElement("script");
script.src = browser.runtime.getURL("bridge.js");
(document.head || document.documentElement).appendChild(script);
script.remove();

