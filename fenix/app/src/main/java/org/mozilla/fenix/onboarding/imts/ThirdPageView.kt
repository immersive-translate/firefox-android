/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package org.mozilla.fenix.onboarding.imts

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.FrameLayout
import org.mozilla.fenix.databinding.OnboardingPageThirdLayoutBinding
import org.mozilla.fenix.immersive_transalte.ImmersiveTracker

class ThirdPageView : FrameLayout, OnPageListener {
    private lateinit var binding: OnboardingPageThirdLayoutBinding
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
        binding = OnboardingPageThirdLayoutBinding.inflate(LayoutInflater.from(context))
        addView(
            binding.root,
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT,
        )
        binding.btnFinish.setOnClickListener {
            callback?.onFinish()
            ImmersiveTracker.appTrack("Onboarding_Step4_Continue_Click")
        }
    }

    fun setCallback(callback: Callback) {
        this.callback = callback
    }

    interface Callback {
        fun onFinish()
    }

    override fun onPageShow() {
        ImmersiveTracker.appTrack("Onboarding_Step4_Show")
    }
}
