/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */
package org.mozilla.fenix.immersive_transalte.user

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

class HwPrivacyRemindDialog(
    private val activity: Activity,
    onAgree: () -> Unit,
    onShowHwAgreement: () -> Unit,
) : PopupWindow(activity) {
    private var binding: DialogHwPrivacyRemindLayoutBinding =
        DialogHwPrivacyRemindLayoutBinding.inflate(
            LayoutInflater.from(activity),
        )

    init {
        setBackgroundDrawable(ColorDrawable(0x6F000000))
        // setStatusBarTheme(false)
        animationStyle = R.style.popup_window_anim
        contentView = binding.root

        val displaySize = DeviceUtil.getDeviceRealSize(activity)
        width = displaySize.width
        height = displaySize.height

        isFocusable = true
        isOutsideTouchable = true
        isClippingEnabled = false

        var contentText = activity.getString(R.string.buy_vip_hw_agreement_dialog_desc)
        val recurringAgreementDesc =
            activity.getString(R.string.buy_vip_hw_recurring_payment_agreement)
        contentText = String.format(contentText, recurringAgreementDesc)

        val contentSpan = SpannableString(contentText)
        val textColor = ForegroundColorSpan(0xFF4181F0.toInt())

        val startSpan = contentSpan.indexOf(recurringAgreementDesc)
        contentSpan.setSpan(
            textColor,
            startSpan,
            startSpan + recurringAgreementDesc.length,
            Spanned.SPAN_INCLUSIVE_EXCLUSIVE,
        )
        contentSpan.setSpan(
            object : ClickableSpan() {
                override fun onClick(widget: View) {
                    onShowHwAgreement.invoke()
                }

                override fun updateDrawState(ds: TextPaint) {
                    ds.isUnderlineText = false
                    // ds.color = ds.linkColor
                }
            },
            startSpan,
            startSpan + recurringAgreementDesc.length,
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

    @Suppress("SameParameterValue")
    private fun setStatusBarTheme(isLight: Boolean) {
        WindowCompat.getInsetsController(
            activity.window,
            activity.window.decorView,
        ).isAppearanceLightStatusBars = isLight
    }
}
