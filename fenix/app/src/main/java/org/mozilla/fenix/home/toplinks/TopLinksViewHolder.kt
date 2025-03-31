/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package org.mozilla.fenix.home.toplinks

import android.view.View
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.ComposeView
import androidx.lifecycle.LifecycleOwner
import mozilla.components.lib.state.ext.observeAsComposableState
import org.mozilla.fenix.components.components
import org.mozilla.fenix.compose.ComposeViewHolder
import org.mozilla.fenix.home.sessioncontrol.TopLinkInteractor
import org.mozilla.fenix.home.sessioncontrol.TopSiteInteractor
import org.mozilla.fenix.immersive_transalte.ImmersiveTracker

/**
 * View holder for top sites.
 *
 * @param composeView [ComposeView] which will be populated with Jetpack Compose UI content.
 * @param viewLifecycleOwner [LifecycleOwner] to which this Composable will be tied to.
 * @param interactor [TopSiteInteractor] which will have delegated to all user top sites
 * interactions.
 */
class TopLinksViewHolder(
    composeView: ComposeView,
    viewLifecycleOwner: LifecycleOwner,
    private val interactor: TopLinkInteractor,
) : ComposeViewHolder(composeView, viewLifecycleOwner) {
    private val tracks = HashMap<Long, String>().apply {
        put(1L, "Homepage_Web_Click")
        put(2L, "Homepage_Video_Click")
        put(3L, "Homepage_Doc_Click")
        put(4L, "Homepage_Manga_Click")
        put(5L, "Homepage_Image_Click")
        put(6L, "Homepage_Rednote_Click")
        put(7L, "Homepage_Bilin_Click")
    }

    @Composable
    override fun Content() {
        val topLinks =
            components.appStore.observeAsComposableState { state -> state.topLinks }.value

        topLinks?.let {
            TopLinks(
                topLinks = it,
                onTopLinkClick = { topLink ->
                    interactor.onSelectTopLink(topLink, it.indexOf(topLink))
                    track(topLink.id)
                },
            )
        }
    }

    private fun track(id: Long?) {
        val eventName = id?.let {
            tracks[it]
        }
        eventName?.let {
            ImmersiveTracker.appTrack(it)
        }
    }

    companion object {
        val LAYOUT_ID = View.generateViewId()
    }
}
