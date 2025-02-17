package org.mozilla.fenix.onboarding.imts


import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.fragment.app.FragmentActivity
import org.mozilla.fenix.databinding.OnboardingPageFourthLayoutBinding

import org.mozilla.fenix.R
import org.mozilla.fenix.immersive_transalte.user.BuyVipFragment

class FourthPageView : FrameLayout {
    private lateinit var binding: OnboardingPageFourthLayoutBinding
    private var callback: Callback? = null
    private var activity: FragmentActivity? = null

    constructor(context: Context, activity: FragmentActivity?) : super(context) {
        this.activity = activity
        init(context)
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs, 0) {
        init(context)
    }

    constructor(context: Context, attrs: AttributeSet?, def: Int) : super(context, attrs, def) {
        init(context)
    }

    private fun init(context: Context) {
        binding = OnboardingPageFourthLayoutBinding.inflate(LayoutInflater.from(context))
        addView(
            binding.root,
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT,
        )
        binding.btnSkip.setOnClickListener {
            callback?.onNextClick()
        }
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        activity?.let {
            val fragmentTransaction = it.supportFragmentManager.beginTransaction()
            fragmentTransaction.replace(
                R.id.fl_vip_container,
                BuyVipFragment.newInstance(false, object : BuyVipFragment.Callback{
                    override fun onGotoBuy() {
                        callback?.onGotoBuy()
                    }
                }),
            )
            fragmentTransaction.commitAllowingStateLoss()
        }
    }

    fun setCallback(callback: Callback) {
        this.callback = callback
    }

    interface Callback {
        fun onGotoBuy()
        fun onNextClick()
    }
}
