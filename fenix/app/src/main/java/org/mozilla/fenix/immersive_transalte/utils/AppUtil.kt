/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package org.mozilla.fenix.immersive_transalte.utils

import android.content.Context
import android.content.res.Configuration
import android.os.Build
import android.util.DisplayMetrics
import android.view.WindowManager
import java.net.NetworkInterface
import kotlin.math.pow
import kotlin.math.sqrt


object AppUtil {

    @Suppress("DEPRECATION")
    fun isPad(context: Context): Boolean {
        val wm = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val display = wm.defaultDisplay
        val dm = DisplayMetrics()
        display.getMetrics(dm)
        val x: Double = (dm.widthPixels / dm.xdpi).pow(2.0F).toDouble()
        val y: Double = (dm.heightPixels / dm.ydpi).pow(2.0F).toDouble()
        val screenInches = sqrt(x + y) // 屏幕尺寸
        return screenInches >= 7.0
    }

    fun getVersionName(context: Context): String? {
        try {
            val packageManager = context.packageManager
            val packageInfo = packageManager.getPackageInfo(context.packageName, 0)
            return packageInfo.versionName // 例如 "1.0.0"
        } catch (_: Exception) {
        }
        return ""
    }

    fun getCpuType(): String {
        return when {
            Build.SUPPORTED_ABIS.isNotEmpty() -> Build.SUPPORTED_ABIS[0]
            else -> ""
        }
    }

    fun getLocalIpAddress(): String? {
        try {
            val interfaces = NetworkInterface.getNetworkInterfaces()
            while (interfaces.hasMoreElements()) {
                val networkInterface = interfaces.nextElement()
                val addresses = networkInterface.inetAddresses
                while (addresses.hasMoreElements()) {
                    val address = addresses.nextElement()
                    if (!address.isLoopbackAddress) {
                        return address.hostAddress
                    }
                }
            }
        } catch (_: Exception) {
        }
        return ""
    }

    private fun isGooglePlayGamesForPC(context: Context): Boolean {
        return context.packageManager.hasSystemFeature(
            "com.google.android.play.feature.HPE_EXPERIENCE",
        )
    }

    fun getOsVersion(context: Context): String {
        return if (isGooglePlayGamesForPC(context)) ""
        else Build.VERSION.RELEASE
    }

    fun getDeviceName(context: Context): String {
        return if (isGooglePlayGamesForPC(context)) ""
        else Build.MODEL
    }

    fun getDeviceType(context: Context): String {
        return when {
            isGooglePlayGamesForPC(context) -> "pc"

            (context.resources.configuration.uiMode and
                    Configuration.UI_MODE_TYPE_MASK) == Configuration
                .UI_MODE_TYPE_TELEVISION -> "tv"

            else -> when (context.resources.configuration.screenLayout
                    and Configuration.SCREENLAYOUT_SIZE_MASK) {
                Configuration.SCREENLAYOUT_SIZE_SMALL,
                Configuration.SCREENLAYOUT_SIZE_NORMAL,
                    -> "phone"

                Configuration.SCREENLAYOUT_SIZE_LARGE,
                Configuration.SCREENLAYOUT_SIZE_XLARGE,
                    -> "tablet"

                else -> ""
            }
        }
    }

}
