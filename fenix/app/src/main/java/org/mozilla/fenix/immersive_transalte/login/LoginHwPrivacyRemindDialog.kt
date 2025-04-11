/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package org.mozilla.fenix.immersive_transalte.login

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
import androidx.core.view.WindowCompat
import org.mozilla.fenix.R
import org.mozilla.fenix.databinding.DialogHwPrivacyRemindLayoutBinding
import org.mozilla.fenix.immersive_transalte.DeviceUtil

class LoginHwPrivacyRemindDialog(
    activity: Activity,
    onAgree: () -> Unit,
    onShowCondition: () -> Unit,
    onShowPrivacy: () -> Unit,
) : PopupWindow(activity) {
    private var binding: DialogHwPrivacyRemindLayoutBinding =
        DialogHwPrivacyRemindLayoutBinding.inflate(
            LayoutInflater.from(activity),
        )

    init {
        setBackgroundDrawable(ColorDrawable(0x6F000000))
        animationStyle = R.style.popup_window_anim
        contentView = binding.root

        val displaySize = DeviceUtil.getDeviceRealSize(activity)
        width = displaySize.width
        height = displaySize.height

        isFocusable = true
        isOutsideTouchable = true
        isClippingEnabled = false

        val hwConditionText = activity.getString(R.string.login_hw_condition)
        val hwAgreementDesc = activity.getString(R.string.login_hw_privacy)
        var contentText = activity.getString(R.string.login_remind_dialog_desc)
        contentText = String.format(contentText, hwConditionText, hwAgreementDesc)

        val contentSpan = SpannableString(contentText)
        val textColor = ForegroundColorSpan(0xFF4181F0.toInt())

        var startSpan = contentSpan.indexOf(hwConditionText)
        contentSpan.setSpan(
            textColor,
            startSpan,
            startSpan + hwConditionText.length,
            Spanned.SPAN_INCLUSIVE_EXCLUSIVE,
        )
        contentSpan.setSpan(
            object : ClickableSpan() {
                override fun onClick(widget: View) {
                    onShowCondition.invoke()
                }

                override fun updateDrawState(ds: TextPaint) {
                    ds.isUnderlineText = false
                    ds.color = 0xFF4181F0.toInt()
                }
            },
            startSpan,
            startSpan + hwConditionText.length,
            Spanned.SPAN_INCLUSIVE_EXCLUSIVE,
        )

        startSpan = contentSpan.indexOf(hwAgreementDesc)
        contentSpan.setSpan(
            textColor,
            startSpan,
            startSpan + hwAgreementDesc.length,
            Spanned.SPAN_INCLUSIVE_EXCLUSIVE,
        )
        contentSpan.setSpan(
            object : ClickableSpan() {
                override fun onClick(widget: View) {
                    onShowPrivacy.invoke()
                }

                override fun updateDrawState(ds: TextPaint) {
                    ds.isUnderlineText = false
                    ds.color = 0xFF4181F0.toInt()
                }
            },
            startSpan,
            startSpan + hwAgreementDesc.length,
            Spanned.SPAN_INCLUSIVE_EXCLUSIVE,
        )

        binding.tvDesc.text = contentSpan
        binding.tvDesc.movementMethod = LinkMovementMethod.getInstance()
        binding.tvDesc.highlightColor = Color.TRANSPARENT

        binding.btnAgree.setOnClickListener {
            dismiss()
            onAgree()
        }
        binding.ivClose.setOnClickListener {
            dismiss()
        }
    }

    fun show(parent: View) {
        showAtLocation(parent, Gravity.BOTTOM, 0, 0)
    }
}
