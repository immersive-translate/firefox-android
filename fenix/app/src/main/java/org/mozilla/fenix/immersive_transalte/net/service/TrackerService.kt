/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package org.mozilla.fenix.immersive_transalte.net.service

import android.os.Build
import org.json.JSONObject
import org.mozilla.fenix.immersive_transalte.Constant
import org.mozilla.fenix.immersive_transalte.ImmersiveTracker
import org.mozilla.fenix.immersive_transalte.base.http.BaseService
import org.mozilla.fenix.immersive_transalte.base.http.HttpClient
import org.mozilla.fenix.immersive_transalte.base.http.Response
import org.mozilla.fenix.immersive_transalte.net.api.TrackerApi

object TrackerService : BaseService() {
    private val trackUrl = Constant.appTrackUrl
    private val adjustS2sUrl = Constant.apiBaseUrl
    private val trackerApi: TrackerApi? by lazy { HttpClient.retrofit?.create(TrackerApi::class.java) }

    suspend fun appTrack(
        eventName: String,
        eventParams: Map<String, Any?>? = null,
    ): Response<Any?> {
        val url = "${trackUrl}/collect"
        val params: MutableMap<String, Any?> = HashMap()
        params["nonce"] = "${System.currentTimeMillis()}"
        params["subject"] = "user_behaviour"
        params["logs"] = ArrayList<String>().apply {
            add(getEvent(eventName, eventParams).toString())
        }
        return executeHttpAndCallback(trackerApi?.appUserBehaviour(url, params))
    }

    private fun getEvent(
        eventName: String,
        eventParams: Map<String, Any?>? = null,
    ): JSONObject {
        val event = JSONObject()
        event.put("os_name", "android")
        event.put("os_version", "${Build.VERSION.SDK_INT}")
        event.put("os_version_name", Build.VERSION.RELEASE)
        event.put("page_type", "android")

        val platformType = "${Build.MANUFACTURER} ${Build.BRAND} ${Build.MODEL}"
        event.put("platform_type", platformType)

        event.put("version", appVersionName)
        event.put("event_name", eventName)
        event.put("device_id", ImmersiveTracker.getAdjustDeviceId())

        val extParams = JSONObject()
        eventParams?.let { params ->
            val keys = params.keys
            keys.forEach { key ->
                extParams.put(key, params[key])
            }
        }
        event.put("ex_char_arg1", extParams.toString())

        return event
    }

    suspend fun adjustS2sSession(params: MutableMap<String, Any?>): Response<Any?> {
        val url = "${adjustS2sUrl}/adjust/session"
        val headerMap = getHeadersMap()
        return executeHttpAndCallback(trackerApi?.adjustS2sSession(url, headerMap, params))
    }

    suspend fun adjustS2sEvent(params: MutableMap<String, Any?>): Response<Any?> {
        val url = "${adjustS2sUrl}/adjust/event"
        val headerMap = getHeadersMap()
        return executeHttpAndCallback(trackerApi?.adjustS2sEvent(url, headerMap, params))
    }
}
