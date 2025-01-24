/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package org.mozilla.fenix.immersive_transalte.user

import android.annotation.SuppressLint
import android.app.Activity
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.net.http.SslError
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.webkit.SslErrorHandler
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.PopupWindow
import androidx.core.view.WindowCompat
import org.mozilla.fenix.R
import org.mozilla.fenix.databinding.BuyVipPopwindowLayoutBinding
import org.mozilla.fenix.immersive_transalte.DeviceUtil

@SuppressLint("SetJavaScriptEnabled")
class BuyVipPopWindow(
    context: Activity,
    url: String,
    onPaySuccess: () -> Unit,
    onPayFailed: () -> Unit,
) : PopupWindow(context) {

    private val activity = context
    private var binding: BuyVipPopwindowLayoutBinding

    init {
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
                    if (it.startsWith("http://stripe_pay")) {
                        val uri = Uri.parse(url)
                        if ("/success" == uri.path) {
                            onPaySuccess()
                        } else {
                            onPayFailed()
                        }
                        dismiss()
                        return true
                    }
                }
                return false
            }

            @SuppressLint("WebViewClientOnReceivedSslError")
            override fun onReceivedSslError(
                view: WebView?,
                handler: SslErrorHandler?,
                error: SslError?,
            ) {
                handler?.proceed()
            }
        }

        // 加载网页
        binding.webview.loadUrl(url)
    }

    fun show(parent: View) {
        showAtLocation(parent, Gravity.BOTTOM, 0, 0)
        setStatusBarTheme(true)
    }

    override fun dismiss() {
        binding.webview.destroy()
        super.dismiss()
    }

    private fun setStatusBarTheme(isLight: Boolean) {
        WindowCompat.getInsetsController(
            activity.window,
            activity.window.decorView,
        ).isAppearanceLightStatusBars = isLight
    }

}
