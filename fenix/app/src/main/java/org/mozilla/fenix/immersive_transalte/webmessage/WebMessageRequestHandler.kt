/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package org.mozilla.fenix.immersive_transalte.webmessage

import org.json.JSONObject

class WebMessageRequestHandler {
    private val handlerMap = HashMap<String, RequestHandler>()

    internal fun registerHandler(
        handlerName: String,
        requestHandler: RequestHandler,
    ) {
        handlerMap[handlerName] = requestHandler
    }

    internal fun processRequest(
        message: WebMessage,
        callback: (response: JSONObject) -> Unit,
    ) {
        val handler = handlerMap[message.handlerName]
        handler?.let {
            it.process(message) { response ->
                callback(message.createJsonResponse(response))
            }
            return
        }

        val response = JSONObject().apply {
            put("message", "Native handler no register.")
        }
        val jsonObject = message.createJsonResponse(response)
        callback(jsonObject)
    }

}

interface RequestHandler {
    fun process(message: WebMessage, callback: (response: JSONObject?) -> Unit)
}
