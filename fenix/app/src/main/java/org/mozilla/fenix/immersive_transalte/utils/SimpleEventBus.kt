/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package org.mozilla.fenix.immersive_transalte.utils

import java.util.concurrent.CopyOnWriteArrayList

object SimpleEventBus {
    private val eventMap = HashMap<String, MutableList<(Any?) -> Unit>>()

    fun register(event: String, block: (Any?) -> Unit) {
        var events = eventMap[event]
        if (events == null) {
            events = CopyOnWriteArrayList()
            eventMap[event] = events
        }
        events.add(block)
    }

    fun unregister(event: String, block: (Any?) -> Unit) {
        val events = eventMap[event]
        events?.remove(block)
    }

    fun postEvent(event: String, obj: Any? = null) {
        val events = eventMap[event]
        events?.forEach {
            it.invoke(obj)
        }
    }

    const val EVENT_LOGIN = "login"
    const val EVENT_RESET_PWD = "reset_pwd"
    const val EVENT_WEB_LOGIN_GET_TOKEN = "web_login_get_token"
    const val EVENT_WEB_LOGIN_CANCEL = "EVENT_WEB_LOGIN_CANCEL"
    const val EVENT_WX_LOGIN_GET_TOKEN = "EVENT_WX_LOGIN_GET_TOKEN"
}
