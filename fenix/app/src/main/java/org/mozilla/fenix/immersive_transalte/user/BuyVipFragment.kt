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
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.mozilla.fenix.BrowserDirection
import org.mozilla.fenix.HomeActivity
import org.mozilla.fenix.R
import org.mozilla.fenix.browser.browsingmode.BrowsingMode
import org.mozilla.fenix.databinding.FragmentBuyVipLayoutBinding
import org.mozilla.fenix.ext.showToolbar
import org.mozilla.fenix.immersive_transalte.Constant
import org.mozilla.fenix.immersive_transalte.ImmersiveTracker
import org.mozilla.fenix.immersive_transalte.base.widget.ProcessDialog
import org.mozilla.fenix.immersive_transalte.bean.UserBean
import org.mozilla.fenix.immersive_transalte.bean.VipProductBean
import org.mozilla.fenix.immersive_transalte.bean.VipUpgradeBean
import org.mozilla.fenix.immersive_transalte.net.service.MemberService
import org.mozilla.fenix.immersive_transalte.utils.PixelUtil
import kotlin.math.ceil


@Suppress("DEPRECATION")
class BuyVipFragment : Fragment() {

    companion object {
        fun newInstance(isNeedTitle: Boolean, callback: Callback): BuyVipFragment {
            val fragment = BuyVipFragment()
            fragment.isNeedTitle = isNeedTitle
            fragment.callback = callback
            return fragment
        }
    }

    private lateinit var binding: FragmentBuyVipLayoutBinding
    private var scope = MainScope()

    private var payType = 0 // 0 年卡，月卡

    private var userInfo: UserBean? = null
    private var productInfo: VipProductBean? = null
    private var trailList: List<String>? = null
    private var isNeedTitle = true

    private val browsingModeManager get() = (activity as HomeActivity).browsingModeManager

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
            if (userInfo != null) {
                createOrderClick()
            } else {
                gotoUserLogin()
            }
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

        if (!isNeedTitle) {
            val paddingLR = PixelUtil.dp2px(context, 26)
            binding.flVipYear.setBackgroundResource(R.drawable.buy_vip_top_bg)
            binding.flVipMonth.setBackgroundResource(R.drawable.buy_vip_top_bg)
            binding.flVipYear.setPadding(paddingLR, 0, paddingLR, 0)
            binding.flVipMonth.setPadding(paddingLR, 0, paddingLR, 0)
            (binding.llBuyVip.layoutParams as MarginLayoutParams).bottomMargin =
                PixelUtil.dp2px(context, 10)
        }

        refreshPayType()
        // 加载数据
        loadData()
    }

    override fun onResume() {
        super.onResume()
        if (isNeedTitle) {
            showToolbar(getString(R.string.buy_vip_btn_text_upgrade))
        }
        if (browsingModeManager.mode == BrowsingMode.Private) {
            binding.root.setBackgroundColor(0xFFFFFFFF.toInt())
        } else {
            binding.root.setBackgroundColor(0x0)
        }
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
            binding.btnVipMonth.setTextColor(requireContext().resources.getColor(R.color.fx_mobile_text_color_primary))
            binding.btnVipMonth.setBackgroundResource(0)
            (binding.llProVipTitle.layoutParams as MarginLayoutParams).topMargin =
                PixelUtil.dp2px(context, 24)
        } else {
            binding.flVipYear.visibility = View.GONE
            binding.flVipMonth.visibility = View.VISIBLE
            binding.llVipYearRemind.visibility = View.GONE
            binding.btnVipYear.setTextColor(requireContext().resources.getColor(R.color.fx_mobile_text_color_primary))
            binding.btnVipYear.setBackgroundResource(0)
            binding.btnVipMonth.setTextColor(0xFFFFFFFF.toInt())
            binding.btnVipMonth.setBackgroundResource(R.drawable.buy_vip_btn_selected_bg)
            (binding.llProVipTitle.layoutParams as MarginLayoutParams).topMargin = 0
        }

        refreshBuyButton()
    }

    private fun refreshBuyButton() {
        binding.llBuyVip.setBackgroundResource(R.mipmap.img_buy_vip_bg)
        binding.btnBuy.setTextColor(0xFFFFC736.toInt())
        binding.ivBuyHot.visibility = View.VISIBLE

        userInfo?.let {
            // 年度会员
            if (it.isSubYearVip) {
                if (payType == 0) {
                    binding.btnBuy.text = resources.getString(R.string.buy_vip_btn_text_cur)
                    binding.llBuyVip.setBackgroundResource(R.drawable.buy_vip_btn_buy_disable_bg)
                    binding.ivBuyHot.visibility = View.GONE
                    binding.btnBuy.setTextColor(0xFF999999.toInt())
                } else {
                    binding.btnBuy.text = resources.getString(R.string.vip_downgrade)
                    binding.llBuyVip.setBackgroundResource(R.drawable.buy_vip_btn_buy_disable_bg)
                    binding.ivBuyHot.visibility = View.GONE
                    binding.btnBuy.setTextColor(0xFF999999.toInt())
                }
                return
            }

            // 年费试用
            if (it.isSubYearVipTry) {
                if (payType == 0) {
                    binding.btnBuy.text = resources.getString(R.string.vip_year_btn_try_upgrade)
                } else {
                    binding.btnBuy.text = resources.getString(R.string.vip_downgrade)
                    binding.llBuyVip.setBackgroundResource(R.drawable.buy_vip_btn_buy_disable_bg)
                    binding.ivBuyHot.visibility = View.GONE
                    binding.btnBuy.setTextColor(0xFF999999.toInt())
                }
                return
            }

            // 月度会员
            if (it.isSubMonthVip) {
                if (payType == 0) {
                    binding.btnBuy.text = resources.getString(R.string.buy_vip_btn_text_upgrade)
                    binding.btnBuy.visibility = View.VISIBLE
                } else {
                    binding.btnBuy.text = resources.getString(R.string.buy_vip_btn_text_cur)
                    binding.llBuyVip.setBackgroundResource(R.drawable.buy_vip_btn_buy_disable_bg)
                    binding.ivBuyHot.visibility = View.GONE
                    binding.btnBuy.setTextColor(0xFF999999.toInt())
                }
                return
            }

        }

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
            binding.ivBuyHot.visibility = View.VISIBLE
        } else {
            binding.btnBuy.text = resources.getString(R.string.buy_vip_btn_buy)
            binding.ivBuyHot.visibility = View.GONE
        }
    }

    private var tryTimes = 0

    /**
     * 加载商品数据
     */
    private fun loadData() {
        scope.launch(Dispatchers.Main) {
            val isLogin = UserManager.isLogin(requireContext())
            val userInfoDeferred = if (isLogin) async { MemberService.getUserInfo() } else null
            val productDeferred = async { MemberService.getProducts() }
            val trailDeferred = if (isLogin) async { MemberService.queryTrail() } else null

            val userInfoResult = userInfoDeferred?.await()
            val productResult = productDeferred.await()
            val trailResult = trailDeferred?.await()

            userInfo = userInfoResult?.data?.data
            productInfo = productResult.data
            trailList = trailResult?.data?.data

            if ((productInfo == null) && tryTimes == 0) {
                tryTimes++
                loadData()
                return@launch
            }

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

    private fun gotoUserLogin() {
        if (!isNeedTitle) {
            callback?.onGotoBuy()
        }
        (requireActivity() as HomeActivity).openToBrowserAndLoad(
            "${Constant.loginPage}?app_action=gotoUpgrade", true,
            BrowserDirection.FromGlobal,
        )
    }

    private fun createCheckOrder(): Boolean {
        if (userInfo != null && userInfo!!.isSubYearVip) {
            return true
        }
        if (payType == 1) {
            if (userInfo != null && (userInfo!!.isSubMonthVip || userInfo!!.isSubYearVipTry)) {
                return true
            }
        }
        return false
    }

    private fun createOrderClick() {
        if (createCheckOrder()) {
            return
        }

        // 试用升级到正式
        val isSubTrial = userInfo?.isSubYearVipTry ?: false
        if (isSubTrial) {
            trialVipUpgrade();
            return
        }

        if (payType == 0) { // 年度会员
            val priceId = productInfo?.entities?.year?.priceId
            val isSubMonthVip = userInfo?.isSubMonthVip ?: false

            if (isSubMonthVip) {
                // 升级到包年
                monthVipUpgrade()
            } else {
                // 试用逻辑
                priceId?.let {
                    createOrder(it, true)
                }
            }
            return
        }

        if (payType == 1) { // 月度会员
            val priceId = productInfo?.entities?.month?.priceId
            priceId?.let {
                createOrder(it, false)
            }
            return
        }
    }

    private var isUpdating = false

    private fun trialVipUpgrade() {
        if (isUpdating) {
            return
        }
        val priceId = productInfo?.entities?.year?.priceId
        if (priceId.isNullOrEmpty()) {
            return
        }
        showProcessDialog()
        isUpdating = true
        scope.launch(Dispatchers.Main) {
            isUpdating = false
            val upcomming = MemberService.orderUpcomming(priceId).data?.data
            val currency = upcomming?.currency
            val product = currency?.let {
                MemberService.getUpgradeProducts(currency).data
            }
            hideProcessDialog()
            delay(100)
            product?.let {
                TrialUpgradeDialog(
                    requireContext(), it, upcomming,
                    {
                        (requireActivity() as HomeActivity).openToBrowserAndLoad(
                            searchTermOrURL = Constant.paySuccess,
                            newTab = true,
                            from = BrowserDirection.FromGlobal,
                        )
                        trackTrialUpgradeToYearVip(upcomming)
                    },
                ).show()
            }
        }
    }

    private fun monthVipUpgrade() {
        if (isUpdating) {
            return
        }
        val priceId = productInfo?.entities?.year?.priceId
        if (priceId.isNullOrEmpty()) {
            return
        }
        showProcessDialog()
        isUpdating = true;
        scope.launch(Dispatchers.Main) {
            isUpdating = false
            val upcomming = MemberService.orderUpcomming(priceId).data?.data
            val currency = upcomming?.currency
            val product = currency?.let {
                MemberService.getUpgradeProducts(currency).data
            }
            hideProcessDialog()
            delay(100)
            product?.let {
                MonthUpgradeDialog(
                    requireContext(), it, upcomming,
                    {
                        (requireActivity() as HomeActivity).openToBrowserAndLoad(
                            Constant.paySuccess, true, BrowserDirection.FromGlobal,
                        )
                        trackMonthUpgradeToYearVip(upcomming)
                    },
                ).show()
            }
        }
    }

    private var isHandle = false
    private fun createOrder(priceId: String, isEnableTrial: Boolean) {
        if (isHandle) {
            return
        }
        isHandle = true
        showProcessDialog()
        scope.launch(Dispatchers.Main) {
            isHandle = false
            val orderBean = MemberService.createOrder(
                priceId, isEnableTrial,
                Constant.profile,
                Constant.profile,
            )
            hideProcessDialog()
            delay(100)
            orderBean.data?.data?.redirect?.let {
                BuyVipPopWindow(
                    requireActivity(), it, userInfo!!,
                    onPaySuccess = {
                        (requireActivity() as HomeActivity).openToBrowserAndLoad(
                            Constant.paySuccess, true, BrowserDirection.FromGlobal,
                        )
                        trackTrialOrMonthVip(isEnableTrial)
                    },
                    onPayFailed = {
                    },
                    onNeedReload = {
                        loadData()
                    },
                ).show(binding.root)
            }
        }
    }

    /**
     * 月费升级到年费
     */
    private fun trackMonthUpgradeToYearVip(upgradeVip: VipUpgradeBean) {
        val money = (upgradeVip.amount_remaining / 100)
        val currency = upgradeVip.currency
        val vipType = 4
        trackPurchase(money, currency, vipType)
    }

    /**
     * 试用升级到年费
     */
    private fun trackTrialUpgradeToYearVip(upgradeVip: VipUpgradeBean) {
        val money = upgradeVip.amount_remaining / 100.0F
        val currency = upgradeVip.currency
        val vipType = 3
        trackPurchase(money, currency, vipType)
    }

    /**
     * 试用或者月费会员
     */
    private fun trackTrialOrMonthVip(isEnableTrial: Boolean) {
        var money = 0F
        var currency = ""
        val vipType:Int
        if (isEnableTrial) {
            vipType = 1
        } else {
            vipType = 2
            val year = productInfo?.entities?.year
            year?.let { vip ->
                money = (ceil(vip.unitAmount / 100 / 12 * 10) / 10F)
                currency = vip.currencySymbol
            }
        }
        trackPurchase(money, currency, vipType)
    }

    private fun trackPurchase(
        money: Float,
        currency: String,
        vipType: Int,
    ) {
        var uid = 0L
        userInfo?.let { uid = it.uid }
        ImmersiveTracker.trackPurchase(money, currency, vipType, uid)
    }

    override fun onDestroy() {
        scope.cancel()
        super.onDestroy()
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

    private var callback: Callback? = null

    interface Callback {
        fun onGotoBuy()
    }
}
