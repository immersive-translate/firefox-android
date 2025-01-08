/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package org.mozilla.fenix.home.toplinks

import android.view.View
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.ComposeView
import androidx.lifecycle.LifecycleOwner
import mozilla.components.lib.state.ext.observeAsComposableState
import org.mozilla.fenix.HomeActivity
import org.mozilla.fenix.components.components
import org.mozilla.fenix.compose.ComposeViewHolder
import org.mozilla.fenix.home.sessioncontrol.TopLinkInteractor
import org.mozilla.fenix.home.sessioncontrol.TopSiteInteractor
import org.mozilla.fenix.home.topsites.TopSiteColors
import org.mozilla.fenix.home.topsites.TopSites
import org.mozilla.fenix.perf.StartupTimeline
import org.mozilla.fenix.wallpapers.WallpaperState

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

    @Composable
    override fun Content() {
        val topLinks =
            components.appStore.observeAsComposableState { state -> state.topLinks }.value

        topLinks?.let {
            TopLinks(
                topLinks = it,
                onTopLinkClick = { topLink ->
                    interactor.onSelectTopLink(topLink, it.indexOf(topLink))
                },
            )
        }
    }

    companion object {
        val LAYOUT_ID = View.generateViewId()
    }
}
