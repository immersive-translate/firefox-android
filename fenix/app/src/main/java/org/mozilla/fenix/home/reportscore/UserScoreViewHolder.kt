/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package org.mozilla.fenix.home.reportscore

import android.view.View
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.ComposeView
import androidx.lifecycle.LifecycleOwner
import org.mozilla.fenix.compose.ComposeViewHolder
import org.mozilla.fenix.ext.settings
import org.mozilla.fenix.home.sessioncontrol.UserScoreInteractor

class UserScoreViewHolder(
    composeView: ComposeView,
    viewLifecycleOwner: LifecycleOwner,
    private val interactor: UserScoreInteractor,
) : ComposeViewHolder(composeView, viewLifecycleOwner) {

    @Composable
    override fun Content() {
        if (!ReportScoreStateHolder.isShow.value) {
            return
        }
        val context = composeView.context
        val userFeedbackScope = context.settings().userFeedbackScope
        if (userFeedbackScope) {
            return
        }
        ReportScore(
            context = context,
            onScopeBtnClick = { isAppReport ->
                interactor.onUserScore(isAppReport)
            },
            onCloseBtnClick = {
            },
        )
    }

    companion object {
        val LAYOUT_ID = View.generateViewId()
    }
}
