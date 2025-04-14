/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package org.mozilla.fenix.immersive_transalte

import android.content.Context
import android.os.Build
import com.adjust.sdk.AdjustAttribution
import com.adjust.sdk.AndroidIdUtil
import com.google.android.gms.ads.identifier.AdvertisingIdClient
import mozilla.components.support.ktx.android.org.json.toJSON
import org.mozilla.fenix.immersive_transalte.net.service.TrackerService
import org.mozilla.fenix.immersive_transalte.utils.AppUtil
import org.mozilla.fenix.immersive_transalte.utils.TimeUtil

class AdjustS2STracker(
    ctx: Context,
) {
    private val androidId: String = AndroidIdUtil.getAndroidId(ctx)

    private val baseParams = mutableMapOf<String, Any?>().apply {
        // put("s2s", 1)
        // put("app_token", token)
        put("android_id", androidId)
        put("attribution_deeplink", 1)

        /*val environment = if (Config.channel.isRelease)
            AdjustConfig.ENVIRONMENT_PRODUCTION else
            AdjustConfig.ENVIRONMENT_SANDBOX
        put("environment", environment)*/

        put("app_version", AppUtil.getVersionName(ctx))
        put("package_name", ctx.packageName)
        val locales = ctx.resources.configuration.getLocales()
        val locale = locales.get(0)
        put("country", locale.country)
        put("language", locale.language)

        put("os_name", "android")
        put("os_version", AppUtil.getOsVersion(ctx))
        put("cpu_type", AppUtil.getCpuType())
        put("device_type", AppUtil.getDeviceType(ctx))
        put("device_name", AppUtil.getDeviceName(ctx))
        put("hardware_name", Build.HARDWARE)
    }

    // session params
    private val sessionParams = baseParams.toMutableMap()

    // event params
    private val eventParams = baseParams.toMutableMap()

    private var setId: String? = null
    private var adId: String? = null
    private var gpsInfo: AdvertisingIdClient.Info? = null
    private var attribution: AdjustAttribution? = null

    fun setIds(setId: String?, adId: String?, gpsInfo: AdvertisingIdClient.Info?) {
        this.setId = setId
        this.adId = adId
        this.gpsInfo = gpsInfo

        setId?.let {
            sessionParams["google_app_set_id"] = it
            eventParams["google_app_set_id"] = it
        }
        adId?.let {
            eventParams["adid"] = it
        }
        gpsInfo?.let {
            sessionParams["tracking_enabled"] = if (it.isLimitAdTrackingEnabled) 0 else 1
            if (!it.isLimitAdTrackingEnabled) {
                sessionParams["gps_adid"] = it.id
                eventParams["gps_adid"] = it.id
            }
        }
    }

    fun setAttribution(attribution: AdjustAttribution?) {
        this.attribution = attribution
        /*attribution?.let {
            val campaign = it.toMap()
            val campaignJson = campaign.toJSON().toString()
            sessionParams["trackerCampaign"] = campaignJson
            eventParams["trackerCampaign"] = campaignJson
        }*/
    }

    suspend fun trackSession() {
        val params = sessionParams.toMutableMap()
        val time = TimeUtil.getCurrentIso8601Time()
        params["created_at"] = time
        params["sent_at"] = time
        params["ip_address"] = AppUtil.getLocalIpAddress()
        // 发送请求
        TrackerService.adjustS2sSession(params)
    }

    suspend fun trackEvent(eventToken: String, eventParams: Map<String, String>? = null) {
        val params = getTrackEventParams()
        params["event_token"] = eventToken
        params["eventToken"] = eventToken
        eventParams?.let {
            params["callback_params"] = it.toJSON().toString()
        }
        // 发送请求
        TrackerService.adjustS2sEvent(params)
    }

    suspend fun trackEventRevenue(
        eventToken: String,
        revenue: Double,
        currency: String,
        imtSessionId: Long,
        imtOrderId: Long,
        eventParams: Map<String, String>? = null,
    ) {
        val params = getTrackEventParams()
        params["event_token"] = eventToken
        params["eventToken"] = eventToken
        params["revenue"] = revenue
        params["currency"] = currency
        if (imtSessionId > 0) {
            params["imtSessionId"] = imtSessionId
        }
        if (imtOrderId > 0) {
            params["imtOrderId"] = imtOrderId
        }
        eventParams?.let {
            params["callback_params"] = it.toJSON().toString()
        }
        // 发送请求
        TrackerService.adjustS2sEvent(params)
    }

    private fun getTrackEventParams(): MutableMap<String, Any?> {
        val params = eventParams.toMutableMap()
        // params["created_at_unix"] = System.currentTimeMillis() / 1000
        // val time = TimeUtil.getCurrentIso8601Time()
        // params["created_at"] = time
        // params["ip_address"] = AppUtil.getLocalIpAddress()
        return params
    }

}
