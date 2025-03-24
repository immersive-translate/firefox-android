/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package org.mozilla.fenix.home.report

import android.view.ViewGroup
import androidx.compose.runtime.mutableStateOf
import androidx.recyclerview.widget.RecyclerView
import mozilla.components.support.ktx.android.util.dpToPx

object BottomReportStateHolder {
    val isShowInList = mutableStateOf(false)
    private var listener: ((Boolean) -> Unit)? = null

    private fun updateState(isShow: Boolean) {
        isShowInList.value = isShow
        listener?.invoke(isShow)
    }

    fun refreshState(recyclerView: RecyclerView) {
        recyclerView.post {
            val root = recyclerView.parent as ViewGroup
            val value = root.height - recyclerView.height -
                    (56 * 3).dpToPx(root.context.resources.displayMetrics)
            val isScrollable = value <= 0
            updateState(isScrollable)
        }
    }

    fun listener(listener: (Boolean) -> Unit) {
        this.listener = listener
    }
}
