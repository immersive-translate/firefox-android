/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package org.mozilla.fenix.immersive_transalte.base.http

import android.os.Build
import android.text.TextUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject
import org.mozilla.fenix.FenixApplication
import org.mozilla.fenix.immersive_transalte.Constant
import org.mozilla.fenix.immersive_transalte.base.http.httpcallbak.OnHttpListener
import org.mozilla.fenix.immersive_transalte.user.UserManager
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine


open class BaseService {

    companion object {
        private val scope = MainScope()
        init {
            HttpClient.baseApiUrl = Constant.apiBaseUrl
        }
    }

    protected val appVersionName: String? by lazy {
        try {
            val cxt = FenixApplication.application
            cxt.packageManager.getPackageInfo(cxt.packageName, 0).versionName
        } catch (_: Exception) {
            ""
        }
    }

    @Suppress("DEPRECATION")
    protected val appVersionCode: Long by lazy {
        try {
            val cxt = FenixApplication.application
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                cxt.packageManager.getPackageInfo(cxt.packageName, 0).longVersionCode
            } else {
                cxt.packageManager.getPackageInfo(cxt.packageName, 0).versionCode.toLong()
            }
        } catch (_: Exception) {
            Long.MAX_VALUE
        }
    }

    private val token: String?
        get() = UserManager.getUserToken(FenixApplication.application)

    private val time: Long
        get() = System.currentTimeMillis()

    fun getCommonBodyParams(): MutableMap<String, Any?> {
        val params: MutableMap<String, Any?> = HashMap()
        params["appVersion"] = appVersionName
        params["platForm"] = "android"
        return params
    }

    fun getCommonQueryParams(): MutableMap<String, Any?> {
        val params: MutableMap<String, Any?> = HashMap()
        params["t"] = time
        params["appVersion"] = appVersionName
        params["platForm"] = "android"
        return params
    }

    fun getHeadersMap(): MutableMap<String, Any?> {
        val params: MutableMap<String, Any?> = HashMap()
        params["appVersion"] = appVersionName
        params["platForm"] = "android"
        token?.let {
            params["token"] = it
        }
        return params
    }

    /**
     * 执行 网络 回调
     *
     * @param onHttpListener 回调监听
     * @param call           okHttp call
     * @param <T>            返回对象
     */
    fun <T> exeHttpAndCallback(
        onHttpListener: OnHttpListener<T>?,
        call: Call<T>?,
    ): Call<T>? {
        call?.enqueue(
            object : Callback<T?> {

                override fun onResponse(
                    call: Call<T?>,
                    response: Response<T?>,
                ) {
                    val resultObject = Response<T>()
                    resultObject.code = response.code()
                    resultObject.data = response.body()
                    resultObject.msg = response.message()

                    if (response.code() == 200) {
                        onHttpListener?.doSuccess(resultObject)
                    } else {
                        val errorString = response.errorBody()?.string()
                        if (!TextUtils.isEmpty(errorString)) {
                            handleError(resultObject, errorString!!)
                        } else {
                            onHttpListener?.doError(resultObject)
                        }
                    }
                }

                override fun onFailure(call: Call<T?>, t: Throwable) {
                    val result = Response<T>()
                    result.msg = t.message
                    onHttpListener?.doError(result)
                }

                private fun handleError(
                    resultObject : org.mozilla.fenix.immersive_transalte.base.http.Response<T>,
                    errorString: String) {
                    scope.launch(Dispatchers.Main) {
                        val jo = withContext(Dispatchers.IO) {
                            try {
                                JSONObject(errorString)
                            } catch (_: Exception) {
                                null
                            }
                        }
                        jo?.let {
                            resultObject.errorCode = it.optInt("code")
                            resultObject.msg = it.optString("error")
                        }
                        onHttpListener?.doError(resultObject)
                    }
                }
            },
        )
        return call
    }

    /**
     * 执行 网络 回调
     *
     * @param call           okHttp call
     * @param source         request source
     */
    suspend fun <T> executeHttpAndCallback(
        call: Call<T>?,
    ): org.mozilla.fenix.immersive_transalte.base.http.Response<T> {
        return suspendCoroutine { continuation ->
            exeHttpAndCallback(
                object : OnHttpListener<T>() {
                    override fun onSuccess(result: org.mozilla.fenix.immersive_transalte.base.http.Response<T>) {
                        continuation.resume(result)
                    }

                    override fun onError(result: org.mozilla.fenix.immersive_transalte.base.http.Response<T>) {
                        continuation.resume(result)
                    }
                },
                call,
            )
        }
    }

}
