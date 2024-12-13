/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package org.mozilla.fenix.onboarding.imts

import android.content.Context
import android.text.Html
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.FrameLayout
import org.mozilla.fenix.R
import org.mozilla.fenix.databinding.OnboardingPageLanguageLayoutBinding
import java.util.Locale

class LanguagePageView : FrameLayout {
    private lateinit var binding: OnboardingPageLanguageLayoutBinding
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

    private lateinit var selectLocal: Locale

    @Suppress("DEPRECATION")
    private fun init(context: Context) {
        binding = OnboardingPageLanguageLayoutBinding.inflate(LayoutInflater.from(context))
        addView(
            binding.root,
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT,
        )

        val setLanText =
            "<font color='red'>*</font>" + context.getString(R.string.onboarding_lan_set_title)
        binding.tvSetLanTitle.text = Html.fromHtml(setLanText)

        selectLocal = Locale.getDefault()
        binding.tvSelectLang.text = selectLocal.displayName

        binding.llLang.setOnClickListener {
            callback?.onSelectLang()
        }
        binding.btnSetDefaultBrowser.setOnClickListener {
            callback?.onSetDefaultBrowser()
        }
        binding.btnSkip.setOnClickListener {
            callback?.onSkip()
        }
    }

    fun setCallback(callback: Callback) {
        this.callback = callback
    }

    interface Callback {
        fun onSelectLang()
        fun onSetDefaultBrowser()
        fun onSkip()
    }
}
