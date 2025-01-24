/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package org.mozilla.fenix.immersive_transalte.base.http

import okhttp3.OkHttpClient
import org.mozilla.fenix.BuildConfig
import org.mozilla.fenix.immersive_transalte.base.http.interceptor.HttpAuthInterceptor
import org.mozilla.fenix.immersive_transalte.base.http.interceptor.HttpCacheInterceptor
import org.mozilla.fenix.immersive_transalte.base.http.interceptor.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

/**
 * created by xupx
 * 网络客户端
 */
object HttpClient {

    private const val CACHE_SIZE = 1024 * 1024 * 10L // 10M
    private const val CONNECT_TIME_OUT = 10 * 1000L // 20s
    private const val READ_TIME_OUT = 10 * 1000L // 20s
    private const val WRITE_TIME_OUT = 10 * 1000L // 20s

    // api url
    var baseApiUrl: String? = null
        set(value) {
            if (value == null || value == field) {
                return
            }
            field = value
            retrofit = null
        }

    var retrofit: Retrofit? = null
        get() {
            if (field == null) {
                field = createRetrofitClient();
            }
            return field;
        }

    private val httpClint: OkHttpClient

    init {
        // OkHttp
        val httpBuilder = OkHttpClient.Builder()
        httpBuilder.connectTimeout(CONNECT_TIME_OUT, TimeUnit.MILLISECONDS)
        httpBuilder.readTimeout(READ_TIME_OUT, TimeUnit.MILLISECONDS)
        httpBuilder.writeTimeout(WRITE_TIME_OUT, TimeUnit.MILLISECONDS)
        httpBuilder.addNetworkInterceptor(HttpCacheInterceptor())
        httpBuilder.addInterceptor(HttpAuthInterceptor())

        if (BuildConfig.DEBUG) {
            httpBuilder.addInterceptor(HttpLoggingInterceptor())
        }

        /*val cachePath =
            "${BaseApplication.getApplication().cacheDir}" + File.separator + HttpConstant.HTTP_CACHE
        httpBuilder.cache(Cache(File(cachePath), CACHE_SIZE))*/
        // https配置
        /*val sslParams = HttpsUtils.getSslSocketFactory(null, null, null)
        httpBuilder.sslSocketFactory(sslParams.sSLSocketFactory, sslParams.trustManager)
        httpBuilder.hostnameVerifier(sslParams.unSafeHostnameVerifier)*/

        // http client
        httpClint = httpBuilder.build()
    }

    private fun createRetrofitClient(): Retrofit {
        return Retrofit.Builder()
            .baseUrl(baseApiUrl!!)
            .client(httpClint)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

}
