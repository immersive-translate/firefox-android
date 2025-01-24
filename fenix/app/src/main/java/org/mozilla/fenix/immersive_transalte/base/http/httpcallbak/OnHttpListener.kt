/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package org.mozilla.fenix.immersive_transalte.base.http.httpcallbak

import org.mozilla.fenix.immersive_transalte.base.http.Response

/**
 * create by xupx
 * 网络请求，回调
 *
 * @param <T>
</T> */
abstract class OnHttpListener<T> protected constructor() {

    fun doSuccess(result: Response<T>) {
        onSuccess(result)
    }

    fun doError(result: Response<T>) {
        onError(result)
    }

    abstract fun onSuccess(result: Response<T>)

    open fun onError(result: Response<T>) {}
}
