/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package org.mozilla.fenix.immersive_transalte.net.service

import com.google.gson.JsonObject
import org.mozilla.fenix.FenixApplication
import org.mozilla.fenix.ext.components
import org.mozilla.fenix.immersive_transalte.Constant
import org.mozilla.fenix.immersive_transalte.base.http.BaseService
import org.mozilla.fenix.immersive_transalte.base.http.HttpClient
import org.mozilla.fenix.immersive_transalte.base.http.Response
import org.mozilla.fenix.immersive_transalte.bean.OrderBean
import org.mozilla.fenix.immersive_transalte.bean.ResultData
import org.mozilla.fenix.immersive_transalte.bean.UpgradeBean
import org.mozilla.fenix.immersive_transalte.bean.VipUpgradeBean
import org.mozilla.fenix.immersive_transalte.bean.UserBean
import org.mozilla.fenix.immersive_transalte.bean.VipProductBean
import org.mozilla.fenix.immersive_transalte.net.api.MemberApi

object MemberService : BaseService() {

    init {
        HttpClient.baseApiUrl = Constant.apiBaseUrl
    }

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
    ): Response<ResultData<OrderBean>> {
        val params = getCommonBodyParams()
        params["priceId"] = priceId
        params["startTrial"] = startTrial
        params["successUrl"] = successUrl
        params["cancelUrl"] = cancelUrl
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
    ): Response<ResultData<UpgradeBean>> {
        val params = getCommonQueryParams()
        params["priceId"] = priceId
        return executeHttpAndCallback(memberApi?.vipUpgrade(getHeadersMap(), params))
    }
}
