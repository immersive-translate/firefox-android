/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package org.mozilla.fenix.immersive_transalte.user

import android.app.Activity
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.MarginLayoutParams
import android.widget.PopupWindow
import androidx.annotation.StringRes
import org.mozilla.fenix.databinding.RemindPopDialogLayoutBinding
import org.mozilla.fenix.immersive_transalte.utils.PixelUtil
import kotlin.math.max
import kotlin.math.min

class BuyVipTipsPopWindow(
    context: Activity,
    @StringRes resId: Int,
) : PopupWindow(context) {

    private val binding: RemindPopDialogLayoutBinding =
        RemindPopDialogLayoutBinding.inflate(LayoutInflater.from(context))

    init {
        isOutsideTouchable = true
        isFocusable = false
        setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        // contentView = binding.root
        width = ViewGroup.LayoutParams.WRAP_CONTENT
        height = ViewGroup.LayoutParams.WRAP_CONTENT
        val content = context.getString(resId)
        binding.tvRemind.text = content
    }

    fun show(groupView: ViewGroup, parent: View) {
        groupView.removeAllViews()
        groupView.addView(binding.root)
        binding.root.post {
            groupView.removeAllViews()
            contentView = binding.root
            showPopupWindow(parent)
        }
    }

    private fun showPopupWindow(parent: View) {
        val popupWidth = binding.llContent.width
        val popupHeight = binding.llContent.height

        val arrWidth = binding.ivArrDown.width
        val arrHeight = binding.ivArrDown.height

        val maxX = parent.context.resources.displayMetrics.widthPixels - popupWidth
        val location = IntArray(2)
        parent.getLocationOnScreen(location)
        val x = max(min(location[0] + parent.width / 2 - popupWidth / 2, maxX), 0)
        var y = location[1] - popupHeight

        var arrView = binding.ivArrDown
        if (y < 100) {
            arrView.visibility = View.GONE
            arrView = binding.ivArrUp
            arrView.visibility = View.VISIBLE
            y = location[1] + arrHeight + PixelUtil.dp2px(parent.context, 10)
        }

        val arrOffsetX = location[0] + parent.width / 2 - x -
                arrWidth / 2 - PixelUtil.dp2px(parent.context, 20)
        (arrView.layoutParams as MarginLayoutParams).marginStart = arrOffsetX

        showAtLocation(parent, Gravity.NO_GRAVITY, x, y)
    }
}
