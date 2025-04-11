/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package com.immersivetranslate.browser.wxapi

import android.app.Activity
import android.os.Bundle
import com.tencent.mm.opensdk.modelbase.BaseReq
import com.tencent.mm.opensdk.modelbase.BaseResp
import com.tencent.mm.opensdk.modelmsg.SendAuth
import com.tencent.mm.opensdk.openapi.IWXAPIEventHandler
import org.mozilla.fenix.immersive_transalte.utils.SimpleEventBus

class WXEntryActivity : Activity(), IWXAPIEventHandler {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WxApi.handlerIntent(intent, this)
    }

    override fun onResp(resp: BaseResp?) {
        finish()
        if (resp is SendAuth.Resp) {
            val code = resp.code
            SimpleEventBus.postEvent(SimpleEventBus.EVENT_WX_LOGIN_GET_TOKEN, code)
        }
    }

    override fun onReq(req: BaseReq?) {
    }
}
