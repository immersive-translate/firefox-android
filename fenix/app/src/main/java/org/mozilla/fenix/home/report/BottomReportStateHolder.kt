/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package org.mozilla.fenix.home.report

import androidx.compose.runtime.mutableStateOf

object BottomReportStateHolder {
    val isShowInList = mutableStateOf(false)
    private var listener: ((Boolean) -> Unit)? = null

    fun updateState(isShow: Boolean) {
        isShowInList.value = isShow
        listener?.invoke(isShow)
    }

    fun listener(listener: (Boolean) -> Unit) {
        this.listener = listener
    }
}
