/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package org.mozilla.fenix.immersive_transalte

import android.app.Activity
import android.app.Application
import android.app.Application.ActivityLifecycleCallbacks
import android.os.Bundle
import com.adjust.sdk.Adjust
import com.adjust.sdk.AdjustConfig
import com.adjust.sdk.AdjustEvent
import com.adjust.sdk.LogLevel
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

    fun initTrack(ctx: Application) {
        @OptIn(DelicateCoroutinesApi::class)
        GlobalScope.launch(Dispatchers.IO) {
            init(ctx)
        }
    }

    private fun init(ctx: Application) {
        val isRelease = Config.channel.isRelease
        val environment = if (isRelease) AdjustConfig.ENVIRONMENT_PRODUCTION else AdjustConfig.ENVIRONMENT_SANDBOX
        val logLevel = if (isRelease) LogLevel.WARN  else LogLevel.VERBOSE
        val config = AdjustConfig(ctx, appToken, environment)
        config.setLogLevel(logLevel)
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
            }
        )
        isInit = true
    }

    fun track(trackMessage: String) {
        if (isInit && trackMessage.isNotEmpty()) {
            val adjustEvent = AdjustEvent(trackMessage)
            Adjust.trackEvent(adjustEvent)
        }
    }

}
