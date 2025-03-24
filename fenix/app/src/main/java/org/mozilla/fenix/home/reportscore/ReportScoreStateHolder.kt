/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package org.mozilla.fenix.home.reportscore

import androidx.compose.runtime.mutableStateOf
import org.mozilla.fenix.FenixApplication
import org.mozilla.fenix.ext.settings

object ReportScoreStateHolder {
    private const val translateCount = 3
    val isShow = mutableStateOf(false)

    init {
        refreshState()
    }

    fun refreshState() {
        isShow.value = FenixApplication.application.settings().translateCount >= translateCount
    }

    fun track() {
        var count = FenixApplication.application.settings().translateCount
        if (count >= translateCount) {
            return
        }
        count += 1
        FenixApplication.application.settings().translateCount = count
        isShow.value = count >= translateCount
    }
}
