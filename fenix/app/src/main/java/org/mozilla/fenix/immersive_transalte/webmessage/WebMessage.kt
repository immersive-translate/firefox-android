/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package org.mozilla.fenix.immersive_transalte.webmessage

import mozilla.components.support.ktx.android.org.json.tryGetInt
import mozilla.components.support.ktx.android.org.json.tryGetString
import org.json.JSONObject

class WebMessage {
    var sessionId: String? = null
    var messageId: String? = null
    var tabId: Int = 0
    var handlerName: String? = null
    var msgType: String? = null
    var data: JSONObject? = null
}

fun JSONObject.convertToWebMessage(): WebMessage {
    val webMessage = WebMessage()
    webMessage.sessionId = this.tryGetString("sessionId")
    webMessage.messageId = this.tryGetString("messageId")
    webMessage.tabId = this.tryGetInt("tabId") ?: 0
    webMessage.handlerName = this.tryGetString("handlerName")
    webMessage.msgType = tryGetString("msgType")
    webMessage.data = optJSONObject("data")
    return webMessage
}

fun WebMessage.createJsonResponse(data: JSONObject?): JSONObject {
    val jsonObject = JSONObject()
    jsonObject.put("sessionId", sessionId)
    jsonObject.put("messageId", messageId)
    jsonObject.put("tabId", tabId)
    jsonObject.put("handlerName", handlerName)
    jsonObject.put("msgType", "response")
    jsonObject.put("data", data)
    return jsonObject
}

fun WebMessage.toJson(): JSONObject {
    val jsonObject = JSONObject()
    jsonObject.put("sessionId", sessionId)
    jsonObject.put("messageId", messageId)
    jsonObject.put("tabId", tabId)
    jsonObject.put("handlerName", handlerName)
    jsonObject.put("msgType", msgType)
    jsonObject.put("data", data)
    return jsonObject
}

