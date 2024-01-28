/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package org.mozilla.fenix.immersive_transalte

import android.content.Context
import android.util.DisplayMetrics
import android.util.Size
import android.view.WindowManager


/**
 * created by xupx
 * on 2024-01-28
 */
object DeviceUtil {

    @Suppress("DEPRECATION")
    fun getDeviceRealSize(context: Context): Size {
        val wm = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val dm = DisplayMetrics()
        wm.defaultDisplay.getRealMetrics(dm)
        return Size(dm.widthPixels, dm.heightPixels)
    }

}
