/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package org.mozilla.fenix.onboarding.imts

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.FrameLayout
import org.mozilla.fenix.databinding.OnboardingPageSecondLayoutBinding

class SecondPageView : FrameLayout {
    private lateinit var binding: OnboardingPageSecondLayoutBinding
    private var callback: Callback? = null

    constructor(context: Context) : super(context) {
        init(context)
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs, 0) {
        init(context)
    }

    constructor(context: Context, attrs: AttributeSet?, def: Int) : super(context, attrs, def) {
        init(context)
    }

    private fun init(context: Context) {
        binding = OnboardingPageSecondLayoutBinding.inflate(LayoutInflater.from(context))
        addView(
            binding.root,
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT,
        )
        binding.btnISee.setOnClickListener {
            callback?.onNextClick()
        }
    }

    fun setCallback(callback: Callback) {
        this.callback = callback
    }

    interface Callback {
        fun onNextClick()
    }
}
