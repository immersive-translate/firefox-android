/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package org.mozilla.fenix.home.toplogin

import android.os.Handler
import android.os.Looper
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import org.mozilla.fenix.FenixApplication
import org.mozilla.fenix.immersive_transalte.user.UserManager

object UserLoginStateHolder {
    private val handler = Handler(Looper.getMainLooper())
    private val scope = MainScope()
    val isLoginState = mutableStateOf(true)

    init {
        updateLoginState()
    }

    private val observer = object : DefaultLifecycleObserver {
        override fun onResume(owner: LifecycleOwner) {
            handler.removeCallbacksAndMessages(null)
            handler.postDelayed(
                { UserManager.refreshUser { updateLoginState() } }, 80,
            )
        }
    }

    fun register(viewLifecycleOwner: LifecycleOwner) {
        viewLifecycleOwner.lifecycle.removeObserver(observer)
        viewLifecycleOwner.lifecycle.addObserver(observer)
    }

    private fun updateLoginState() {
        scope.launch(Dispatchers.IO) {
            isLoginState.value = UserManager.isLogin(FenixApplication.application)
        }
    }
}
