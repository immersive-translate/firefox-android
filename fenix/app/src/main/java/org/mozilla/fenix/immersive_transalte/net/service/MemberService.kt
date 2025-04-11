/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package org.mozilla.fenix.immersive_transalte.net.service

import android.text.TextUtils
import org.mozilla.fenix.FenixApplication
import org.mozilla.fenix.ext.components
import org.mozilla.fenix.immersive_transalte.Constant
import org.mozilla.fenix.immersive_transalte.base.http.BaseService
import org.mozilla.fenix.immersive_transalte.base.http.HttpClient
import org.mozilla.fenix.immersive_transalte.base.http.Response
import org.mozilla.fenix.immersive_transalte.bean.OrderBean
import org.mozilla.fenix.immersive_transalte.bean.ResultData
import org.mozilla.fenix.immersive_transalte.bean.UpgradeBean
import org.mozilla.fenix.immersive_transalte.bean.UserBean
import org.mozilla.fenix.immersive_transalte.bean.VipProductBean
import org.mozilla.fenix.immersive_transalte.bean.VipUpgradeBean
import org.mozilla.fenix.immersive_transalte.bean.WebLoginBean
import org.mozilla.fenix.immersive_transalte.net.api.MemberApi

object MemberService : BaseService() {

    private val memberApi: MemberApi? by lazy { HttpClient.retrofit?.create(MemberApi::class.java) }

    /**
     * 商品接口
     */
    suspend fun getProducts(): Response<VipProductBean> {
        val params = getCommonQueryParams()
        params["group"] = "year_discount_7_year_trial_3"
        params["lang"] = FenixApplication.application.components.settings.defaultTsLanguage
        val url = "${Constant.workerBaseUrl}/goods"
        return executeHttpAndCallback(memberApi?.getProducts(url, params))
    }

    /**
     * 查询是否有试用
     */
    suspend fun queryTrail(): Response<ResultData<List<String>>> {
        val params = getCommonQueryParams()
        return executeHttpAndCallback(memberApi?.queryTrail(getHeadersMap(), params))
    }

    /**
     * 创建订单
     */
    suspend fun createOrder(
        priceId: String,
        startTrial: Boolean,
        successUrl: String,
        cancelUrl: String,
        trackerCampaign: String?,
    ): Response<ResultData<OrderBean>> {
        val params = getCommonBodyParams()
        params["priceId"] = priceId
        params["startTrial"] = startTrial
        params["successUrl"] = successUrl
        params["cancelUrl"] = cancelUrl
        trackerCampaign?.let {
            params["trackerCampaign"] = it
        }
        params["returnUrl"] = "https://immersivetranslate.com/pricing"
        params["platform"] = "android"
        return executeHttpAndCallback(memberApi?.createOrder(getHeadersMap(), params))
    }

    /**
     * 获取用户信息
     */
    suspend fun getUserInfo(): Response<ResultData<UserBean>> {
        return executeHttpAndCallback(
            memberApi?.getUser(
                getHeadersMap(), getCommonQueryParams(),
            ),
        )
    }

    /**
     * 会员 升级
     */
    suspend fun orderUpcomming(
        priceId: String,
    ): Response<ResultData<VipUpgradeBean>> {
        val params = getCommonQueryParams()
        params["priceId"] = priceId
        return executeHttpAndCallback(
            memberApi?.upcoming(getHeadersMap(), params),
        )
    }

    /**
     * 商品接口
     */
    suspend fun getUpgradeProducts(currency: String): Response<VipProductBean> {
        val params = getCommonQueryParams()
        params["group"] = "year_discount_7_year_trial_3"
        params["currency"] = currency
        val url = "${Constant.workerBaseUrl}/goods"
        return executeHttpAndCallback(memberApi?.getProducts(url, params))
    }

    /**
     * 月费升级到年费
     */
    suspend fun vipUpgrade(
        priceId: String,
        trackerCampaign: String?,
    ): Response<ResultData<UpgradeBean>> {
        val params = getCommonQueryParams()
        params["priceId"] = priceId
        trackerCampaign?.let {
            params["trackerCampaign"] = it
        }
        return executeHttpAndCallback(memberApi?.vipUpgrade(getHeadersMap(), params))
    }

    /**
     * 登录
     */
    suspend fun webLogin(
        username: String? = null,
        password: String? = null,
        loginType: String? = null,
        externalOAuthAppName: String? = null,
    ): Response<ResultData<WebLoginBean>> {
        val headerMap = getHeadersMap()
        val params: MutableMap<String, Any?> = HashMap()
        if (TextUtils.isEmpty(loginType)) {
            params["userName"] = username
            params["password"] = password
        } else {
            params["loginType"] = loginType
            params["externalOAuthAppName"] = externalOAuthAppName
        }
        return executeHttpAndCallback(memberApi?.webLogin(headerMap, params))
    }

    /**
     * 更新设备信息
     */
    suspend fun updateDeviceInfo(user: UserBean): Response<Any?> {
        val headerMap = getHeadersMap()
        val params: MutableMap<String, Any?> = HashMap()
        params["userId"] = user.uid
        params["userEmail"] = user.email
        params["platform"] = "Android"
        params["lastLoginTime"] = user.lastLoginTime
        params["deviceId"] = user.deviceId
        return executeHttpAndCallback(memberApi?.updateDeviceInfo(headerMap, params))
    }

    /**
     * 获取重置密码验证码
     */
    suspend fun getResetPwdCode(
        email: String,
    ): Response<Any?> {
        val headerMap = getHeadersMap()
        val params: MutableMap<String, Any?> = HashMap()
        params["email"] = email
        return executeHttpAndCallback(memberApi?.getPwdCode(headerMap, params))
    }


    /**
     * 重置密码验证码
     */
    suspend fun resetPassword(
        userEmail: String,
        password: String,
        resetCode: String,
    ): Response<Any?> {
        val headerMap = getHeadersMap()
        val params: MutableMap<String, Any?> = HashMap()
        params["userEmail"] = userEmail
        params["password"] = password
        params["resetCode"] = resetCode
        return executeHttpAndCallback(memberApi?.resetPassword(headerMap, params))
    }

    /**
     * 获取 激活邮箱 验证码
     */
    suspend fun getActiveEmailVerifyCode(
        email: String,
    ): Response<Any?> {
        val headerMap = getHeadersMap()
        val params: MutableMap<String, Any?> = HashMap()
        params["email"] = email
        return executeHttpAndCallback(memberApi?.getActiveEmailVerifyCode(headerMap, params))
    }

    /**
     * 用户注册
     */
    suspend fun register(
        username: String,
        password: String,
    ): Response<Any?> {
        val headerMap = getHeadersMap()
        val params: MutableMap<String, Any?> = HashMap()
        params["username"] = username
        params["email"] = username
        params["password"] = password
        params["isDevice"] = false
        return executeHttpAndCallback(memberApi?.register(headerMap, params))
    }

    /**
     * 激活邮箱
     */
    suspend fun activeEmail(
        email: String,
        verifyCode: String,
    ): Response<Any?> {
        val headerMap = getHeadersMap()
        val params: MutableMap<String, Any?> = HashMap()
        params["email"] = email
        params["activationCode"] = verifyCode
        return executeHttpAndCallback(memberApi?.activeEmail(headerMap, params))
    }

    /**
     * 使用 access token 登录
     */
    suspend fun loginWithAccessToken(
        params: MutableMap<String, Any?>,
    ): Response<ResultData<WebLoginBean>> {
        val headerMap = getHeadersMap()
        return executeHttpAndCallback(memberApi?.loginWithAccessToken(headerMap, params))
    }

    /**
     * 使用 wx token 登录
     */
    suspend fun loginWithWxToken(
        params: MutableMap<String, Any?>,
    ): Response<ResultData<WebLoginBean>> {
        val headerMap = getHeadersMap()
        return executeHttpAndCallback(memberApi?.loginWithWxToken(headerMap, params))
    }
}
