/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package org.mozilla.fenix.immersive_transalte

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import mozilla.components.feature.app.links.AppLinksWhiteListHolder
import org.mozilla.fenix.immersive_transalte.net.service.HomePageService

object AppConfigProvider {

    fun fetchAppConfig() {
        MainScope().launch(Dispatchers.Main) {
            val hostWhitList = HomePageService.fetchAppConfig().data?.data?.appHostWhiteList
            hostWhitList?.let {
                AppLinksWhiteListHolder.updateWhiteList(it)
            }
        }
    }

}
