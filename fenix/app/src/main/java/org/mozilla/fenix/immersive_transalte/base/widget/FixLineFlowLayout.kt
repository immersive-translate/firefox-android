/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package org.mozilla.fenix.immersive_transalte.base.widget

import android.content.Context
import android.util.AttributeSet
import android.view.ViewGroup
import org.mozilla.fenix.R
import kotlin.math.max

open class FixLineFlowLayout @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
) : ViewGroup(context, attrs, defStyleAttr) {

    protected var maxItemsPerRow = 3
    private var horizontalSpacing = 20
    private var verticalSpacing = 20

    init {
        context.theme.obtainStyledAttributes(attrs, R.styleable.FixLineFlowLayout, 0, 0).apply {
            try {
                maxItemsPerRow = getInteger(R.styleable.FixLineFlowLayout_lineViewCount, 3)
                horizontalSpacing =
                    getDimensionPixelSize(R.styleable.FixLineFlowLayout_horizontalSpacing, 20)
                verticalSpacing =
                    getDimensionPixelSize(R.styleable.FixLineFlowLayout_verticalSpacing, 20)
            } finally {
                recycle()
            }
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val width = MeasureSpec.getSize(widthMeasureSpec)
        val childWidth =
            ((width - paddingLeft - paddingRight - (maxItemsPerRow - 1) * horizontalSpacing) / maxItemsPerRow)

        var rowHeight = 0
        var totalHeight = paddingTop
        var rowItemCount = 0

        for (i in 0 until childCount) {
            val child = getChildAt(i)
            if (child.visibility == GONE) continue

            // 每个子 View 宽度固定，高度 wrap_content
            val childWidthSpec = MeasureSpec.makeMeasureSpec(childWidth, MeasureSpec.EXACTLY)
            val childHeightSpec = getChildMeasureSpec(
                MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED),
                0,
                child.layoutParams.height,
            )
            child.measure(childWidthSpec, childHeightSpec)

            rowHeight = max(rowHeight, child.measuredHeight)
            rowItemCount++

            if (rowItemCount == maxItemsPerRow || i == childCount - 1) {
                // 记录一整行高度
                totalHeight += rowHeight + verticalSpacing
                rowHeight = 0
                rowItemCount = 0
            }
        }

        // 最终测量结果（自适应高度）
        setMeasuredDimension(width, resolveSize(totalHeight + paddingBottom, heightMeasureSpec))
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        val parentWidth = right - left
        val childWidth =
            ((parentWidth - paddingLeft - paddingRight - (maxItemsPerRow - 1) * horizontalSpacing) / maxItemsPerRow)

        var curLeft = paddingLeft
        var curTop = paddingTop
        var rowHeight = 0
        var rowItemCount = 0

        for (i in 0 until childCount) {
            val child = getChildAt(i)
            if (child.visibility == GONE) continue

            val childHeight = child.measuredHeight
            val childRight = curLeft + childWidth
            val childBottom = curTop + childHeight

            child.layout(curLeft, curTop, childRight, childBottom)

            rowHeight = max(rowHeight, childHeight)
            curLeft += childWidth + horizontalSpacing
            rowItemCount++

            if (rowItemCount == maxItemsPerRow) {
                curLeft = paddingLeft
                curTop += rowHeight + verticalSpacing
                rowItemCount = 0
                rowHeight = 0
            }
        }
    }
}
