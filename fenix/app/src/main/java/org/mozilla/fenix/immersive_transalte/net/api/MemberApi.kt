/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package org.mozilla.fenix.immersive_transalte.net.api

import org.mozilla.fenix.immersive_transalte.bean.OrderBean
import org.mozilla.fenix.immersive_transalte.bean.ResultData
import org.mozilla.fenix.immersive_transalte.bean.UpgradeBean
import org.mozilla.fenix.immersive_transalte.bean.UserBean
import org.mozilla.fenix.immersive_transalte.bean.VipProductBean
import org.mozilla.fenix.immersive_transalte.bean.VipUpgradeBean
import org.mozilla.fenix.immersive_transalte.bean.WebLoginBean
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
    ): Call<ResultData<VipUpgradeBean>>


    @POST("v1/user-subscription/one-click-upgrade")
    fun vipUpgrade(
        @HeaderMap headers: MutableMap<String, Any?>,
        @QueryMap params: MutableMap<String, Any?>,
    ): Call<ResultData<UpgradeBean>>

    @POST("/v1/user-account/get-web-login-url")
    @Headers("Content-Type: application/json")
    fun webLogin(
        @HeaderMap headers: MutableMap<String, Any?>,
        @Body params: MutableMap<String, Any?>,
    ): Call<ResultData<WebLoginBean>>

    @POST("/v1/user/device-info?force=true")
    @Headers("Content-Type: application/json")
    fun updateDeviceInfo(
        @HeaderMap headers: MutableMap<String, Any?>,
        @Body params: MutableMap<String, Any?>,
    ): Call<Any?>

    @POST("/v1/user-account/find-password-code")
    @Headers("Content-Type: application/json")
    fun getPwdCode(
        @HeaderMap headers: MutableMap<String, Any?>,
        @Body params: MutableMap<String, Any?>,
    ): Call<Any?>

    @POST("/v1/user-account/reset-password")
    @Headers("Content-Type: application/json")
    fun resetPassword(
        @HeaderMap headers: MutableMap<String, Any?>,
        @Body params: MutableMap<String, Any?>,
    ): Call<Any?>

    @POST("/v1/user-account/re-activate")
    @Headers("Content-Type: application/json")
    fun getActiveEmailVerifyCode(
        @HeaderMap headers: MutableMap<String, Any?>,
        @Body params: MutableMap<String, Any?>,
    ): Call<Any?>

    @POST("/v1/user-account/register")
    @Headers("Content-Type: application/json")
    fun register(
        @HeaderMap headers: MutableMap<String, Any?>,
        @Body params: MutableMap<String, Any?>,
    ): Call<Any?>

    @POST("/v1/user-account/activate")
    @Headers("Content-Type: application/json")
    fun activeEmail(
        @HeaderMap headers: MutableMap<String, Any?>,
        @Body params: MutableMap<String, Any?>,
    ): Call<Any?>

    @GET("/v1/user-account/web-oauth-callback")
    fun loginWithAccessToken(
        @HeaderMap headers: MutableMap<String, Any?>,
        @QueryMap params: MutableMap<String, Any?>,
    ): Call<ResultData<WebLoginBean>>

    @GET("/v1/user-account/wechat-app-oauth-callback")
    fun loginWithWxToken(
        @HeaderMap headers: MutableMap<String, Any?>,
        @QueryMap params: MutableMap<String, Any?>,
    ): Call<ResultData<WebLoginBean>>
}
