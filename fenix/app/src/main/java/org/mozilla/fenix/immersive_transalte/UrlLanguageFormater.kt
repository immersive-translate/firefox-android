/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package org.mozilla.fenix.immersive_transalte

import android.net.Uri
import mozilla.components.support.ktx.kotlin.isUrl
import org.mozilla.fenix.components.Components

object UrlLanguageFormater {

    fun handleUrl(components: Components, url: String?): String {
        if (url.isNullOrEmpty()) {
            return ""
        }

        val lang = components.settings.defaultTsLanguage
        if (lang.isEmpty() || !url.isUrl()) {
            return url
        }

        if (components.settings.isSetDefaultLanguage) {
            return url
        }
        components.settings.isSetDefaultLanguage = true

        var webUrl = url
        val key = "imt_set_targetLanguage"
        var uri = Uri.parse(webUrl)

        try {
            if (uri.getQueryParameter("key").isNullOrEmpty()) {
                uri = uri.buildUpon().appendQueryParameter(key, lang).build()
                webUrl = uri.toString()
            }
        } catch (_: Exception) {
        }

        return webUrl ?: ""
    }

}
