/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package org.mozilla.fenix.onboarding.imts

import android.annotation.SuppressLint
import android.content.Context
import android.text.Editable
import android.text.Html
import android.text.TextUtils
import android.text.TextWatcher
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.FrameLayout
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject
import org.mozilla.fenix.R
import org.mozilla.fenix.databinding.OnboardingPageLanguageLayoutBinding
import org.mozilla.fenix.ext.components
import org.mozilla.fenix.immersive_transalte.LanguageJson
import java.util.Locale


@SuppressLint("ClickableViewAccessibility")
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

    private lateinit var langCode: String
    private lateinit var langName: String

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
        binding.etSelectLang.setOnFocusChangeListener(
            object : OnFocusChangeListener {
                override fun onFocusChange(v: View?, hasFocus: Boolean) {
                    if (hasFocus) {
                        // callback?.onSelectLang()
                        binding.ivLang.setImageResource(R.mipmap.img_search)
                        binding.etSelectLang.hint = binding.etSelectLang.text
                        binding.etSelectLang.setText("")
                        binding.languageSelectorView.show()
                    } else {
                        binding.ivLang.setImageResource(R.mipmap.img_arr_down)
                        binding.languageSelectorView.dismiss()
                        binding.etSelectLang.setText(binding.etSelectLang.hint)
                    }
                }
            },
        )

        binding.etSelectLang.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }

            override fun afterTextChanged(s: Editable?) {
                s?.let {
                    binding.languageSelectorView.onSearch(it.toString().lowercase())
                }
            }
        })

        binding.languageSelectorView.setOnItemClickListener(
            object : LanguageSelectView.OnItemClickListener {
                override fun onItemClick(o: JSONObject) {
                    dismissLangPopWin()
                    try {
                        binding.languageSelectorView.dismiss()
                        langCode = o.getString("code")
                        langName = o.getString("language")
                        binding.etSelectLang.hint = langName
                        binding.etSelectLang.setText(langName)
                        saveDefaultLanguage()
                    } finally {
                    }
                }
            },
        )

        binding.root.setOnTouchListener(
            object : OnTouchListener {
                override fun onTouch(v: View?, event: MotionEvent?): Boolean {
                    if (event?.action == MotionEvent.ACTION_DOWN) {
                        dismissLangPopWin()
                    }
                    return false
                }
            },
        )

        binding.btnSetDefaultBrowser.setOnClickListener {
            dismissLangPopWin()
            callback?.onSetDefaultBrowser()
        }
        binding.btnSkip.setOnClickListener {
            dismissLangPopWin()
            callback?.onSkip()
        }
    }

    private fun dismissLangPopWin() {
        binding.etSelectLang.clearFocus()
        // 清理键盘
        val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager?
        imm?.hideSoftInputFromWindow(binding.etSelectLang.windowToken, 0)
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
                    binding.etSelectLang.hint = langName
                    binding.etSelectLang.setText(langName)
                    saveDefaultLanguage()
                    return@launch
                } finally {
                }
            }
            val selectLocal = Locale.getDefault()
            binding.etSelectLang.hint = selectLocal.displayName
            binding.etSelectLang.setText(selectLocal.displayName)
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
