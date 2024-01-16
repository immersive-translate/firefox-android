/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package org.mozilla.fenix.immersive_transalte

import mozilla.components.feature.addons.Addon
import mozilla.components.feature.addons.AddonManager
import mozilla.components.support.webextensions.WebExtensionSupport

/**
 * created by xupx
 * on 2023-12-24
 */
class ImmersiveTranslateAddonGetter(
    private val addonManager: AddonManager,
) {

    /**
     * 创建插件对象
     */
    suspend fun createImmersiveAddon(): Addon {
        return getInstalledImmersiveAddon() ?: generateImmersiveAddon()
    }

    private fun generateImmersiveAddon(): Addon {
        return Addon(
            id, author, downloadUrl, version,
            permissions, emptyList(), emptyList(),
            translatableName, translatableDescription,
            translatableSummary, iconUrl, homepageUrl, rating,
            createdAt, updatedAt, null, null,
            defaultLocale, ratingUrl, detailUrl,
        )
    }

    /**
     * 获取安装的翻译插件
     */
    suspend fun getInstalledImmersiveAddon(): Addon? {
        try {
            WebExtensionSupport.awaitInitialization()
            val installedAddons = WebExtensionSupport.installedExtensions
                .filterValues { !it.isBuiltIn() }
                .map {
                    val extension = it.value
                    val installedState = addonManager.toInstalledState(extension)
                    Addon.newFromWebExtension(extension, installedState)
                }
            if (installedAddons.isNotEmpty()) {
                val addons = installedAddons.filter { id == it.id }.toList()
                if (addons.isNotEmpty()) {
                    return addons.first()
                }
            }
        } catch (_: Exception) {
        }
        return null
    }

    companion object {
        //const val id = "{5efceaa7-f3a2-4e59-a54b-85319448e306}"
        //const val id = "{5efceaa7-f3a2-4e59-a54b-85319448e305}"
        const val id = ImmersivePluginConfig.localPluginId
        val author = Addon.Author(
            "Immersive Translate",
            "https://addons.mozilla.org/zh-CN/android/user/17891955/",
        )
        val createdAt = ""
        val defaultLocale = "en-us"
        val detailUrl = "https://addons.mozilla.org/zh-CN/android/addon/immersive-translate/"

        //val downloadUrl = "https://addons.mozilla.org/android/downloads/file/4214378/immersive_translate-0.12.13.xpi"
        //val downloadUrl = "resource://android/assets/ts/immersive_translate_beta-1.1.3/"
        val downloadUrl = ImmersivePluginConfig.localPluginResource

        //val downloadUrl = "resource://android/assets/ts/immersive_translate-0.12.13.xpi"
        val homepageUrl = "https://immersivetranslate.com/"

        val icon = ""
        val iconUrl = ""

        val permissions = arrayListOf(
            "<all_urls>", "file:///*", "*://*/*",
        )

        val ratingUrl =
            "https://addons.mozilla.org/zh-CN/android/addon/immersive-translate/reviews/"
        val rating = Addon.Rating(average = 5f, reviews = 10000)

        val translatableDescription = mapOf(
            "en-us" to "主要特性：\n" +
                    "\n" +
                    "沉浸式阅读外文网站 通过智能识别网页主内容区域并进行双语对照翻译，沉浸式翻译提供了全新的外文阅读体验，因此得名“沉浸式翻译”。\n" +
                    "强大的输入框翻译，将任何网页上的输入框化身为多语言翻译器，立刻解锁谷歌搜索，ChatGPT 等工具的双语实时对话体验。\n" +
                    "高效的文件翻译 一键导出双语电子书，同时支持 PDF、字幕、TXT 等文件的实时双语翻译。\n" +
                    "创新的鼠标悬停翻译 仅需将鼠标停留在任意网页的任意段落上，相应的译文就会立即出现在段落下方。段落在在沉浸式翻译的设计理念中被视为最小单位，保留其上下文，这样我们才能真正理解并学习外语。\n" +
                    "深度定制优化主流网站 针对 Google、Twitter、Reddit、YouTube、彭博社、华尔街日报等主流网站进行优化，无论是搜索、社交还是获取资讯，都更加流畅高效。\n" +
                    "全平台支持 除了各大桌面端浏览器，移动设备也可享受同样的沉浸式翻译体验。在 iOS Safari、安卓 Kiwi 浏览器等移动端浏览器上轻松实现双语浏览 Twitter,Reddit\n" +
                    "等社交媒体。\n" +
                    "支持 10+种翻译服务 在沉浸式翻译中，你可以选择超过 10 种翻译服务，如 Deepl、OpenAI、微软翻译、谷歌翻译、腾讯翻译等等，这份名单还在不断增加中。\n" +
                    "\n" +
                    "\n" +
                    "Privacy policy\n" +
                    "\n" +
                    "We do not collect any information. However, to translate, the contents of the web pages will be sent to third party translation services, like Google, Microsoft, OpenAI, DeepL, Transmart, volc, Tencent, Baidu, Caiyun, Openl, Youdao, Niu.",
        )

        val translatableName = mapOf("en-us" to "双语对照网页翻译 & PDF文档翻译")

        val translatableSummary =
            mapOf("en-us" to "沉浸式网页双语翻译扩展，支持PDF翻译，双语Epub电子书制作，Youtube/Netflix/Udemy 等平台双语字幕，支持Deepl/Google等多个翻译服务，免费使用。")

        val updatedAt = "2023-12-24T06:10:48Z"
        //val version = "1.1.3"
        val version = ImmersivePluginConfig.localPluginVersion
    }
}
