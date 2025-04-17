/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package org.mozilla.fenix.immersive_transalte.webmessage

import android.app.Activity
import android.content.Intent
import android.net.Uri
import com.immersivetranslate.mltextdetect.detect.ImageTextDetectManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import mozilla.components.feature.contextmenu.TranslateImageLinkHolder
import org.json.JSONObject
import org.mozilla.fenix.HomeActivity
import org.mozilla.fenix.ext.openSetDefaultBrowserOption
import org.mozilla.fenix.immersive_transalte.user.UserManager
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

interface OnPageCallback {
    fun onPageTranslateStateChange(
        sessionId: String?,
        pageTranslated: Boolean,
    )
}

object JavaScriptMessageHandler {
    private val pageStateCallbacks = ArrayList<OnPageCallback>()
    private val scope = MainScope()

    fun register(context: Activity) {
        registerDefaultBrowserHandler(context)
        registerShareHandler(context)
        registerSyncUserLoginHandler(context)
        registerGotoBuyVipHandler(context)
        registerPageTranslateStateHandler()
        registerImageTextRecognitionHandler()
        registerGetUserInfoHandler(context)
    }

    private fun registerDefaultBrowserHandler(context: Activity) {
        // 查询是否为默认浏览器
        WebMessageBridge.registerHandler(
            "isDefaultBrowser",
            object : RequestHandler {
                override fun process(
                    message: WebMessage,
                    callback: (response: JSONObject?) -> Unit,
                ) {
                    val intent = Intent(Intent.ACTION_VIEW,
                        Uri.parse("http://www.baidu.com"))
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    val res = context.packageManager.resolveActivity(intent, 0)
                    val packageName = res?.activityInfo?.packageName
                    val isDefault = context.packageName.equals(packageName)

                    val result = getResult(true)
                    result.put("isDefaultBrowser", isDefault)
                    callback(result)
                }
            },
        )

        // 设置默认浏览器
        WebMessageBridge.registerHandler(
            "setDefaultBrowser",
            object : RequestHandler {
                override fun process(
                    message: WebMessage,
                    callback: (response: JSONObject?) -> Unit,
                ) {
                    context.openSetDefaultBrowserOption(useCustomTab = true)
                    callback(getResult(true))
                }
            },
        )
    }

    private fun registerShareHandler(context: Activity) {
        WebMessageBridge.registerHandler(
            "shareContent",
            object : RequestHandler {
                override fun process(
                    message: WebMessage,
                    callback: (response: JSONObject?) -> Unit,
                ) {
                    val data = message.data

                    val content = data?.optString("content")
                    val url = data?.optString("url")
                    var shareContent = data?.optString("title") ?: ""

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

                    callback(getResult(true))
                }
            },
        )
    }

    private fun registerSyncUserLoginHandler(context: Activity) {
        WebMessageBridge.registerHandler(
            "syncLoginData",
            object : RequestHandler {
                override fun process(
                    message: WebMessage,
                    callback: (response: JSONObject?) -> Unit,
                ) {
                    val data = message.data
                    data?.let {
                        UserManager.saveUser(context, it.toString())
                    }
                    callback(getResult(true))
                }
            },
        )
    }

    private fun registerGotoBuyVipHandler(context: Activity) {
        WebMessageBridge.registerHandler(
            "gotoUpgrade",
            object : RequestHandler {
                override fun process(
                    message: WebMessage,
                    callback: (response: JSONObject?) -> Unit,
                ) {
                    (context as HomeActivity).navigateToBuyVip()
                    callback(getResult(true))
                }
            },
        )
    }

    private fun registerPageTranslateStateHandler() {
        WebMessageBridge.registerHandler(
            "updateTranslateState",
            object : RequestHandler {
                override fun process(
                    message: WebMessage,
                    callback: (response: JSONObject?) -> Unit,
                ) {
                    val data = message.data
                    data?.let {
                        try {
                            val pageTranslated = it.optBoolean("pageTranslated")
                            pageStateCallbacks.forEach { page ->
                                page.onPageTranslateStateChange(
                                    message.sessionId, pageTranslated,
                                )
                            }
                        } finally {
                        }
                    }
                    callback(getResult(true))
                }
            },
        )
    }

    private fun registerImageTextRecognitionHandler() {
        WebMessageBridge.registerHandler(
            "imageTextRecognition",
            object : RequestHandler {
                override fun process(
                    message: WebMessage,
                    callback: (response: JSONObject?) -> Unit,
                ) {
                    scope.launch(Dispatchers.Main) {
                        val response = withContext(Dispatchers.IO) {
                            postImageRequest(message)
                        }
                        callback(response)
                    }
                }
            },
        )
    }

    private suspend fun postImageRequest(
        message: WebMessage,
    ): JSONObject {
        val data = message.data
        val imageId = data?.optString("imageId")
        val imageData = data?.optString("imageData")
        val imageUrl = data?.optString("imageUrl")
        return suspendCoroutine { continuation ->
            ImageTextDetectManager.postRequest(imageId, imageData) { success, response ->
                // save map imageId -> imageUrl
                if (success) {
                    imageId?.let { imgId ->
                        imageUrl?.let { url ->
                            TranslateImageLinkHolder.save(imgId, url)
                        }
                    }
                }
                continuation.resume(response)
            }
        }
    }

    private fun registerGetUserInfoHandler(context: Activity) {
        WebMessageBridge.registerHandler(
            "getUserInfo",
            object : RequestHandler {
                override fun process(
                    message: WebMessage,
                    callback: (response: JSONObject?) -> Unit,
                ) {
                    val jsonObject = UserManager.getUserInfo(context)?.let {
                        try {
                            JSONObject(it)
                        } catch (_: Exception) {
                            null
                        }
                    }
                    val result = getResult(true)
                    jsonObject?.let {
                        result.put("data", jsonObject)
                    }
                    callback(result)
                }
            },
        )
    }

    fun addPageStateCallback(onPageCallback: OnPageCallback) {
        if (!pageStateCallbacks.contains(onPageCallback)) {
            pageStateCallbacks.add(onPageCallback)
        }
    }

    fun removePageStateCallback(onPageCallback: OnPageCallback) {
        pageStateCallbacks.remove(onPageCallback)
    }

    private fun getResult(isOK: Boolean): JSONObject {
        val result = JSONObject()
        result.put("isOk", isOK)
        return result
    }

}
