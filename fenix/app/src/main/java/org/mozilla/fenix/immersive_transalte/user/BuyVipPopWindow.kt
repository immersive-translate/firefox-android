/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package org.mozilla.fenix.immersive_transalte.user

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.text.TextUtils
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.PopupWindow
import androidx.core.view.WindowCompat
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.mozilla.fenix.R
import org.mozilla.fenix.databinding.BuyVipPopwindowLayoutBinding
import org.mozilla.fenix.immersive_transalte.Constant
import org.mozilla.fenix.immersive_transalte.DeviceUtil
import org.mozilla.fenix.immersive_transalte.bean.UserBean
import org.mozilla.fenix.immersive_transalte.net.service.MemberService


@SuppressLint("SetJavaScriptEnabled")
class BuyVipPopWindow(
    context: Activity,
    url: String,
    userInfo: UserBean,
    onPaySuccess: () -> Unit,
    onPayFailed: () -> Unit,
    onNeedReload: () -> Unit,
) : PopupWindow(context) {

    private val scope = MainScope()
    private val activity = context
    private var binding: BuyVipPopwindowLayoutBinding
    private var userVipLevel = 0

    private val onPageReload = onNeedReload
    private var isNeedReload = false

    init {
        userVipLevel = getUserVipLevel(userInfo)

        animationStyle = R.style.popup_window_anim
        binding = BuyVipPopwindowLayoutBinding.inflate(
            LayoutInflater.from(context.applicationContext),
        )
        contentView = binding.root

        val displaySize = DeviceUtil.getDeviceRealSize(context)
        width = displaySize.width
        height = displaySize.height

        isFocusable = true
        isOutsideTouchable = false
        isClippingEnabled = false
        setBackgroundDrawable(ColorDrawable(0x6F000000))

        binding.ivClose.setOnClickListener {
            dismiss()
            onPayFailed()
        }

        binding.webview.settings.javaScriptEnabled = true
        binding.webview.webViewClient = object : WebViewClient() {
            @Deprecated("Deprecated in Java")
            override fun shouldOverrideUrlLoading(view: WebView?, url: String?): Boolean {
                url?.let {
                    if (it.startsWith(Constant.profile)) {
                        try {
                            val uri = Uri.parse(url)
                            val result = uri.getQueryParameter("success")
                            if (TextUtils.equals(result, "true")) {
                                onPaySuccess()
                            } else {
                                onPayFailed()
                            }
                        } finally {
                        }
                        dismiss()
                        return true
                    } else if (it.startsWith("alipays://")) {
                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                        context.startActivity(intent)
                        refreshPayState(onPaySuccess)
                        isNeedReload = true
                        return true
                    }
                }
                return false
            }
        }

        // 加载网页
        binding.webview.loadUrl(url)
    }

    private fun getUserVipLevel(user: UserBean): Int {
        if (user.isSubYearVipTry) {
            return 1
        } else if (user.isSubMonthVip) {
            return 2
        } else if (user.isSubYearVip) {
            return 3
        }
        return 0
    }

    fun show(parent: View) {
        showAtLocation(parent, Gravity.BOTTOM, 0, 0)
        setStatusBarTheme(true)
    }

    override fun dismiss() {
        scope.cancel()
        binding.webview.destroy()
        super.dismiss()
        if (isNeedReload) {
            onPageReload()
        }
    }

    private fun setStatusBarTheme(isLight: Boolean) {
        WindowCompat.getInsetsController(
            activity.window,
            activity.window.decorView,
        ).isAppearanceLightStatusBars = isLight
    }

    /**
     * 查询支付状态
     */
    private fun refreshPayState(onPaySuccess: () -> Unit) {
        scope.launch(Dispatchers.Main) {
            delay(1000)
            val user = MemberService.getUserInfo().data?.data
            user?.let {
                val level = getUserVipLevel(it)
                if (level > userVipLevel) {
                    dismiss()
                    onPaySuccess()
                    return@launch
                }
            }
            refreshPayState(onPaySuccess)
        }
    }

}
