/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package org.mozilla.fenix.immersive_transalte.webmessage

class WebMessageResponseHolder {
    private val handlerMap = HashMap<String, ((response: WebMessage) -> Unit)>()

    internal fun registerHandler(
        messageId: String,
        responseCallback: ((response: WebMessage) -> Unit),
    ) {
        handlerMap[messageId] = responseCallback
    }

    private fun removeHandler(messageId: String): ((response: WebMessage) -> Unit)? {
        return handlerMap.remove(messageId)
    }

    internal fun processResponse(msgData: WebMessage) {
        msgData.messageId?.let { id ->
            removeHandler(id)?.invoke(msgData)
        }
    }
}

