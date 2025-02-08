/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package org.mozilla.fenix.immersive_transalte.utils

import android.content.Context

object PixelUtil {

    // dip转像素
    fun dp2px(context: Context?, dip: Int): Int {
        if (context == null) {
            return 0
        }
        val SCALE = context.resources.displayMetrics.density
        return (dip * SCALE + 0.5f).toInt()
    }

    fun dp2px(context: Context?, dip: Float): Int {
        if (context == null) {
            return 0
        }
        val SCALE = context.resources.displayMetrics.density
        return (dip * SCALE + 0.5f).toInt()
    }

    fun dp2pxFloat(context: Context?, dip: Float): Float {
        if (context == null) {
            return 0F
        }
        val SCALE = context.resources.displayMetrics.density
        return dip * SCALE + 0.5f
    }

}
