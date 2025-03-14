/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package org.mozilla.fenix.immersive_transalte.net.service

import android.graphics.Bitmap
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import org.json.JSONObject
import org.mozilla.fenix.immersive_transalte.base.http.BaseService
import org.mozilla.fenix.immersive_transalte.base.http.HttpClient
import org.mozilla.fenix.immersive_transalte.base.http.Response
import org.mozilla.fenix.immersive_transalte.bean.AppConfigBean
import org.mozilla.fenix.immersive_transalte.bean.HomePageBean
import org.mozilla.fenix.immersive_transalte.bean.ImageUploadBean
import org.mozilla.fenix.immersive_transalte.bean.OnBoardingTranslateBean
import org.mozilla.fenix.immersive_transalte.bean.ResultData
import org.mozilla.fenix.immersive_transalte.net.api.HomePageApi
import java.io.File
import java.io.FileOutputStream

object HomePageService : BaseService() {
    private val homepageApi: HomePageApi? by lazy { HttpClient.retrofit?.create(HomePageApi::class.java) }

    /**
     * app 首页 运营坑位链接
     */
    suspend fun fetchHomeTopLinks(): Response<ResultData<HomePageBean>> {
        val params = getCommonQueryParams()
        return executeHttpAndCallback(homepageApi?.fetchTopLinks(params))
    }

    /**
     * app config 接口
     */
    suspend fun fetchAppConfig(): Response<ResultData<AppConfigBean>> {
        val params = getCommonQueryParams()
        return executeHttpAndCallback(homepageApi?.fetchAppConfig(params))
    }

    /**
     * app onboarding 翻译接口
     */
    suspend fun fetchOnBoardingTranslations(language: String):
            Response<ResultData<List<OnBoardingTranslateBean>>> {
        val params = getCommonQueryParams()
        val queryMap = mutableMapOf<String, Any?>()
        queryMap["language"] = language
        return executeHttpAndCallback(homepageApi?.fetchOnBoardingTranslations(params, queryMap))
    }

    suspend fun uploadImage(bitmap: Bitmap): Response<ResultData<ImageUploadBean>>? {
        try {
            val params = getCommonQueryParams()
            val file = File.createTempFile("img_" + System.currentTimeMillis(), ".jpg")
            val outputStream = FileOutputStream(file)
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream) // 90% 质量
            outputStream.flush()
            outputStream.close()

            val requestBody = file.asRequestBody("image/jpeg".toMediaTypeOrNull()) // 文件体
            val multipartBody = MultipartBody.Part.createFormData("file", file.name, requestBody)

            return executeHttpAndCallback(homepageApi?.uploadImage(params, multipartBody))
        } catch (_: Exception) {
        }
        return null
    }

    suspend fun reportProblem(
        feedType: String,
        reason: String,
        contactInfo: String,
        urls: List<String>?,
    ): Response<ResultData<Any>> {
        val params = getCommonQueryParams()
        val fieldMap = mutableMapOf<String, Any?>()
        fieldMap["feedType"] = feedType
        fieldMap["reason"] = reason
        fieldMap["contactInfo"] = contactInfo

        urls?.let {
            if(it.isNotEmpty()) {
                val metaData = JSONObject()
                metaData.put("objectKeyList", it)
                fieldMap["metaData"] = metaData
            }
        }

        return executeHttpAndCallback(homepageApi?.reportProblem(params, fieldMap))
    }
}
