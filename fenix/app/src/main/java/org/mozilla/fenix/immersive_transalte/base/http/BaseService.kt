/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package org.mozilla.fenix.immersive_transalte.base.http

import org.mozilla.fenix.FenixApplication
import org.mozilla.fenix.immersive_transalte.base.http.httpcallbak.OnHttpListener
import org.mozilla.fenix.immersive_transalte.user.UserManager
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine


open class BaseService {

    /*private val appVersion: String? by lazy { AppUtil.getVersion(EnvironmentUtil.getContext()) }

    private val userId: String?
        get() = user?.uid

    private val userAuth: String?
        get() = user?.auth*/

    private val time: Long
        get() = System.currentTimeMillis()

    fun getCommonBodyParams(): MutableMap<String, Any?> {
        val params: MutableMap<String, Any?> = HashMap()
        /*appVersion?.let { params["version"] = it }
        userId?.let { params["uid"] = it }
        userAuth?.let { params["auth"] = it }*/
        return params
    }

    fun getCommonQueryParams(): MutableMap<String, Any?> {
        val params: MutableMap<String, Any?> = HashMap()
        params["t"] = time
        return params
    }

    fun getHeadersMap(): MutableMap<String, Any?> {
        val params: MutableMap<String, Any?> = HashMap()
        params["token"] = UserManager.getUserToken(FenixApplication.application)
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
                        onHttpListener?.doError(resultObject)
                    }
                }

                override fun onFailure(call: Call<T?>, t: Throwable) {
                    val result = Response<T>()
                    result.msg = t.message
                    onHttpListener?.doError(result)
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
