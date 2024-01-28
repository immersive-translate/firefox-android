/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package org.mozilla.fenix.immersive_transalte

import android.annotation.SuppressLint
import android.app.Activity
import android.graphics.drawable.ColorDrawable
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.widget.PopupWindow
import androidx.core.view.WindowCompat
import org.mozilla.fenix.R
import org.mozilla.fenix.databinding.DialogWebviewLayoutBinding

@SuppressLint("SetJavaScriptEnabled")
class WebDialog(
    context: Activity,
    url: String,
    onKnown: () -> Unit,
) : PopupWindow(context) {

    private val activity = context
    private var binding: DialogWebviewLayoutBinding

    init {
        animationStyle = R.style.popup_window_anim
        binding = DialogWebviewLayoutBinding.inflate(LayoutInflater.from(context))
        contentView = binding.root

        val displaySize = DeviceUtil.getDeviceRealSize(context)
        width = displaySize.width
        height = displaySize.height

        isFocusable = false
        isOutsideTouchable = false
        isClippingEnabled = false
        setBackgroundDrawable(ColorDrawable(0xFFFFFFFF.toInt()))

        binding.webview.settings.javaScriptEnabled = true
        binding.webview.loadUrl(url)

        binding.btnKnown.setOnClickListener {
            dismiss()
            onKnown()
        }
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
            activity.window.decorView
        ).isAppearanceLightStatusBars = isLight
    }
}
