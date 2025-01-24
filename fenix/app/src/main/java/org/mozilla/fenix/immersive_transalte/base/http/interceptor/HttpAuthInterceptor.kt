/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package org.mozilla.fenix.immersive_transalte.base.http.interceptor

import okhttp3.Interceptor
import okhttp3.Response
import java.io.IOException

/**
 * created by xupx
 * 用于认证的 拦截器
 */
class HttpAuthInterceptor : Interceptor {

    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        return chain.proceed(chain.request())
    }

}
