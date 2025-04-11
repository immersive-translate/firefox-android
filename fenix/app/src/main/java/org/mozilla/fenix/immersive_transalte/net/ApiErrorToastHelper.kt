/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package org.mozilla.fenix.immersive_transalte.net

import android.os.Handler
import android.os.Looper
import org.mozilla.fenix.FenixApplication
import org.mozilla.fenix.R
import org.mozilla.fenix.immersive_transalte.base.http.Response
import org.mozilla.fenix.immersive_transalte.utils.ToastUtil

sealed class ApiModule(private val modelName: String) {
    fun errorToast(
        errorCode: Int,
        defaultErrMsg: String? = null,
    ) {
        ApiErrorMappingHelper.errorToast(modelName, errorCode, defaultErrMsg)
    }

    fun errorToast(result: Response<*>) {
        errorToast(result.errorCode, result.msg)
    }
}

object ApiErrorMappingHelper {
    private const val WEB_LOGIN = "web_login"
    private const val REGISTER = "register"
    private const val AUTH_CALLBACK = "auth_callback"
    private const val FIND_PASS_CODE = "find_pass_code"
    private const val RESET_PWD = "reset_pwd"
    private const val ACCOUNT_ACTIVE = "account_active"
    private const val REACTIVE_CODE = "reactive_code"

    data object WebLogin : ApiModule(WEB_LOGIN)
    data object Register : ApiModule(REGISTER)
    data object AuthCallback : ApiModule(AUTH_CALLBACK)
    data object FindPassCode : ApiModule(FIND_PASS_CODE)
    data object ResetPwd : ApiModule(RESET_PWD)
    data object AccountActive : ApiModule(ACCOUNT_ACTIVE)
    data object ReActiveCode : ApiModule(REACTIVE_CODE)

    private val handler = Handler(Looper.getMainLooper())
    private val errorCodeMapping = mutableMapOf<String, MutableMap<Int, Int>>()

    init {
        errorCodeMapping[WEB_LOGIN] = mutableMapOf<Int, Int>().apply {
            put(1000, R.string.error_account_login_1000)
            put(1001, R.string.error_account_login_1001)
            put(1002, R.string.error_account_login_1002)
            put(1003, R.string.error_account_login_1003)
            put(1004, R.string.error_account_login_1004)
            put(1005, R.string.error_account_login_1005)
            put(1006, R.string.error_account_login_1006)
            put(1007, R.string.error_account_login_1007)
            put(1008, R.string.error_account_login_1008)
            put(1009, R.string.error_account_login_1009)
            put(1010, R.string.error_account_login_1010)
            put(1011, R.string.error_account_login_1011)
            put(1014, R.string.error_account_login_1014)
            put(1015, R.string.error_account_login_1015)
        }

        errorCodeMapping[REGISTER] = mutableMapOf<Int, Int>().apply {
            put(1022, R.string.error_register_1022)
            put(1023, R.string.error_register_1023)
            put(1024, R.string.error_register_1024)
            put(1003, R.string.error_register_1003)
            put(1004, R.string.error_register_1004)
            put(1006, R.string.error_register_1006)
        }

        errorCodeMapping[AUTH_CALLBACK] = mutableMapOf<Int, Int>().apply {
            put(1007, R.string.error_auth_callback_1007)
        }

        errorCodeMapping[FIND_PASS_CODE] = mutableMapOf<Int, Int>().apply {
            put(1015, R.string.error_find_pwd_verify_1015)
            put(1011, R.string.error_find_pwd_verify_1011)
            put(1009, R.string.error_find_pwd_verify_1009)
        }

        errorCodeMapping[RESET_PWD] = mutableMapOf<Int, Int>().apply {
            put(1002, R.string.error_reset_password_1002)
            put(1023, R.string.error_reset_password_1023)
            put(1024, R.string.error_reset_password_1024)
            put(1025, R.string.error_reset_password_1025)
            put(1026, R.string.error_reset_password_1026)
            put(1027, R.string.error_reset_password_1027)
            put(1029, R.string.error_reset_password_1029)
        }

        errorCodeMapping[ACCOUNT_ACTIVE] = mutableMapOf<Int, Int>().apply {
            put(1030, R.string.error_account_activate_1030)
            put(1031, R.string.error_account_activate_1031)
            put(1032, R.string.error_account_activate_1032)
            put(1028, R.string.error_account_activate_1028)
            put(1033, R.string.error_account_activate_1033)
        }

        errorCodeMapping[REACTIVE_CODE] = mutableMapOf<Int, Int>().apply {
            put(1029, R.string.error_re_active_code_1029)
            put(1028, R.string.error_re_active_code_1028)
        }
    }

    /**
     * 获取对应模块，对应错误码的错误信息
     */
    fun getErrorMessage(
        modelName: String,
        errorCode: Int,
        defaultErrMsg: String? = null,
    ): String? {
        val errorCodeMap = errorCodeMapping[modelName]
        errorCodeMap?.let {
            if (it.containsKey(errorCode)) {
                return FenixApplication.application.getString(it[errorCode]!!)
            }
        }
        return defaultErrMsg
    }

    /**
     * 获取对应模块，对应错误码的错误信息
     */
    fun errorToast(
        modelName: String,
        errorCode: Int,
        defaultErrMsg: String? = null,
    ) {
        val errorMessage = getErrorMessage(modelName, errorCode, defaultErrMsg)
        errorMessage?.let {
            val ctx = FenixApplication.application
            handler.post { ToastUtil.toast(ctx, it) }
        }
    }

}

