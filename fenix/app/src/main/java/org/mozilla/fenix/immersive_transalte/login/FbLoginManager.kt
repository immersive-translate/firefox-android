/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package org.mozilla.fenix.immersive_transalte.login

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.util.Base64
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import org.json.JSONObject
import org.mozilla.fenix.immersive_transalte.utils.SimpleEventBus
import java.security.MessageDigest

class FbLoginManager {
    private val callbackManager = CallbackManager.Factory.create()

    init {
        // 注册回调
        LoginManager.getInstance().registerCallback(
            callbackManager,
            object : FacebookCallback<LoginResult> {
                override fun onSuccess(result: LoginResult) {
                    val token = result.accessToken.token
                    postMessage(token)
                }

                override fun onCancel() {
                }

                override fun onError(error: FacebookException) {
                }
            },
        )
    }

    /**
     * 发送消息
     */
    private fun postMessage(token: String) {
        val params: MutableMap<String, String?> = mutableMapOf()
        params["state"] = "Facebook_imt"
        params["access_token"] = token
        val jsonObject = JSONObject()
        jsonObject.put("type", "facebook")
        jsonObject.put("params", params)
        SimpleEventBus.postEvent(SimpleEventBus.EVENT_WEB_LOGIN_GET_TOKEN, jsonObject)
    }

    /**
     * 是否安装 FB
     */
    fun isInstall(context: Context): Boolean {
        val pm = context.packageManager
        val fbPackages = listOf("com.facebook.katana", "com.facebook.lite")
        return fbPackages.any {
            try {
                pm.getPackageInfo(it, 0)
                true
            } catch (e: PackageManager.NameNotFoundException) {
                false
            }
        }
    }

    @Suppress("DEPRECATION")
    fun requestLogin(fragment: Fragment) {
        LoginManager.getInstance().logInWithReadPermissions(
            fragment,
            listOf("public_profile", "email"),
        )
    }

    fun handlerResult(requestCode: Int, resultCode: Int, data: Intent?) {
        callbackManager.onActivityResult(requestCode, resultCode, data)
    }

    @RequiresApi(Build.VERSION_CODES.P)
    fun printFacebookKeyHash(context: Context) {
        try {
            val info = context.packageManager.getPackageInfo(
                context.packageName,
                PackageManager.GET_SIGNING_CERTIFICATES,
            )

            for (signature in info.signingInfo?.apkContentsSigners!!) {
                val md = MessageDigest.getInstance("SHA")
                md.update(signature.toByteArray())
                val keyHash = Base64.encodeToString(md.digest(), Base64.NO_WRAP)
                Log.i("Facebook", "Facebook KeyHash: $keyHash")
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
