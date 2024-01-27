/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package org.mozilla.fenix.immersive_transalte

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.drawable.ColorDrawable
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.widget.PopupWindow
import org.mozilla.fenix.R
import org.mozilla.fenix.databinding.DialogWebviewLayoutBinding

@SuppressLint("SetJavaScriptEnabled")
class WebDialog(
    context: Context,
    url: String,
    onKnown: () -> Unit,
) : PopupWindow(context) {
    private var binding: DialogWebviewLayoutBinding

    init {
        animationStyle = R.style.popup_window_anim
        binding = DialogWebviewLayoutBinding.inflate(LayoutInflater.from(context))
        contentView = binding.root
        width = context.resources.displayMetrics.widthPixels
        height = context.resources.displayMetrics.heightPixels
        isFocusable = true
        isOutsideTouchable = false
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
    }

    override fun dismiss() {
        binding.webview.destroy()
        super.dismiss()
    }
}
