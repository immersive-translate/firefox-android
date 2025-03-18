/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */


package org.mozilla.fenix.immersive_transalte.utils

import android.content.Context
import android.util.DisplayMetrics
import android.view.WindowManager
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
}
