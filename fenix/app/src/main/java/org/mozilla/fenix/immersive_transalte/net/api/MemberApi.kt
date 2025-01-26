/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package org.mozilla.fenix.immersive_transalte.net.api

import com.google.gson.JsonObject
import org.mozilla.fenix.immersive_transalte.bean.OrderBean
import org.mozilla.fenix.immersive_transalte.bean.ResultData
import org.mozilla.fenix.immersive_transalte.bean.VipUpgradeBean
import org.mozilla.fenix.immersive_transalte.bean.UserBean
import org.mozilla.fenix.immersive_transalte.bean.VipProductBean
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.HeaderMap
import retrofit2.http.Headers
import retrofit2.http.POST
import retrofit2.http.QueryMap
import retrofit2.http.Url

internal interface MemberApi {

    @GET
    fun getProducts(
        @Url url: String,
        @QueryMap params: MutableMap<String, Any?>,
    ): Call<VipProductBean>

    @GET("/v1/user/get-trial-product-id")
    fun queryTrail(
        @HeaderMap headers: MutableMap<String, Any?>,
        @QueryMap params: MutableMap<String, Any?>,
    ): Call<ResultData<List<String>>>

    //@POST("/v1/user/subs-checkout-sessions-for-android")
    @POST("/v1/user/subs-checkout-sessions")
    @Headers("Content-Type: application/json")
    // @FormUrlEncoded
    fun createOrder(
        @HeaderMap headers: MutableMap<String, Any?>,
        @Body params: MutableMap<String, Any?>,
    ): Call<ResultData<OrderBean>>

    @GET("/v1/user")
    fun getUser(
        @HeaderMap headers: MutableMap<String, Any?>,
        @QueryMap params: MutableMap<String, Any?>,
    ): Call<ResultData<UserBean>>

    @POST("/v1/user-subscription/upcoming")
    fun upcoming(
        @HeaderMap headers: MutableMap<String, Any?>,
        @QueryMap params: MutableMap<String, Any?>,
    ) :Call<ResultData<VipUpgradeBean>>


    @POST("v1/user-subscription/one-click-upgrade")
    fun vipUpgrade(
        @HeaderMap headers: MutableMap<String, Any?>,
        @QueryMap params: MutableMap<String, Any?>
    ): Call<ResultData<JsonObject>>

}
