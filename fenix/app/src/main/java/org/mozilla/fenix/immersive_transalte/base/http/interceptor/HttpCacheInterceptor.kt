/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package org.mozilla.fenix.immersive_transalte.base.http.interceptor

import android.text.TextUtils
import okhttp3.Interceptor
import okhttp3.Response
import java.io.IOException

/**
 * created by xupx
 * 网络缓存拦截器
 */
class HttpCacheInterceptor : Interceptor {
    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        //读接口上的@Headers里的注解配置
        val cacheControl = request.cacheControl().toString()
        val reqHasCacheControl = !TextUtils.isEmpty(cacheControl) && cacheControl.contains(
            MAX_AGE
        )
        val response = chain.proceed(request)
        val builder = response.newBuilder()
        if (reqHasCacheControl && response.code() == 200) {
            builder.removeHeader("Pragma").header("Cache-Control", cacheControl)
        } else {
            // 如果请求失败，或者
            builder.removeHeader("Pragma").removeHeader("Cache-Control")
        }
        return builder.build()
    }

    companion object {
        private const val MAX_AGE = "max-age"
    }
}
