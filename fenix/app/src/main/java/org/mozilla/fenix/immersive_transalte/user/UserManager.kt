/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package org.mozilla.fenix.immersive_transalte.user

import android.content.Context
import com.google.gson.Gson
import com.google.gson.JsonObject

object UserManager {

    @JvmStatic
    private val SP_KEY = "sp_data"
    @JvmStatic
    private val USER_INFO_KEY = "user_info"

    private val gson = Gson()

    private var payPendingTime = 0L
    private var payPending = false

    fun setPayPending(pending: Boolean) {
        this.payPending = pending
        if (pending) {
            this.payPendingTime = System.currentTimeMillis()
        }
    }

    fun isPayPending(): Boolean {
        return payPending && (System.currentTimeMillis() - payPendingTime) / 1000 < 60 * 10
    }

    fun saveUser(context: Context, json: String) {
        val sp = context.getSharedPreferences(SP_KEY, Context.MODE_PRIVATE)
        sp.edit().putString(USER_INFO_KEY, json).apply()
    }

    fun clearUser(context: Context) {
        val sp = context.getSharedPreferences(SP_KEY, Context.MODE_PRIVATE)
        sp.edit().remove(USER_INFO_KEY).apply()
    }

    /**
     * 是否是登录态
     */
    fun isLogin(context: Context): Boolean {
        val sp = context.getSharedPreferences(SP_KEY, Context.MODE_PRIVATE)
        val json = sp.getString(USER_INFO_KEY, "")
        if (json.isNullOrEmpty()) {
            return false
        }
        try {
            val userObject = gson.fromJson(json, JsonObject::class.java)
            userObject?.let {
                return it.get("token")?.asString != null
            }
        } finally {
        }
        return false
    }

    /**
     * 获取 用户 token
     */
    fun getUserToken(context: Context): String? {
        val sp = context.getSharedPreferences(SP_KEY, Context.MODE_PRIVATE)
        val json = sp.getString(USER_INFO_KEY, "")
        if (json.isNullOrEmpty()) {
            return ""
        }
        try {
            val userObject = gson.fromJson(json, JsonObject::class.java)
            userObject?.let {
                return it.get("token")?.asString
            }
        } finally {
        }
        return ""
    }
}
