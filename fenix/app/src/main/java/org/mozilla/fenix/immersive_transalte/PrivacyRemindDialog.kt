/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package org.mozilla.fenix.immersive_transalte

import android.app.Activity
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.text.SpannableString
import android.text.Spanned
import android.text.TextPaint
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.text.style.ForegroundColorSpan
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.widget.PopupWindow
import org.mozilla.fenix.R
import org.mozilla.fenix.databinding.DialogPrivacyRemindLayoutBinding
import org.mozilla.fenix.settings.SupportUtils


/**
 * created by xupx
 * on 2024-01-27
 */
class PrivacyRemindDialog(
    context: Activity,
    onAgree: () -> Unit,
    onDisagree: () -> Unit,
    onShowWeb: (url: String) -> Unit,
) :
    PopupWindow(context) {

    private val clickUSSpan = object : ClickableSpan() {
        override fun onClick(widget: View) {
            dismiss()
            onShowWeb(SupportUtils.APP_AGREEMENT_URL)
        }

        override fun updateDrawState(ds: TextPaint) {
            ds.color = ds.linkColor
            //ds.isUnderlineText = true
        }
    }

    private val clickITPSpan = object : ClickableSpan() {
        override fun onClick(widget: View) {
            dismiss()
            onShowWeb(SupportUtils.APP_PRIVACY_NOTICE)
        }

        override fun updateDrawState(ds: TextPaint) {
            ds.color = ds.linkColor
            //ds.isUnderlineText = true
        }
    }

    private var binding: DialogPrivacyRemindLayoutBinding

    init {
        animationStyle = R.style.popup_window_anim
        binding = DialogPrivacyRemindLayoutBinding.inflate(LayoutInflater.from(context))
        contentView = binding.root
        width = context.resources.displayMetrics.widthPixels
        height = context.resources.displayMetrics.heightPixels
        isFocusable = true
        isOutsideTouchable = false
        setBackgroundDrawable(ColorDrawable(0x6F000000))
        // isClippingEnabled = true

        val contentUS = context.getString(R.string.privacy_remind_dialog_us)
        val contentITP = context.getString(R.string.privacy_remind_dialog_itp)
        var contentText = context.getString(R.string.privacy_remind_dialog_content)
        contentText = String.format(contentText, contentUS, contentITP)

        val contentSpan = SpannableString(contentText)
        val textColor = ForegroundColorSpan(Color.parseColor("#FF1052FF"))

        // 隐私政策
        var startSpan = contentSpan.indexOf(contentITP)
        contentSpan.setSpan(
            textColor,
            startSpan,
            startSpan + contentITP.length,
            Spanned.SPAN_INCLUSIVE_EXCLUSIVE,
        )
        contentSpan.setSpan(
            clickITPSpan,
            startSpan,
            startSpan + contentITP.length,
            Spanned.SPAN_INCLUSIVE_EXCLUSIVE,
        )

        // 用户协议
        startSpan = contentSpan.indexOf(contentUS)
        contentSpan.setSpan(
            textColor,
            startSpan,
            startSpan + contentUS.length,
            Spanned.SPAN_INCLUSIVE_EXCLUSIVE,
        )
        contentSpan.setSpan(
            clickUSSpan,
            startSpan,
            startSpan + contentUS.length,
            Spanned.SPAN_INCLUSIVE_EXCLUSIVE,
        )

        binding.tvContent.movementMethod = LinkMovementMethod.getInstance()
        binding.tvContent.text = contentSpan

        binding.btnAgree.setOnClickListener {
            dismiss()
            onAgree()
        }
        binding.btnDisagree.setOnClickListener {
            dismiss()
            onDisagree()
        }
    }

    fun show(parent: View) {
        showAtLocation(parent, Gravity.BOTTOM, 0, 0)
    }
}
