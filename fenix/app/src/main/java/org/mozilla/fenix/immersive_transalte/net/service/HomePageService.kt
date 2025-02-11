/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package org.mozilla.fenix.immersive_transalte.net.service

import org.mozilla.fenix.immersive_transalte.base.http.BaseService
import org.mozilla.fenix.immersive_transalte.base.http.HttpClient
import org.mozilla.fenix.immersive_transalte.base.http.Response
import org.mozilla.fenix.immersive_transalte.bean.HomePageBean
import org.mozilla.fenix.immersive_transalte.bean.ResultData
import org.mozilla.fenix.immersive_transalte.net.api.HomePageApi

object HomePageService : BaseService() {
    private val homepageApi: HomePageApi? by lazy { HttpClient.retrofit?.create(HomePageApi::class.java) }

    /**
     * app 首页 运营坑位链接
     */
    suspend fun fetchHomeTopLinks(): Response<ResultData<HomePageBean>> {
        val params = getCommonQueryParams()
        return executeHttpAndCallback(homepageApi?.fetchTopLinks(params))
    }

}
