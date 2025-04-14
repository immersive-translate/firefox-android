/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package org.mozilla.fenix.immersive_transalte.utils

class FastClickUtil(private val timeMills: Long = 300) {
    private var lastClickTime = 0L

    fun isFastClick(): Boolean {
        val currentTime = System.currentTimeMillis()
        if (currentTime - lastClickTime < timeMills) {
            return true
        }
        lastClickTime = currentTime
        return false
    }
}
