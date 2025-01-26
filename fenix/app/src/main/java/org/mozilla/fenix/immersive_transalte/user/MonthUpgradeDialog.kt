/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package org.mozilla.fenix.immersive_transalte.user

import android.app.Dialog
import android.content.Context
import android.text.Html
import android.view.LayoutInflater
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import org.mozilla.fenix.R
import org.mozilla.fenix.databinding.DialogUpcommingLayoutBinding
import org.mozilla.fenix.immersive_transalte.base.widget.ProcessDialog
import org.mozilla.fenix.immersive_transalte.bean.VipUpgradeBean
import org.mozilla.fenix.immersive_transalte.bean.VipProductBean
import org.mozilla.fenix.immersive_transalte.net.service.MemberService

/**
 * 月费升级年费
 */
@Suppress("DEPRECATION")
class MonthUpgradeDialog(
    context: Context,
    product: VipProductBean,
    upcomming: VipUpgradeBean,
    onUpgradeSuccess: () -> Unit,
) : Dialog(context, R.style.remind_dialog_style) {
    private val binding: DialogUpcommingLayoutBinding

    init {
        binding = DialogUpcommingLayoutBinding.inflate(LayoutInflater.from(context))
        setContentView(binding.root)
        setCanceledOnTouchOutside(false)
        binding.ivClose.setOnClickListener { dismiss() }
        binding.tvCancel.setOnClickListener { dismiss() }


        val desc = context.getString(
            R.string.vip_upgrade_desc,
            "<b><font color='#EA4C89'>${product.entities.year.percentDiscountValue}</font></b>",
        )
        binding.tvDesc.text = Html.fromHtml(desc)

        val amount = "${product.entities.year.currencySymbol}${upcomming.amount_remaining / 100}"
        val detail = context.getString(
            R.string.vip_upgrade_detail,
            "<b><font color='#EA4C89'>${amount}</font></b><br>",
        )
        binding.tvDetail.text = Html.fromHtml(detail)

        binding.tvEndTime.text = context.getString(
            R.string.vip_upgrade_end_time,
            upcomming.subs_to,
        )

        binding.llUpgrade.setOnClickListener {
            upgrade(product, onUpgradeSuccess)
        }

    }

    private fun upgrade(product: VipProductBean, onUpgradeSuccess: () -> Unit) {
        showProcessDialog()
        MainScope().launch(Dispatchers.Main) {

            val priceId = product.entities?.year?.priceId
            val response = priceId?.let {
                MemberService.vipUpgrade(priceId)
            }
            hideProcessDialog()

            response?.let {
                if (it.isOk()) {
                    dismiss()
                    onUpgradeSuccess()
                }
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
