/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package org.mozilla.fenix.immersive_transalte.appupdate

import android.app.Activity
import android.app.Dialog
import android.content.ActivityNotFoundException
import android.content.Intent
import android.os.Handler
import android.os.Looper
import androidx.core.net.toUri
import org.mozilla.fenix.R
import org.mozilla.fenix.databinding.DialogGoogleAppUpdateLayoutBinding
import org.mozilla.fenix.immersive_transalte.utils.SPUtil


class GoogleAppUpdateDialog(
    context: Activity,
) : Dialog(context, R.style.remind_dialog_style) {
    private val binding: DialogGoogleAppUpdateLayoutBinding =
        DialogGoogleAppUpdateLayoutBinding.inflate(layoutInflater)

    init {
        setContentView(binding.root)
        setCanceledOnTouchOutside(false)
        setCancelable(false)
        binding.btnCancel.setOnClickListener {
            dismiss()
        }
        binding.btnUpdate.setOnClickListener {
            dismiss()
            gotoPlayStore()
        }
    }

    // 跳转 google play 新应用
    private fun gotoPlayStore() {
        val newAppPackageName = "com.immersivetranslate.transtify"
        val intent = Intent(Intent.ACTION_VIEW)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        intent.setData("market://details?id=${newAppPackageName}".toUri())
        // 可选，指定跳转到 Google Play 商店 App
        intent.setPackage("com.android.vending")
        try {
            context.startActivity(intent)
        } catch (e: ActivityNotFoundException) {
            // 如果没有安装 Google Play 商店 App，则使用浏览器跳转
            intent.setData("https://play.google.com/store/apps/details?id=${newAppPackageName}".toUri())
            context.startActivity(intent)
        }
    }

    companion object {
        fun checkShow(activity: Activity, isCheck: Boolean) {
            Handler(Looper.getMainLooper()).postDelayed(
                {
                    if (activity.isDestroyed) {
                        return@postDelayed
                    }
                    if (isCheck && SPUtil.isGoogleNewVersionShown(activity)) {
                        return@postDelayed
                    }
                    SPUtil.saveGoogleNewVersionShown(activity)
                    GoogleAppUpdateDialog(activity).show()
                },
                500,
            )
        }
    }
}
