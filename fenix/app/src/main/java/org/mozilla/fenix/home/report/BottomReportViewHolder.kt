/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package org.mozilla.fenix.home.report

import android.util.Log
import android.view.View
import android.view.ViewGroup
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.ComposeView
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.RecyclerView
import mozilla.components.support.ktx.android.util.dpToPx
import org.mozilla.fenix.compose.ComposeViewHolder
import org.mozilla.fenix.home.sessioncontrol.ContactInteractor
import org.mozilla.fenix.home.sessioncontrol.TopLinkInteractor
import org.mozilla.fenix.home.sessioncontrol.TopSiteInteractor

/**
 * View holder for top sites.
 *
 * @param composeView [ComposeView] which will be populated with Jetpack Compose UI content.
 * @param viewLifecycleOwner [LifecycleOwner] to which this Composable will be tied to.
 * @param interactor [TopSiteInteractor] which will have delegated to all user top sites
 * interactions.
 */

class BottomReportViewHolder(
    composeView: ComposeView,
    viewLifecycleOwner: LifecycleOwner,
    private val interactor: ContactInteractor,
) : ComposeViewHolder(composeView, viewLifecycleOwner) {

    init {
        composeView.addOnLayoutChangeListener { _, _, _, _, _, _, _, _, _ ->
            val recyclerView = composeView.parent as RecyclerView
            val root = recyclerView.parent as ViewGroup
            recyclerView.post {
                val value = root.height - recyclerView.height -
                        (56 * 3).dpToPx(root.context.resources.displayMetrics)
                val isScrollable = value <= 0
                BottomReportStateHolder.updateState(isScrollable)
            }
        }
    }

    @Composable
    override fun Content() {
        if (!BottomReportStateHolder.isShowInList.value) {
            return
        }
        BottomReport(
            onReport = {
                interactor.onReport()
            },
        )
    }

    companion object {
        val LAYOUT_ID = View.generateViewId()
    }
}
