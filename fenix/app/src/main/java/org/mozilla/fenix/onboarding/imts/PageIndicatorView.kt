/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package org.mozilla.fenix.onboarding.imts

import android.content.Context
import android.graphics.Outline
import android.util.AttributeSet
import android.view.Gravity
import android.view.View
import android.view.ViewOutlineProvider
import android.widget.LinearLayout
import org.mozilla.fenix.R

class PageIndicatorView : LinearLayout {
    private var pageCount = 0
    private var primaryColor = 0
    private var selectedColor = 0
    private var indicatorWidth = 0
    private var indicatorSpace = 0;

    private val childViews = ArrayList<View>()

    private var indicatorIndex = 0

    constructor(context: Context) : super(context) {
        init(context, null)
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs, 0) {
        init(context, attrs)
    }

    constructor(context: Context, attrs: AttributeSet?, def: Int) : super(context, attrs, def) {
        init(context, attrs)
    }

    private fun init(context: Context, attrs: AttributeSet?) {
        orientation = HORIZONTAL
        gravity = Gravity.CENTER_VERTICAL or Gravity.START

        val ta = context.theme.obtainStyledAttributes(attrs, R.styleable.PageIndicatorView, 0, 0)
        pageCount = ta.getInt(R.styleable.PageIndicatorView_pageCount, 0)
        primaryColor = ta.getResourceId(R.styleable.PageIndicatorView_primaryColor, 0)
        selectedColor = ta.getResourceId(R.styleable.PageIndicatorView_selectedColor, 0)
        indicatorWidth = ta.getDimensionPixelSize(R.styleable.PageIndicatorView_indicatorWidth, 0)
        indicatorSpace = ta.getDimensionPixelSize(R.styleable.PageIndicatorView_indicatorSpace, 0)
        ta.recycle()

        removeAllViews()
        for (i in 0 until pageCount) {
            val childView = View(context)
            val layoutParams = LayoutParams(indicatorWidth, LayoutParams.MATCH_PARENT)
            layoutParams.marginEnd = indicatorSpace
            addView(childView, layoutParams)
            childViews.add(childView)

            childView.setBackgroundResource(if (i <= indicatorIndex) selectedColor else primaryColor)
        }
        invalidate()
    }

    fun setIndicatorIndex(index: Int) {
        if (indicatorIndex == index) {
            return
        }
        if (index < 0 || index >= childViews.size) {
            return
        }

        indicatorIndex = index
        for (i in 0 until childViews.size) {
            childViews[i].setBackgroundResource(
                if (i <= indicatorIndex) selectedColor else primaryColor
            )
        }
    }
}
