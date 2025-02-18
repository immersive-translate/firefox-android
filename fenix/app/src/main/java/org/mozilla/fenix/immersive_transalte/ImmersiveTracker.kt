/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package org.mozilla.fenix.immersive_transalte

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
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.mozilla.fenix.Config


/**
 * created by xupx
 * on 2024-01-30
 */
object ImmersiveTracker {
    private const val appToken = "yrf6oviwfshs"
    private var isInit = false
    private var adjustAttribution: AdjustAttribution? = null

    fun initTrack(ctx: Application) {
        @OptIn(DelicateCoroutinesApi::class)
        GlobalScope.launch(Dispatchers.IO) {
            initAdjust(ctx)
            initFB(ctx)
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
        config.setOnAttributionChangedListener { p0 -> adjustAttribution = p0; }
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
    ) {
        if (!isInit) {
            return
        }
        /*var finalMoney = money
        if (money > 0) {
            finalMoney = ceil(money * 100) / 100
        }*/
        val event = AdjustEvent("purchase_android")
        event.setRevenue(money.toDouble(), currency)
        event.addPartnerParameter("pay_type", "$vipType")
        event.addPartnerParameter("user_id", "$userId")
        Adjust.trackEvent(event)
    }

    fun getAdjustAttribution(): AdjustAttribution? {
        return adjustAttribution
    }
}
