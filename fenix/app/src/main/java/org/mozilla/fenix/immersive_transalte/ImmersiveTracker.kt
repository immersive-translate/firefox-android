/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package org.mozilla.fenix.immersive_transalte

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Application
import android.app.Application.ActivityLifecycleCallbacks
import android.os.Bundle
import com.adjust.sdk.Adjust
import com.adjust.sdk.AdjustAttribution
import com.adjust.sdk.AdjustConfig
import com.adjust.sdk.AdjustEvent
import com.adjust.sdk.LogLevel
import com.facebook.FacebookSdk
import com.facebook.appevents.AppEventsConstants
import com.facebook.appevents.AppEventsLogger
import com.google.android.gms.ads.identifier.AdvertisingIdClient
import com.google.android.gms.appset.AppSet
import com.google.android.gms.appset.AppSetIdClient
import com.google.android.gms.tasks.Tasks
import com.immersivetranslate.browser.wxapi.WxApi
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import org.mozilla.fenix.Config
import org.mozilla.fenix.immersive_transalte.net.service.TrackerService


/**
 * created by xupx
 * on 2024-01-30
 */
object ImmersiveTracker {
    private const val appToken = "yrf6oviwfshs"
    private var isInit = false

    private var adjustAttribution: AdjustAttribution? = null
    private var adJustDeviceId = ""
    private var adJustAdid = ""
    private var gpsInfo: AdvertisingIdClient.Info? = null

    private lateinit var adjustS2STracker: AdjustS2STracker

    private val trackScope = MainScope()

    @SuppressLint("HardwareIds")
    fun initTrack(ctx: Application) {
        @OptIn(DelicateCoroutinesApi::class)
        trackScope.launch(Dispatchers.IO) {
            adjustS2STracker = AdjustS2STracker(ctx)
            initAdjust(ctx)
            initFB(ctx)
            WxApi.init(ctx)
        }
    }

    private fun initFB(ctx: Application) {
        FacebookSdk.setAutoInitEnabled(true)
        FacebookSdk.setAdvertiserIDCollectionEnabled(true)
        FacebookSdk.setAutoLogAppEventsEnabled(true)

        /*FacebookSdk.setIsDebugEnabled(true)
        FacebookSdk.addLoggingBehavior(LoggingBehavior.APP_EVENTS)*/

        val logger = AppEventsLogger.newLogger(ctx)
        logger.logEvent(AppEventsConstants.EVENT_NAME_ACTIVATED_APP)
        logger.flush()
    }

    private fun initAdjust(ctx: Application) {
        val isRelease = Config.channel.isRelease
        val environment =
            if (isRelease) AdjustConfig.ENVIRONMENT_PRODUCTION else AdjustConfig.ENVIRONMENT_SANDBOX
        val logLevel = if (isRelease) LogLevel.WARN else LogLevel.VERBOSE
        val config = AdjustConfig(ctx, appToken, environment)
        config.setLogLevel(logLevel)
        config.setOnAttributionChangedListener { p0 ->
            adjustAttribution = p0
            adJustAdid = Adjust.getAdid() ?: ""
            adjustS2STracker.setIds(adJustDeviceId, adJustAdid, gpsInfo)
            adjustS2STracker.setAttribution(adjustAttribution)
        }

        Adjust.onCreate(config)
        ctx.registerActivityLifecycleCallbacks(
            object : ActivityLifecycleCallbacks {
                override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
                }

                override fun onActivityStarted(activity: Activity) {
                }

                override fun onActivityResumed(activity: Activity) {
                    Adjust.onResume()
                }

                override fun onActivityPaused(activity: Activity) {
                    Adjust.onPause()
                }

                override fun onActivityStopped(activity: Activity) {
                }

                override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {
                }

                override fun onActivityDestroyed(activity: Activity) {
                }
            },
        )
        isInit = true

        // 获取 set_id 和 adid
        val client: AppSetIdClient = AppSet.getClient(ctx)
        val taskResult = Tasks.await(client.appSetIdInfo)
        adJustDeviceId = taskResult.id
        adJustAdid = Adjust.getAdid() ?: ""

        // gps_adid
        try {
            gpsInfo = AdvertisingIdClient.getAdvertisingIdInfo(ctx)
        } catch (_: Exception) {
        }

        adjustS2STracker.setIds(adJustDeviceId, adJustAdid, gpsInfo)
        adjustS2STracker.setAttribution(Adjust.getAttribution())
    }

    fun track(trackMessage: String) {
        if (isInit && trackMessage.isNotEmpty()) {
            val adjustEvent = AdjustEvent(trackMessage)
            Adjust.trackEvent(adjustEvent)
        }
    }

    /**
     * 购买会员 track
     */
    fun trackPurchase(
        money: Float,
        currency: String,
        vipType: Int,
        userId: Long,
        imtSessionId: Long,
        imtOrderId: Long
    ) {
        if (!isInit) {
            return
        }
        /*var finalMoney = money
        if (money > 0) {
            finalMoney = ceil(money * 100) / 100
        }*/

        val token = when (vipType) {
            1 -> "fj537o"       // purchase_trial_android               年费试用
            2 -> "c85hj9"       // purchase_monthly_android             月费会员
            3 -> "ovd2oe"       // purchase_trial_to_annual_android     试用升级到年费会员
            4 -> "5ql8x0"       // purchase_monthly_to_annual_android   月费升级到年费会员
            5 -> "7zkkgy"       // purchase_annual_android              年费会员
            else -> "2y25ob"    // purchase_android                     老的购买事件
        }

        val event = AdjustEvent(token)
        event.setRevenue(money.toDouble(), currency)
        event.addPartnerParameter("pay_type", "$vipType")
        event.addPartnerParameter("user_id", "$userId")
        Adjust.trackEvent(event)

        // 总的付费埋点
        val trackEvent = AdjustEvent("2y25ob")
        trackEvent.setRevenue(money.toDouble(), currency)
        trackEvent.addPartnerParameter("pay_type", "$vipType")
        trackEvent.addPartnerParameter("user_id", "$userId")
        Adjust.trackEvent(trackEvent)

        // s2s 上报
        adjustS2sTrackRevenue(
            "rugbsl", money.toDouble(), currency,
            imtSessionId, imtOrderId,
        )
    }

    fun getAdjustAttribution(): AdjustAttribution? {
        return adjustAttribution ?: Adjust.getAttribution()
    }

    fun getAdjustDeviceId(): String {
        return adJustDeviceId
    }

    fun getAdjustAdId(): String {
        return adJustAdid
    }

    fun appTrack(
        eventName: String,
        eventParams: Map<String, Any?>? = null,
    ) {
        trackScope.launch(Dispatchers.IO) {
            TrackerService.appTrack(eventName, eventParams)
        }
    }

    fun adjustS2sTrackSession() {
        trackScope.launch(Dispatchers.IO) {
            adjustS2STracker.trackSession()
        }
    }

    fun adjustS2sTrackEvent(eventToken: String, eventParams: Map<String, String>? = null) {
        trackScope.launch(Dispatchers.IO) {
            adjustS2STracker.trackEvent(eventToken, eventParams)
        }
    }

    private fun adjustS2sTrackRevenue(
        eventToken: String,
        revenue: Double,
        currency: String,
        imtSessionId: Long,
        imtOrderId: Long,
        eventParams: Map<String, String>? = null,
    ) {
        trackScope.launch(Dispatchers.IO) {
            adjustS2STracker.trackEventRevenue(
                eventToken, revenue, currency,
                imtSessionId, imtOrderId,
                eventParams,
            )
        }
    }
}
