/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package org.mozilla.fenix.immersive_transalte.login

import android.content.Context
import android.view.Gravity
import androidx.fragment.app.FragmentManager
import com.immersivetranslate.browser.wxapi.WxApi
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject
import org.mozilla.fenix.R
import org.mozilla.fenix.immersive_transalte.net.ApiErrorMappingHelper
import org.mozilla.fenix.immersive_transalte.net.service.MemberService
import org.mozilla.fenix.immersive_transalte.user.UserManager
import org.mozilla.fenix.immersive_transalte.utils.SimpleEventBus
import org.mozilla.fenix.immersive_transalte.utils.ToastUtil

class ThirdLoginManager(
    private val ctx: Context,
    private val scope: CoroutineScope,
    private val fragmentManager: FragmentManager,
    private val onStateCallback: (state: Int) -> Unit = {},
) {
    companion object {
        const val TYPE_GOOGLE = 1
        const val TYPE_FACEBOOK = 2
        const val TYPE_APPLE = 3
        const val TYPE_WX = 4

        const val STATE_LOADING = 1
        const val STATE_LOGIN_SUCCESS = 2
        const val STATE_LOGIN_FAIL = 3
    }

    private var isGettingUrl = false
    fun loginThird(loginType: Int) {
        if (isGettingUrl) {
            return
        }
        when (loginType) {
            TYPE_GOOGLE -> {
                isGettingUrl = true
                loginWithGoogle()
            }

            TYPE_FACEBOOK -> {
                isGettingUrl = true
                loginWithFacebook()
            }

            TYPE_APPLE -> {
                isGettingUrl = true
                loginWithApple()
            }

            TYPE_WX -> loginWithWx()
        }
    }

    @Suppress("UNCHECKED_CAST")
    fun loginWithAccessToken(jsonObject: JSONObject) {
        // val type = jsonObject.opt("type")
        val params = jsonObject.opt("params") as MutableMap<String, Any?>
        scope.launch(Dispatchers.Main) {
            onStateCallback.invoke(STATE_LOADING)
            val result = withContext(Dispatchers.IO) {
                MemberService.loginWithAccessToken(params)
            }
            if (!result.isOk()) {
                ApiErrorMappingHelper.AuthCallback.errorToast(result)
            }
            val loginResult = result.data?.data
            val isLogin = loginResult?.hasToken() ?: false
            if (isLogin) {
                loginResult!!.updateUserInfo()
                val user = loginResult.loginResult!!.user
                // 更新设备信息
                withContext(Dispatchers.IO) {
                    MemberService.updateDeviceInfo(user)
                    // 保存用户信息
                    UserManager.saveUser(ctx, user)
                }
            }
            if (isLogin) {
                onStateCallback.invoke(STATE_LOGIN_SUCCESS)
                SimpleEventBus.postEvent(SimpleEventBus.EVENT_LOGIN)
            } else {
                onStateCallback.invoke(STATE_LOGIN_FAIL)
            }
        }
    }

    /**
     * 微信登录
     */
    fun loginWithWxToken(params: MutableMap<String, Any?>) {
        scope.launch(Dispatchers.Main) {
            onStateCallback.invoke(STATE_LOADING)
            val loginResult = withContext(Dispatchers.IO) {
                MemberService.loginWithWxToken(params).data?.data
            }
            val isLogin = loginResult?.hasToken() ?: false
            if (isLogin) {
                loginResult!!.updateUserInfo()
                val user = loginResult.loginResult!!.user
                // 更新设备信息
                withContext(Dispatchers.IO) {
                    MemberService.updateDeviceInfo(user)
                    // 保存用户信息
                    UserManager.saveUser(ctx, user)
                }
            }
            if (isLogin) {
                onStateCallback.invoke(STATE_LOGIN_SUCCESS)
                SimpleEventBus.postEvent(SimpleEventBus.EVENT_LOGIN)
            } else {
                onStateCallback.invoke(STATE_LOGIN_FAIL)
            }
        }
    }

    /**
     * google
     */
    private fun loginWithGoogle() {
        scope.launch(Dispatchers.Main) {
            val result = withContext(Dispatchers.IO) {
                MemberService.webLogin(
                    loginType = "oauth",
                    externalOAuthAppName = "Google",
                )
            }
            isGettingUrl = false
            if (!result.isOk()) {
                ApiErrorMappingHelper.WebLogin.errorToast(result)
                return@launch
            }
            val redirectTo = result.data?.data?.redirectTo
            redirectTo?.let {
                WebLoginFragmentDialog(it).show(
                    fragmentManager, "WebLoginFragmentDialog",
                )
            }
        }
    }

    /**
     * apple
     */
    private fun loginWithApple() {
        scope.launch(Dispatchers.Main) {
            val result = withContext(Dispatchers.IO) {
                MemberService.webLogin(
                    loginType = "oauth",
                    externalOAuthAppName = "Apple",
                )
            }
            isGettingUrl = false
            if (!result.isOk()) {
                ApiErrorMappingHelper.WebLogin.errorToast(result)
                return@launch
            }
            val redirectTo = result.data?.data?.redirectTo
            redirectTo?.let {
                WebLoginFragmentDialog(it).show(
                    fragmentManager, "WebLoginFragmentDialog",
                )
            }
        }
    }

    /**
     * facebook
     */
    private fun loginWithFacebook() {
        scope.launch(Dispatchers.Main) {
            val result = withContext(Dispatchers.IO) {
                MemberService.webLogin(
                    loginType = "oauth",
                    externalOAuthAppName = "Facebook",
                )
            }
            isGettingUrl = false
            if (!result.isOk()) {
                ApiErrorMappingHelper.WebLogin.errorToast(result)
                return@launch
            }
            val redirectTo = result.data?.data?.redirectTo
            redirectTo?.let {
                WebLoginFragmentDialog(it).show(
                    fragmentManager, "WebLoginFragmentDialog",
                )
            }
        }
    }

    private fun loginWithWx() {
        if (!WxApi.isInstall()) {
            val toastMsg = ctx.getString(R.string.wx_login_no_install)
            ToastUtil.toast(ctx, toastMsg, false, Gravity.BOTTOM)
            return
        }
        WxApi.requestLogin()
    }
}
