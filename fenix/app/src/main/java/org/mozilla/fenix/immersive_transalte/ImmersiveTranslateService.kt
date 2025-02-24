/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package org.mozilla.fenix.immersive_transalte

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import mozilla.components.concept.engine.EngineSession
import mozilla.components.concept.engine.webextension.MessageHandler
import mozilla.components.concept.engine.webextension.Port
import mozilla.components.feature.addons.Addon
import mozilla.components.feature.addons.AddonManager
import mozilla.components.feature.addons.update.AddonUpdater
import org.json.JSONObject
import kotlin.math.max


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
    private val localVersion = ImmersiveTranslateAddonGetter.version
    private val localResource = ImmersiveTranslateAddonGetter.downloadUrl

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
            addon.id,
            addon.downloadUrl, null,
            onSuccess = {
                // callRegisterMessageHandler(addon.id)
                fetchInstalledTSAddon()
                ImmersiveTranslateFlow.emit(true)
            },
            onError = { _ ->
                ImmersiveTranslateFlow.emit(false)
            },
        )
    }

    /**
     * 更新插件
     */
    private fun update(addon: Addon) {
        // callRegisterMessageHandler(addon.id)
        /*if (localVersion == addon.version) {
            addonManager.installAddon(
                addon.id,
                addon.downloadUrl, null,
                onSuccess = {
                    fetchInstalledTSAddon()
                },
            )
        } else if (addon.downloadUrl.isNotEmpty() &&
            addon.downloadUrl.indexOf(addon.version) < 0) {
            addonManager.updateAddon(
                addon.id,
                onFinish = { status ->
                    if (status == AddonUpdater.Status.SuccessfullyUpdated) {
                        fetchInstalledTSAddon()
                    }
                },
            )
        }*/

        val versionCompare = compareVersionNames(localVersion, addon.version)
        if (versionCompare > 0) {
            addonManager.uninstallAddon(
                addon,
                onSuccess = {
                    addonManager.installAddon(
                        addon.id,
                        localResource, null,
                        onSuccess = {
                            fetchInstalledTSAddon()
                        },
                    )
                },
            )
            /*addonManager.installAddon(
                addon.id,
                localResource, null,
                onSuccess = {
                    fetchInstalledTSAddon()
                },
            )*/
        } else if (versionCompare == 0) {
            val onlineVersion = try {
                addon.downloadUrl.let {
                    it.substring(
                        it.lastIndexOf("-") + 1,
                        it.lastIndexOf("."),
                    )
                }
            } catch (_: Exception) {
                addon.version
            }
            val compare = compareVersionNames(onlineVersion, addon.version)
            if (compare > 0) {
                addonManager.installAddon(
                    addon.id,
                    addon.downloadUrl, null,
                    onSuccess = {
                        fetchInstalledTSAddon()
                    },
                )
            }
        } else {
            addonManager.updateAddon(
                addon.id,
                onFinish = { status ->
                    if (status == AddonUpdater.Status.SuccessfullyUpdated) {
                        fetchInstalledTSAddon()
                    }
                },
            )
        }

    }

    @Suppress("SameParameterValue")
    private fun compareVersionNames(version1: String, version2: String): Int {
        try {
            val parts1 = version1.split(".")
            val parts2 = version2.split(".")
            val length = max(parts1.size, parts2.size)
            for (i in 0 until length) {
                val part1 = if (i < parts1.size) parts1[i].toInt() else 0
                val part2 = if (i < parts2.size) parts2[i].toInt() else 0
                if (part1 > part2) return 1
                if (part1 < part2) return -1
            }
            return 0
        } catch (_: Exception) {
        }
        return -1
    }

    /**
     * 获取已安装翻译插件*
     3.

     */
    private fun fetchInstalledTSAddon() {
        CoroutineScope(Dispatchers.IO).launch {
            installedTsAddon = immersiveTranslateAddonGetter.getInstalledImmersiveAddon()
        }
    }

    fun getInstalledTSAddon(): Addon? {
        return installedTsAddon
    }

    private fun callRegisterMessageHandler(id: String) {
        MainScope().launch(Dispatchers.Main) {
            registerMessageHandler(id)
        }
    }

    private fun registerMessageHandler(id: String) {
        addonManager.registerAddonMessageHandler(
            id, "imt_connector",
            object : MessageHandler {
                override fun onPortConnected(port: Port) {
                    val message = JSONObject()
                    message.put("type", "sayHello")
                    val data = JSONObject()
                    message.put("data", data)
                    port.postMessage(message)
                }

                override fun onMessage(message: Any, source: EngineSession?): Any? {
                    return super.onMessage(message, source)
                }

                override fun onPortMessage(message: Any, port: Port) {
                    super.onPortMessage(message, port)
                }

                override fun onPortDisconnected(port: Port) {
                    super.onPortDisconnected(port)
                }
            },
        )
    }
}
