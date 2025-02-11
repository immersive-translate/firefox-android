/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package org.mozilla.fenix.immersive_transalte

import android.app.Activity
import android.content.Intent
import android.net.Uri
import com.google.gson.JsonObject
import mozilla.components.jsbridge.JSBridgeInstance
import mozilla.components.jsbridge.JSBridgeInstance.OnJavaScriptCallback
import mozilla.components.jsbridge.OnBridgeCallback
import org.mozilla.fenix.HomeActivity
import org.mozilla.fenix.ext.openSetDefaultBrowserOption
import org.mozilla.fenix.immersive_transalte.user.UserManager
import org.mozilla.geckoview.GeckoSession


object JsBridge {

    /**
     * init javascript bridge
     */
    fun init(context: Activity) {
        JSBridgeInstance.getInstance().init(context)
        JSBridgeInstance.getInstance().setOnJavaScriptCallback(
            object : OnJavaScriptCallback {
                override fun onJsCall(
                    geckoSession: GeckoSession,
                    jsonObject: JsonObject,
                    callback: OnBridgeCallback?,
                ) {
                    handleJsCall(context, jsonObject, callback)
                }
            },
        )
    }

    private fun handleJsCall(
        context: Activity,
        jsonObject: JsonObject,
        callback: OnBridgeCallback?,
    ) {

        val type = jsonObject.get("type")?.asString
        type?.let {
            when (it) {
                "isDefaultBrowser" -> {
                    queryDefaultBrowser(context, callback)
                }

                "setDefaultBrowser" -> {
                    handleDefaultBrowser(context)
                    callback?.onCallBack(getResult(true))
                }

                "shareContent" -> {
                    handleShare(context, jsonObject)
                    callback?.onCallBack(getResult(true))
                }

                "syncLoginData" -> {
                    handleLogin(context, jsonObject)
                }

                "gotoUpgrade" -> {
                    handleGotoUpgrade(context)
                }

                else -> {}
            }
        }

    }


    /**
     * 去购买页面
     */
    private fun handleGotoUpgrade(context: Activity) {
        (context as HomeActivity).navigateToBuyVip()
    }

    /**
     * 用户登录
     */
    private fun handleLogin(context: Activity, jsonObject: JsonObject) {
        UserManager.saveUser(context, jsonObject.toString())
    }

    /**
     * 查询是否是默认浏览器
     */
    private fun queryDefaultBrowser(
        context: Activity,
        callback: OnBridgeCallback?,
    ) {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse("http://www.baidu.com"))
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        val res = context.packageManager.resolveActivity(intent, 0)
        val packageName = res?.activityInfo?.packageName
        val isDefault = context.packageName.equals(packageName)

        val result = getResult(true)
        result.addProperty("isDefaultBrowser", isDefault)
        callback?.onCallBack(result)
    }

    /**
     * 设置默认浏览器
     */
    private fun handleDefaultBrowser(context: Activity) {
        context.openSetDefaultBrowserOption(useCustomTab = true)
    }

    /**
     * 分享
     */
    private fun handleShare(
        context: Activity,
        jsonObject: JsonObject,
    ) {
        val content = jsonObject.get("content")?.asString
        val url = jsonObject.get("url")?.asString

        var shareContent = jsonObject.get("title")?.asString ?: ""
        content?.let {
            shareContent = "$shareContent  $it"
        }
        url?.let {
            shareContent = "$shareContent  $it"
        }

        val shareIntent = Intent(Intent.ACTION_SEND)
        shareIntent.setType("text/plain")
        shareIntent.putExtra(Intent.EXTRA_TEXT, shareContent)
        context.startActivity(shareIntent)
    }

    private fun getResult(isOK: Boolean): JsonObject {
        val result = JsonObject()
        result.addProperty("isOk", isOK)
        return result
    }
}
