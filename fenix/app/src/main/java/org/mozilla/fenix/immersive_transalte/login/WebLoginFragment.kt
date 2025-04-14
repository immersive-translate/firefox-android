/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package org.mozilla.fenix.immersive_transalte.login

import android.annotation.SuppressLint
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import org.json.JSONObject
import org.mozilla.fenix.databinding.FragmentWebLoginLayoutBinding
import org.mozilla.fenix.ext.components
import org.mozilla.fenix.immersive_transalte.utils.SimpleEventBus
import org.mozilla.geckoview.AllowOrDeny
import org.mozilla.geckoview.GeckoResult
import org.mozilla.geckoview.GeckoRuntime
import org.mozilla.geckoview.GeckoRuntimeSettings
import org.mozilla.geckoview.GeckoSession

class WebLoginFragment : Fragment() {
    companion object {
        private const val APP_WEB_LOGIN_CALLBACK_URL =
            "https://immersivetranslate.com/accounts/callback"

        private const val URL_GOOGLE = "https://accounts.google.com"
        private const val URL_APPLE = "https://appleid.apple.com"
        private const val URL_FACEBOOK = "https://www.facebook.com"

        fun newInstance(
            isDialog: Boolean,
            url: String,
        ): WebLoginFragment {
            val fragment = WebLoginFragment()
            fragment.isDialog = isDialog
            fragment.webLoginUrl = url
            return fragment
        }
    }

    private var isDialog = false
    private lateinit var webLoginUrl: String
    private lateinit var binding: FragmentWebLoginLayoutBinding

    private lateinit var geckoRuntime: GeckoRuntime
    private lateinit var session: GeckoSession
    private var preferredColorScheme: Int = 0

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentWebLoginLayoutBinding.inflate(
            inflater, container, false,
        )
        return binding.root
    }

    @SuppressLint("SetJavaScriptEnabled")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // 强制用light模式
        geckoRuntime = binding.root.context.components.core.geckoRuntime
        preferredColorScheme = geckoRuntime.settings.preferredColorScheme
        geckoRuntime.settings.preferredColorScheme = GeckoRuntimeSettings.COLOR_SCHEME_LIGHT
        initSession()
        binding.geckoview.setSession(session)
        session.loadUri(webLoginUrl)
    }

    private fun initSession() {
        session = GeckoSession()
        session.settings.allowJavascript = true
        session.settings.userAgentOverride = binding.root.context.components.core
            .engine.settings.userAgentString

        session.open(geckoRuntime)
        // 拦截url 获取token
        session.navigationDelegate = object : GeckoSession.NavigationDelegate {
            override fun onLoadRequest(
                session: GeckoSession,
                request: GeckoSession.NavigationDelegate.LoadRequest,
            ): GeckoResult<AllowOrDeny>? {
                var uri = request.uri
                if (uri.contains("callback#")) {
                    uri = uri.replace("callback#", "callback?")
                }
                if (uri.startsWith(APP_WEB_LOGIN_CALLBACK_URL)) {
                    binding.geckoview.postDelayed(
                        {
                            try {
                                handlerCallback(uri)
                            } catch (_: Exception) {
                            }
                        },
                        300,
                    )
                    return GeckoResult.deny()
                }
                return super.onLoadRequest(session, request)
            }
        }

        session.progressDelegate = object : GeckoSession.ProgressDelegate {
            override fun onProgressChange(session: GeckoSession, progress: Int) {
                if (progress >= 60) {
                    binding.progress.visibility = View.GONE
                } else {
                    binding.progress.visibility = View.VISIBLE
                }
            }
        }

    }

    override fun onDestroy() {
        geckoRuntime.settings.preferredColorScheme = preferredColorScheme
        session.progressDelegate = null
        session.navigationDelegate = null
        binding.geckoview.releaseSession()
        session.close()
        super.onDestroy()
    }

    /**
     * 处理回调
     */
    private fun handlerCallback(uri: String) {
        val callbackUri = Uri.parse(uri)
        if (isGoogleLogin()) {
            val params = getAllQueryParameters(callbackUri)
            val jsonObject = JSONObject()
            jsonObject.put("type", "google")
            jsonObject.put("params", params)
            SimpleEventBus.postEvent(SimpleEventBus.EVENT_WEB_LOGIN_GET_TOKEN, jsonObject)
        } else if (isAppleLogin()) {
            val params: MutableMap<String, String?> = mutableMapOf()
            params["state"] = "Apple_imt"
            params["access_token"] = getQueryParameter(callbackUri, "code")
            val jsonObject = JSONObject()
            jsonObject.put("type", "apple")
            jsonObject.put("params", params)
            SimpleEventBus.postEvent(SimpleEventBus.EVENT_WEB_LOGIN_GET_TOKEN, jsonObject)
        } else if (isFacebookLogin()) {
            // val params = getAllQueryParameters(callbackUri)
            val params: MutableMap<String, String?> = mutableMapOf()
            params["state"] = "Facebook_imt"
            params["access_token"] = getQueryParameter(callbackUri, "access_token")
            val jsonObject = JSONObject()
            jsonObject.put("type", "facebook")
            jsonObject.put("params", params)
            SimpleEventBus.postEvent(SimpleEventBus.EVENT_WEB_LOGIN_GET_TOKEN, jsonObject)
        }
    }

    private fun getQueryParameter(uri: Uri, key: String): String? {
        try {
            return uri.getQueryParameter(key)
        } catch (_: Exception) {
        }
        return null
    }

    /**
     * 查询所有的参数
     */
    private fun getAllQueryParameters(uri: Uri): MutableMap<String, String?> {
        val params: MutableMap<String, String?> = mutableMapOf()
        for (name in uri.queryParameterNames) {
            params[name] = uri.getQueryParameter(name)
        }
        return params
    }

    /**
     * 是否是谷歌登录
     */
    private fun isGoogleLogin(): Boolean {
        return webLoginUrl.startsWith(URL_GOOGLE)
    }

    /**
     * 是否是苹果登录
     */
    private fun isAppleLogin(): Boolean {
        return webLoginUrl.startsWith(URL_APPLE)
    }

    /**
     * 是否是FB登录
     */
    private fun isFacebookLogin(): Boolean {
        return webLoginUrl.startsWith(URL_FACEBOOK)
    }
}
