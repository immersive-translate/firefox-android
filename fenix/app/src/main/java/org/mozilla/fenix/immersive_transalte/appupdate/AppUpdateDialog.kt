/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package org.mozilla.fenix.immersive_transalte.appupdate

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Dialog
import android.text.TextUtils
import org.mozilla.fenix.R
import org.mozilla.fenix.databinding.DialogAppUpdateLayoutBinding
import org.mozilla.fenix.immersive_transalte.bean.AppVersionBean.AppVersion
import org.mozilla.fenix.immersive_transalte.utils.AppLangUtil

@SuppressLint("SetTextI18n")
class AppUpdateDialog(
    context: Activity,
    private val appVersion: AppVersion,
    onCancel: () -> Unit = {},
    onUpdate: () -> Unit = {},
) : Dialog(context, R.style.remind_dialog_style) {
    private val binding: DialogAppUpdateLayoutBinding =
        DialogAppUpdateLayoutBinding.inflate(layoutInflater)

    init {
        setContentView(binding.root)
        setCanceledOnTouchOutside(false)
        setCancelable(false)
        binding.tvNewVer.text = "V${appVersion.versionName}"
        getUpdateContent()?.let {
            binding.tvVerContent.text = it
        }
        binding.btnCancel.setOnClickListener {
            dismiss()
            onCancel.invoke()
        }
        binding.btnUpdate.setOnClickListener {
            dismiss()
            onUpdate.invoke()
        }
        binding.btnClose.setOnClickListener {
            dismiss()
            onCancel.invoke()
        }
    }

    private fun getUpdateContent(): String? {
        var updateContent: String? = ""
        appVersion.updateContent?.let {
            if (AppLangUtil.isChineseSimplified()) {
                updateContent = it["zh-CN"]
            } else if (AppLangUtil.isChineseTraditional()) {
                updateContent = it["zh-TW"]
            } else if (AppLangUtil.isKorean()) {
                updateContent = it["ko"]
            }
            if (TextUtils.isEmpty(updateContent)) {
                updateContent = it["en"] ?: ""
            }
        }
        return updateContent
    }

}
