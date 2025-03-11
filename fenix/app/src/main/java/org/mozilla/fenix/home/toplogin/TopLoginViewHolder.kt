/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package org.mozilla.fenix.home.toplogin

import android.view.View
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.ComposeView
import androidx.lifecycle.LifecycleOwner
import org.mozilla.fenix.compose.ComposeViewHolder
import org.mozilla.fenix.home.sessioncontrol.TopLoginInteractor


class TopLoginViewHolder(
    composeView: ComposeView,
    viewLifecycleOwner: LifecycleOwner,
    private val interactor: TopLoginInteractor,
) : ComposeViewHolder(composeView, viewLifecycleOwner) {

    init {
        UserLoginStateHolder.register(viewLifecycleOwner)
    }

    @Composable
    override fun Content() {
        if (UserLoginStateHolder.isLoginState.value) {
            return
        }
        TopLogin(
            onLoginBtnClick = {
                interactor.onGotoLogin()
            },
        )
    }

    companion object {
        val LAYOUT_ID = View.generateViewId()
    }
}
