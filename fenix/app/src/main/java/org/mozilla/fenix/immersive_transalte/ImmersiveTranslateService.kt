/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package org.mozilla.fenix.immersive_transalte

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import mozilla.components.feature.addons.Addon
import mozilla.components.feature.addons.AddonManager
import mozilla.components.feature.addons.update.AddonUpdater


/**
 * created by xupx
 * on 2023-12-24
 */
class ImmersiveTranslateService(
    private val addonManager: AddonManager,
) {
    private val immersiveTranslateAddonGetter = ImmersiveTranslateAddonGetter(addonManager)
    private var isChecked: Boolean = false
    private var installedTsAddon: Addon? = null

    /**
     * 检查安装更新插件
     */
    fun checkAndInstallOrUpdate() {
        if (isChecked) {
            return
        }
        isChecked = true
        CoroutineScope(Dispatchers.IO).launch {
            val addon = immersiveTranslateAddonGetter.createImmersiveAddon()
            MainScope().launch {
                if (!addon.isInstalled()) {
                    install(addon)
                } else {
                    installedTsAddon = addon
                    update(addon)
                }
            }
        }
    }

    /**
     * 安装插件
     */
    private fun install(addon: Addon) {
        addonManager.installAddon(
            addon,
            onSuccess = {
                fetchInstalledTSAddon()
                ImmersiveTranslateFlow.emit(true)
            },
            onError = { _, _ ->
                ImmersiveTranslateFlow.emit(false)
            },
        )
    }

    /**
     * 更新插件
     */
    private fun update(addon: Addon) {
        addonManager.updateAddon(
            addon.id,
            onFinish = { status ->
                when (status) {
                    AddonUpdater.Status.SuccessfullyUpdated -> {
                        fetchInstalledTSAddon()
                    }

                    else -> {}
                }
            },
        )
    }

    /**
     * 获取已安装翻译插件
     */
    private fun fetchInstalledTSAddon() {
        CoroutineScope(Dispatchers.IO).launch {
            delay(500)
            installedTsAddon = immersiveTranslateAddonGetter.getInstalledImmersiveAddon()
        }
    }

    fun getInstalledTSAddon(): Addon? {
        return installedTsAddon
    }
}
