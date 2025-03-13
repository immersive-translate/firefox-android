/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package org.mozilla.fenix.immersive_transalte.net.api

import org.mozilla.fenix.immersive_transalte.bean.AppConfigBean
import org.mozilla.fenix.immersive_transalte.bean.HomePageBean
import org.mozilla.fenix.immersive_transalte.bean.OnBoardingTranslateBean
import org.mozilla.fenix.immersive_transalte.bean.ResultData
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.HeaderMap
import retrofit2.http.Headers
import retrofit2.http.QueryMap

internal interface HomePageApi {

    @GET("/v1/app-home/toplinks")
    @Headers("Cache-Control: public, max-age=60")
    fun fetchTopLinks(
        @QueryMap params: MutableMap<String, Any?>,
    ): Call<ResultData<HomePageBean>>

    @GET("/v1/app/globalconfig")
    @Headers("Cache-Control: public, max-age=60")
    fun fetchAppConfig(
        @HeaderMap headers: MutableMap<String, Any?>,
    ): Call<ResultData<AppConfigBean>>

    @GET("/v1/app/load-onboarding-translations")
    fun fetchOnBoardingTranslations(
        @HeaderMap headers: MutableMap<String, Any?>,
        @QueryMap params: MutableMap<String, Any?>,
    ): Call<ResultData<List<OnBoardingTranslateBean>>>
}
