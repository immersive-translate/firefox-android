package org.mozilla.fenix.browser.tips

import android.app.Activity
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.View
import android.view.ViewGroup
import android.widget.PopupWindow
import org.mozilla.fenix.databinding.ImmBrowserMenuTipsLayoutBinding
import org.mozilla.fenix.R
import org.mozilla.fenix.immersive_transalte.utils.PixelUtil

class ImmTranslateTipsWindow(
    context: Activity,
    private val type: Type,
    private val onPopFinish: () -> Unit,
) : PopupWindow(context) {
    private val binding = ImmBrowserMenuTipsLayoutBinding.inflate(context.layoutInflater)

    init {
        isOutsideTouchable = false
        isFocusable = true
        setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        width = ViewGroup.LayoutParams.WRAP_CONTENT
        height = ViewGroup.LayoutParams.WRAP_CONTENT
        binding.btnKnown.setOnClickListener { dismiss() }
        binding.btnKnown.text = context.getString(R.string.browser_toolbar_pop_btn)
        if (type == Type.Translate) {
            binding.tvContent.text = context.getString(R.string.browser_toolbar_pop_translate)
        } else {
            binding.tvContent.text = context.getString(R.string.browser_toolbar_pop_menu)
        }
    }

    fun show(groupView: ViewGroup, parent: View) {
        groupView.removeAllViews()
        groupView.addView(binding.root)
        binding.root.post {
            groupView.removeAllViews()
            contentView = binding.root
            val yOff = PixelUtil.dp2px(contentView.context, -16)
            var xOff = parent.context.resources.displayMetrics.widthPixels - binding.container.width
            if (type == Type.Translate) {
                xOff -= PixelUtil.dp2px(contentView.context, 100)
            } else {
                xOff -= PixelUtil.dp2px(contentView.context, 52)
            }
            showAsDropDown(parent, xOff, yOff)
        }
    }

    override fun dismiss() {
        super.dismiss()
        onPopFinish()
    }

    sealed class Type {
        data object Menu : Type()
        data object Translate : Type()
    }
}
