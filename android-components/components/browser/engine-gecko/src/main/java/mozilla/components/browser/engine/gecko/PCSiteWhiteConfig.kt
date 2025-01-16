/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */
package mozilla.components.browser.engine.gecko

import android.net.Uri
import android.text.TextUtils


object PCSiteWhiteConfig {

    var isChangeViewPort = false
    var overrideUserAgentString: String =
        "Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:109.0) Gecko/20100101 Firefox/116.0"
    private val pcSites = mutableListOf<String>()

    fun addPCSite(siteHost: String) {
        if (!pcSites.contains(siteHost)) {
            pcSites.add(siteHost)
        }
    }

    fun isPCSite(siteUrl: String?): Boolean {
        siteUrl?.let { url ->
            Uri.parse(url).host?.let { host ->
                pcSites.forEach {
                    if (host.contains(it) ||
                        TextUtils.equals(host, it)
                    ) {
                        return true
                    }
                }
            }
        }
        return false
    }

}
