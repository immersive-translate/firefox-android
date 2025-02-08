/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package org.mozilla.fenix.immersive_transalte.utils

import android.content.Context
import android.widget.Toast
import androidx.annotation.StringRes

object ToastUtil {

    fun toast(context: Context, msg: String, isLong: Boolean, gravity: Int) {
        val toast = Toast.makeText(
            context, msg,
            if (isLong) Toast.LENGTH_LONG
            else Toast.LENGTH_SHORT,
        )
        toast.setGravity(gravity, 0, 0)
        toast.show()
    }

    fun toast(context: Context, @StringRes resId: Int, isLong: Boolean, gravity: Int) {
        val msg = context.getString(resId)
        toast(context, msg, isLong, gravity)
    }

}
