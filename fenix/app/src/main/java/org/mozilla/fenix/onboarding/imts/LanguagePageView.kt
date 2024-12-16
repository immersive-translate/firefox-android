/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package org.mozilla.fenix.onboarding.imts

import android.content.Context
import android.text.Html
import android.text.TextUtils
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.FrameLayout
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject
import org.mozilla.fenix.R
import org.mozilla.fenix.databinding.LanguageListLayoutBinding
import org.mozilla.fenix.databinding.OnboardingPageLanguageLayoutBinding
import org.mozilla.fenix.ext.components
import org.mozilla.fenix.immersive_transalte.LanguageJson
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

    //private lateinit var selectLocal: Locale
    private lateinit var langCode: String
    private lateinit var langName: String
    private var languagePopWindow: LanguagePopWindow? = null

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

        /*val selectLocal = Locale.getDefault()
        binding.tvSelectLang.text = selectLocal.displayName*/
        initDefaultLang()

        binding.llLang.setOnClickListener {
            callback?.onSelectLang()
            if (languagePopWindow == null) {
                languagePopWindow = LanguagePopWindow(
                    context,
                    LanguageListLayoutBinding.inflate(LayoutInflater.from(context))
                )
                languagePopWindow?.setOnItemClickListener(object : LanguagePopWindow.OnItemClickListener{
                    override fun onItemClick(o: JSONObject) {
                        try {
                            langCode = o.getString("code")
                            langName = o.getString("language")
                            binding.tvSelectLang.text = langName
                            saveDefaultLanguage()
                        } finally {
                        }
                    }
                })
            }
            languagePopWindow?.show(binding.llLang)
        }

        binding.btnSetDefaultBrowser.setOnClickListener {
            callback?.onSetDefaultBrowser()
        }
        binding.btnSkip.setOnClickListener {
            callback?.onSkip()
        }
    }

    /**
     * 初始化默认语言
     */
    private fun initDefaultLang() {
        MainScope().launch(Dispatchers.Main) {
            val jsonObject = withContext(Dispatchers.IO) {
                LanguageJson.getDefaultLanguage()
            }
            if (jsonObject != null) {
                try {
                    langCode = jsonObject.getString("code")
                    langName = jsonObject.getString("language")
                    binding.tvSelectLang.text = langName
                    saveDefaultLanguage()
                    return@launch
                } finally {
                }
            }
            val selectLocal = Locale.getDefault()
            binding.tvSelectLang.text = selectLocal.displayName
            langCode = selectLocal.country
            langName = selectLocal.displayName
            saveDefaultLanguage()
        }
    }

    /**
     * 保存语言
     */
    private fun saveDefaultLanguage() {
        if (!TextUtils.isEmpty(langCode)) {
            context.components.settings.defaultTsLanguage = langCode
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
