package org.mozilla.fenix.immersive_transalte.base.widget

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Path
import android.graphics.RectF
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.util.TypedValue
import android.widget.FrameLayout
import org.mozilla.fenix.R

class RoundedFrameLayout @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : FrameLayout(context, attrs) {

    private var topLeftRadius = 0f
    private var topRightRadius = 0f
    private var bottomLeftRadius = 0f
    private var bottomRightRadius = 0f

    private var backgroundColor = Color.TRANSPARENT

    private val path = Path()
    private val rect = RectF()

    // 可以添加自定义背景样式
    private var backgroundDrawable: Drawable? = null

    init {
        // 获取自定义属性
        attrs?.let {
            val typedArray = context.obtainStyledAttributes(it, R.styleable.RoundedFrameLayout)
            // 获取四个角的圆角大小
            topLeftRadius =
                typedArray.getDimension(R.styleable.RoundedFrameLayout_topLeftRadius, 0f)
            topRightRadius =
                typedArray.getDimension(R.styleable.RoundedFrameLayout_topRightRadius, 0f)
            bottomLeftRadius =
                typedArray.getDimension(R.styleable.RoundedFrameLayout_bottomLeftRadius, 0f)
            bottomRightRadius =
                typedArray.getDimension(R.styleable.RoundedFrameLayout_bottomRightRadius, 0f)
            // 获取背景颜色
            backgroundColor = typedArray.getColor(
                R.styleable.RoundedFrameLayout_backgroundColor,
                Color.TRANSPARENT
            )
            typedArray.recycle()
        }

        // 设置默认背景透明
        background = ColorDrawable(backgroundColor)
    }

    private fun convertDpToPx(radiusDp: Float): Float {
        return TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP, radiusDp, resources.displayMetrics
        )
    }

    // 设置圆角半径
    fun setCornerRadius(
        topLeftRadiusDp: Float,
        topRightRadiusDp: Float,
        bottomLeftRadiusDp: Float,
        bottomRightRadiusDp: Float
    ) {
        this.topLeftRadius = convertDpToPx(topLeftRadiusDp)
        this.topRightRadius = convertDpToPx(topRightRadiusDp)
        this.bottomLeftRadius = convertDpToPx(bottomLeftRadiusDp)
        this.bottomRightRadius = convertDpToPx(bottomRightRadiusDp)
        invalidate()  // 更新视图
    }

    override fun dispatchDraw(canvas: Canvas) {
        // 设定裁剪区域的矩形大小
        rect.set(0f, 0f, width.toFloat(), height.toFloat())

        // 重置并添加圆角路径
        path.reset()
        path.addRoundRect(
            rect, floatArrayOf(
                topLeftRadius, topLeftRadius,
                topRightRadius, topRightRadius,
                bottomLeftRadius, bottomLeftRadius,
                bottomRightRadius, bottomRightRadius
            ), Path.Direction.CW
        )

        // 保存当前画布状态
        canvas.save()

        // 裁剪路径：只绘制圆角区域
        canvas.clipPath(path)

        // 绘制子 View
        super.dispatchDraw(canvas)

        // 恢复画布状态
        canvas.restore()
    }

    override fun setBackground(background: Drawable?) {
        // 允许外部设置背景
        backgroundDrawable = background
        super.setBackground(background)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        // 在这里可以绘制额外的背景（例如阴影）
        backgroundDrawable?.let {
            it.setBounds(0, 0, width, height)
            it.draw(canvas)
        }
    }
}
