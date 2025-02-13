package org.mozilla.fenix.home.toplogin

import android.view.View
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.platform.ComposeView
import androidx.lifecycle.LifecycleOwner
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import org.mozilla.fenix.FenixApplication
import org.mozilla.fenix.compose.ComposeViewHolder
import org.mozilla.fenix.home.sessioncontrol.TopLoginInteractor
import org.mozilla.fenix.immersive_transalte.user.UserManager

class TopLoginViewHolder(
    composeView: ComposeView,
    viewLifecycleOwner: LifecycleOwner,
    private val interactor: TopLoginInteractor,
) : ComposeViewHolder(composeView, viewLifecycleOwner) {
    private var isLoginState = mutableStateOf(true)

    @Composable
    override fun Content() {
        fetchLoginState()
        if (isLoginState.value) {
            return
        }
        TopLogin(
            onLoginBtnClick = {
                interactor.onGotoLogin()
            },
        )
    }

    private fun fetchLoginState() {
        MainScope().launch(Dispatchers.IO) {
            isLoginState.value = UserManager.isLogin(FenixApplication.application)
        }
    }

    companion object {
        val LAYOUT_ID = View.generateViewId()
    }
}
