/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package com.immersivetranslate.browser.wxapi

import android.content.Context
import android.content.Intent
import com.tencent.mm.opensdk.modelmsg.SendAuth
import com.tencent.mm.opensdk.openapi.IWXAPI
import com.tencent.mm.opensdk.openapi.IWXAPIEventHandler
import com.tencent.mm.opensdk.openapi.WXAPIFactory

object WxApi {
    private const val APP_ID = "wxc05b7ce50086c55c"
    private lateinit var wxApi: IWXAPI

    fun init(ctx: Context) {
        wxApi = WXAPIFactory.createWXAPI(ctx, APP_ID, true)
        wxApi.registerApp(APP_ID)
    }

    fun isInstall(): Boolean {
        return wxApi.isWXAppInstalled
    }

    fun requestLogin() {
        val req = SendAuth.Req().apply {
            scope = "snsapi_userinfo"
            state = "random_state_string"
        }
        wxApi.sendReq(req)
    }

    fun handlerIntent(intent: Intent, handler: IWXAPIEventHandler) {
        wxApi.handleIntent(intent, handler)
    }
}
