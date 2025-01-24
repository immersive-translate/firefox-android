/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package org.mozilla.fenix.immersive_transalte.user

import android.annotation.SuppressLint
import android.graphics.Paint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.MarginLayoutParams
import androidx.annotation.StringRes
import androidx.fragment.app.Fragment
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import org.mozilla.fenix.BrowserDirection
import org.mozilla.fenix.HomeActivity
import org.mozilla.fenix.R
import org.mozilla.fenix.databinding.FragmentBuyVipLayoutBinding
import org.mozilla.fenix.immersive_transalte.Constant
import org.mozilla.fenix.immersive_transalte.base.widget.ProcessDialog
import org.mozilla.fenix.immersive_transalte.bean.VipProductBean
import org.mozilla.fenix.immersive_transalte.net.service.MemberService
import org.mozilla.fenix.immersive_transalte.utils.PixelUtil
import kotlin.math.ceil


class BuyVipFragment : Fragment() {

    private lateinit var binding: FragmentBuyVipLayoutBinding

    private var payType = 0 // 0 年卡，月卡
    private var productInfo: VipProductBean? = null
    private var trailList: List<String>? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentBuyVipLayoutBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.tvVipYearRemind1.setCompoundDrawablesWithIntrinsicBounds(
            R.mipmap.img_buy_vip_year_remind_dot, 0, 0, 0,
        )
        binding.tvVipYearRemind2.setCompoundDrawablesWithIntrinsicBounds(
            R.mipmap.img_buy_vip_year_remind_dot, 0, 0, 0,
        )
        binding.btnVipYear.setOnClickListener {
            changePayType(0)
        }
        binding.btnVipMonth.setOnClickListener {
            changePayType(1)
        }
        binding.llBuyVip.setOnClickListener {
            createOrder()
        }

        binding.ivPopService.setOnClickListener {
            showTips(it, R.string.buy_vip_tip_01)
        }
        binding.ivPopPdf.setOnClickListener {
            showTips(it, R.string.buy_vip_tip_02)
        }
        binding.ivPopManhua.setOnClickListener {
            showTips(it, R.string.buy_vip_tip_03)
        }
        binding.ivPopEmail.setOnClickListener {
            showTips(it, R.string.buy_vip_tip_04)
        }

        refreshPayType()
        // 加载数据
        loadData()
    }

    private fun showTips(view: View, @StringRes resId: Int) {
        BuyVipTipsPopWindow(
            requireActivity(), resId,
        ).show(binding.flTipsContainer, view)
    }

    private fun changePayType(payType: Int) {
        if (this.payType == payType) {
            return
        }
        this.payType = payType
        refreshPayType()
    }

    private fun refreshPayType() {
        if (payType == 0) {
            binding.flVipYear.visibility = View.VISIBLE
            binding.flVipMonth.visibility = View.GONE
            binding.llVipYearRemind.visibility = View.VISIBLE
            binding.btnVipYear.setTextColor(0xFFFFFFFF.toInt())
            binding.btnVipYear.setBackgroundResource(R.drawable.buy_vip_btn_selected_bg)
            binding.btnVipMonth.setTextColor(0xFF333333.toInt())
            binding.btnVipMonth.setBackgroundResource(0)
            (binding.llProVipTitle.layoutParams as MarginLayoutParams).topMargin =
                PixelUtil.dp2px(context, 24)
        } else {
            binding.flVipYear.visibility = View.GONE
            binding.flVipMonth.visibility = View.VISIBLE
            binding.llVipYearRemind.visibility = View.GONE
            binding.btnVipYear.setTextColor(0xFF333333.toInt())
            binding.btnVipYear.setBackgroundResource(0)
            binding.btnVipMonth.setTextColor(0xFFFFFFFF.toInt())
            binding.btnVipMonth.setBackgroundResource(R.drawable.buy_vip_btn_selected_bg)
            (binding.llProVipTitle.layoutParams as MarginLayoutParams).topMargin = 0
        }

        refreshBuyButton()
    }

    private fun refreshBuyButton() {
        if (payType == 0) {
            productInfo?.entities?.year?.let {
                binding.btnBuy.text =
                    if (it.isEnableTrial /*&& trailList!!.isEmpty()*/)
                        resources.getString(
                            R.string.buy_vip_year_try_trial,
                            "${it.trialPeriodDays}",
                        )
                    else resources.getString(R.string.buy_vip_btn_buy)
            }
            /*val isEnableTrial =
                productInfo?.entities?.year?.isEnableTrial ?: false && trailList!!.isEmpty()
            binding.ivBuyHot.visibility = if (isEnableTrial) View.VISIBLE else View.GONE*/
            binding.ivBuyHot.visibility = View.VISIBLE
        } else {
            binding.btnBuy.text = resources.getString(R.string.buy_vip_btn_buy)
            binding.ivBuyHot.visibility = View.GONE
        }
    }

    /**
     * 加载商品数据
     */
    private fun loadData() {
        MainScope().launch(Dispatchers.Main) {
            val productDeferred = async { MemberService.getProducts() }
            val trailDeferred = async { MemberService.queryTrail() }

            val productResult = productDeferred.await()
            val trailResult = trailDeferred.await()

            productInfo = productResult.data
            trailList = trailResult.data?.data
            if (trailList == null) {
                trailList = ArrayList()
            }

            updateUI()
        }
    }

    /**
     * 更新UI
     */
    @SuppressLint("SetTextI18n")
    private fun updateUI() {
        val year = productInfo?.entities?.year
        val month = productInfo?.entities?.month

        val unitMonth = resources.getString(R.string.buy_vip_unit_year)
        //val unitYear = resources.getString(R.string.buy_vip_unit_year)

        // 年费会员
        year?.let {
            // 每月平均价格（向上取整，保留一位小数）
            val oneMonthPrice = ceil(it.unitAmount / 100 / 12 * 10) / 10F
            binding.tvYearMonthPrice.text = "${it.currencySymbol}${oneMonthPrice}"
            binding.tvYearPrice.text = it.displayedPrice + unitMonth
            binding.tvYearOriginPrice.text = it.originalDisplayedPrice + unitMonth
            binding.tvYearOriginPrice.paint.flags = Paint.STRIKE_THRU_TEXT_FLAG

            val saveMoney = (it.originalUnitAmount - it.unitAmount) / 100
            binding.tvYearSaveMoney.text =
                resources.getString(R.string.buy_vip_year_save_money) + it.currencySymbol + saveMoney
            binding.tvVipYearRemind1.text =
                resources.getString(R.string.buy_vip_year_renewal, it.displayedPrice)
            binding.btnVipYear.text =
                resources.getString(R.string.buy_vip_year_save_percent, it.percentDiscountValue)
        }

        // 月费会员
        month?.let {
            binding.tvMonthPrice.text = it.displayedPrice
        }

        refreshBuyButton()

        if (year != null && month != null) {
            binding.llContent.visibility = View.VISIBLE
        } else {
            binding.llContent.visibility = View.GONE
        }
        binding.progress.visibility = View.GONE
    }

    private var isHandle = false
    private fun createOrder() {
        if (isHandle) {
            return
        }
        val priceId = if (payType == 0) productInfo?.entities?.year?.priceId else
            productInfo?.entities?.month?.priceId
        if (priceId.isNullOrEmpty()) {
            return
        }
        showProcessDialog()
        isHandle = true
        MainScope().launch(Dispatchers.Main) {
            isHandle = false
            binding.root.postDelayed(
                {
                    hideProcessDialog()
                },
                500,
            )

            val isEnableTrial =
                if (payType == 0)
                    productInfo?.entities?.year?.isEnableTrial ?: false && trailList!!.isEmpty()
                else false

            val orderBean = MemberService.createOrder(
                priceId, isEnableTrial,
                "http://stripe_pay/success",
                "http://stripe_pay/failed",
            )

            orderBean.data?.data?.redirect?.let {
                BuyVipPopWindow(
                    requireActivity(), it,
                    onPaySuccess = {
                        (requireActivity() as HomeActivity).openToBrowserAndLoad(
                           Constant.paySuccess, true, BrowserDirection.FromGlobal
                        )
                    },
                    onPayFailed = {
                    },
                ).show(binding.root)
            }
        }

    }

    private var processDialog: ProcessDialog? = null
    private fun showProcessDialog() {
        processDialog = ProcessDialog(context)
        processDialog!!.show()
    }

    private fun hideProcessDialog() {
        if (processDialog != null && processDialog!!.isShowing) {
            processDialog!!.dismiss()
        }
    }

}
