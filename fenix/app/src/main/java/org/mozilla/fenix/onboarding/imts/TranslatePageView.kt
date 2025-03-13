/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package org.mozilla.fenix.onboarding.imts

import android.animation.Animator
import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.FrameLayout
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import org.mozilla.fenix.R
import org.mozilla.fenix.databinding.OnboardingTranslateItemLayoutBinding
import org.mozilla.fenix.databinding.OnboardingTranslatePageLayoutBinding
import org.mozilla.fenix.immersive_transalte.bean.OnBoardingTranslateBean
import org.mozilla.fenix.immersive_transalte.net.service.HomePageService

class TranslatePageView : FrameLayout {
    private lateinit var binding: OnboardingTranslatePageLayoutBinding
    private var callback: Callback? = null
    private lateinit var translateAdapter: TranslateAdapter
    private val translateBeans = ArrayList<OnBoardingTranslateBean>()
    private val scope = MainScope()

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
        binding = OnboardingTranslatePageLayoutBinding.inflate(LayoutInflater.from(context))
        addView(
            binding.root,
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT,
        )
        translateAdapter = TranslateAdapter(context)
        binding.listParagraph.adapter = translateAdapter

        binding.btnTranslate.setOnClickListener {
            binding.root.postDelayed({ handlerTranslate() }, 100)
        }

        binding.btnSkip.setOnClickListener {
            callback?.onNextClick()
        }
        binding.btnContinue.setOnClickListener {
            callback?.onNextClick()
        }

        binding.lottieTsSuccess.addAnimatorListener(
            object : Animator.AnimatorListener {
                override fun onAnimationStart(animation: Animator) {
                }

                override fun onAnimationEnd(animation: Animator) {
                    binding.lottieTsSuccess.visibility = GONE
                }

                override fun onAnimationCancel(animation: Animator) {
                }

                override fun onAnimationRepeat(animation: Animator) {
                }
            },
        )
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        scope.cancel()
    }

    fun loadTranslateData(language: String) {
        binding.root.postDelayed({ loadData(language) }, 100)
    }

    fun setCallback(callback: Callback) {
        this.callback = callback
    }

    private fun handlerTranslate() {
        if (translateBeans.isEmpty() || translateAdapter.isTranslated) {
            return
        }
        // 翻译逻辑
        translateAdapter.setTranslate(true)
        refreshTranslateState()
    }

    private fun refreshTranslateState() {
        val isTranslated = translateAdapter.isTranslated

        binding.tvTitle.text =
            if (isTranslated) context.getString(R.string.onboarding_ts_page_success_title)
            else context.getString(R.string.onboarding_ts_page_title)
        binding.tvSubTitle.text =
            if (isTranslated) context.getString(R.string.onboarding_ts_page_success_sub_title)
            else context.getString(R.string.onboarding_ts_page_sub_title)

        binding.btnTranslate.setImageResource(
            if (isTranslated) R.drawable.ic_imm_translated_home_24
            else R.drawable.ic_imm_trans_home_24,
        )
        binding.btnContinue.isEnabled = isTranslated
        binding.btnContinue.backgroundTintList = context.getColorStateList(
            R.color.onboarding_btn_color_selector,
        )

        // 更新动画状态
        if (isTranslated) {
            binding.lottieTsArr.visibility = GONE
            binding.lottieTsSuccess.visibility = VISIBLE
            binding.lottieTsSuccess.playAnimation()
            binding.lottieTsArr.cancelAnimation()
        } else {
            binding.lottieTsArr.visibility = VISIBLE
            binding.lottieTsSuccess.visibility = GONE
            binding.lottieTsArr.playAnimation()
        }
    }

    private fun loadData(language: String) {
        if (!isAttachedToWindow) {
            return
        }
        scope.launch(Dispatchers.Main) {
            val translates = HomePageService.fetchOnBoardingTranslations(language).data?.data
            translates?.let {
                translateBeans.clear()
                translateBeans.addAll(it)
                translateAdapter.setData(translateBeans)
                refreshTranslateState()
            }
        }
    }

    interface Callback {
        fun onNextClick()
    }

    private class TranslateAdapter(context: Context?) : BaseAdapter() {
        private val inflater: LayoutInflater = LayoutInflater.from(context)
        private var translates = ArrayList<OnBoardingTranslateBean>()
        var isTranslated = false

        fun setData(translates: ArrayList<OnBoardingTranslateBean>) {
            isTranslated = false
            this.translates = translates
            notifyDataSetChanged()
        }

        fun setTranslate(isTranslated: Boolean) {
            this.isTranslated = isTranslated
            notifyDataSetChanged()
        }

        override fun getCount(): Int {
            return translates.size
        }

        override fun getItem(position: Int): OnBoardingTranslateBean? {
            return if (position < translates.size) {
                translates[position]
            } else {
                null
            }
        }

        override fun getItemId(position: Int): Long {
            return position.toLong()
        }

        override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
            val containerView: View
            val holder: ViewHolder
            if (convertView == null) {
                holder = ViewHolder()
                holder.itemBinding = OnboardingTranslateItemLayoutBinding.inflate(
                    inflater, parent, false,
                )
                containerView = holder.itemBinding.root
                containerView.tag = holder
            } else {
                holder = convertView.tag as ViewHolder
                containerView = convertView
            }

            val translateBean = getItem(position)
            translateBean?.let {
                holder.itemBinding.tvSource.text = it.english
                holder.itemBinding.tvTranslated.text = if (isTranslated) it.localizedText else ""
            }

            return containerView
        }
    }

    private class ViewHolder {
        lateinit var itemBinding: OnboardingTranslateItemLayoutBinding
    }
}
