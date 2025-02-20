package org.mozilla.fenix.browser.tips

import android.app.Activity
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.util.DisplayMetrics
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.PopupWindow
import org.mozilla.fenix.R
import org.mozilla.fenix.databinding.ImmBrowserMenuTipsLayoutBinding
import org.mozilla.fenix.immersive_transalte.utils.PixelUtil
import kotlin.math.pow
import kotlin.math.sqrt

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
            val isPad = isPad(parent.context)
            xOff -= if (type == Type.Translate) {
                if (isPad) {
                    PixelUtil.dp2px(contentView.context, 196)
                } else {
                    PixelUtil.dp2px(contentView.context, 100)
                }
            } else {
                if (isPad) {
                    PixelUtil.dp2px(contentView.context, 148)
                } else {
                    PixelUtil.dp2px(contentView.context, 52)
                }
            }
            showAsDropDown(parent, xOff, yOff)
        }
    }

    @Suppress("DEPRECATION")
    fun isPad(context: Context): Boolean {
        val wm = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val display = wm.defaultDisplay
        val dm = DisplayMetrics()
        display.getMetrics(dm)
        val x: Double = (dm.widthPixels / dm.xdpi).pow(2.0F).toDouble()
        val y: Double = (dm.heightPixels / dm.ydpi).pow(2.0F).toDouble()
        val screenInches = sqrt(x + y) // 屏幕尺寸
        return screenInches >= 7.0
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
