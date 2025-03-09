/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package org.mozilla.fenix.immersive_transalte.webmessage

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import mozilla.components.support.webextensions.WebExtensionSupport
import org.json.JSONObject
import org.mozilla.gecko.util.ThreadUtils
import org.mozilla.geckoview.WebExtension
import org.mozilla.geckoview.WebExtension.MessageDelegate
import org.mozilla.geckoview.WebExtension.PortDelegate
import org.mozilla.geckoview.WebExtensionController
import java.util.UUID

object WebMessageBridge {
    private val resourcePath = "resource://android/assets/message/"
    private val webExtensionId = "pengxiao18@163.com"
    private val portName = "imt_message_bridge"
    private var messagePort: WebExtension.Port? = null

    private val messageRequestHandler = WebMessageRequestHandler()
    private val messageResponseHandler = WebMessageResponseHolder()

    fun init(webExtensionController: WebExtensionController) {
        MainScope().launch(Dispatchers.Main) {
            withContext(Dispatchers.IO) {
                WebExtensionSupport.awaitInitialization()
            }
            webExtensionController.ensureBuiltIn(
                resourcePath, webExtensionId,
            ).accept { extension ->
                ThreadUtils.runOnUiThread {
                    extension?.setMessageDelegate(messageDelegate, portName)
                }
            }
        }
    }

    /**
     * 注册处理器
     */
    fun registerHandler(
        handlerName: String,
        requestHandler: RequestHandler,
    ) {
        messageRequestHandler.registerHandler(handlerName, requestHandler)
    }

    /**
     * 调用 js 方法
     */
    fun callHandler(
        sessionId: String?,
        handlerName: String,
        data: JSONObject?,
        responseCallback: ((response: WebMessage) -> Unit)?
    ) {
        if (messagePort == null) {
            return
        }

        val requestMessage = WebMessage()
        requestMessage.sessionId = sessionId ?: ""
        requestMessage.messageId = getMessageId()
        requestMessage.tabId = 0
        requestMessage.handlerName = handlerName
        requestMessage.msgType = "request"
        requestMessage.data = data

        responseCallback?.let {
            messageResponseHandler.registerHandler(
                requestMessage.messageId!!, it
            )
        }
        messagePort!!.postMessage(requestMessage.toJson())
    }

    private fun getMessageId(): String {
        return UUID.randomUUID().toString()
    }

    private val portDelegate = object : PortDelegate {
        override fun onPortMessage(
            message: Any,
            port: WebExtension.Port,
        ) {
            // This method will be called every time a message is sent from the
            // extension through this port. For now, let's just log a
            // message.
            val data = message as JSONObject
            val msgData = data.convertToWebMessage()
            val msgType = msgData.msgType
            if (msgType == "request") {
                messageRequestHandler.processRequest(msgData) {
                    port.postMessage(it)
                }
            } else if (msgType == "response") {
                messageResponseHandler.processResponse(msgData)
            }
        }

        override fun onDisconnect(port: WebExtension.Port) {
            // After this method is called, this port is not usable anymore.
            if (messagePort === port) {
                messagePort = null
            }
        }
    }

    private val messageDelegate = object : MessageDelegate {
        override fun onConnect(port: WebExtension.Port) {
            // Let's store the Port object in a member variable so it can be
            // used later to exchange messages with the WebExtension.
            messagePort = port

            // Registering the delegate will allow us to receive messages sent
            // through this port.
            messagePort?.setDelegate(portDelegate)
        }
    }


}
