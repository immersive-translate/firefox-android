/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package org.mozilla.fenix.immersive_transalte.utils

import mozilla.components.support.locale.LocaleManager
import org.mozilla.fenix.FenixApplication
import java.util.Locale

object AppLangUtil {

    /**
     * 是否简体中文
     */
    fun isChineseSimplified(): Boolean {
        var locale = LocaleManager.getCurrentLocale(FenixApplication.application)
        if (locale == null) {
            locale = Locale.getDefault()
        }
        locale?.let {
            return (Locale.SIMPLIFIED_CHINESE.language.equals(it.language)
                    && (Locale.SIMPLIFIED_CHINESE.country.equals(it.country)))
                    || "Hans" == it.script
        }
        return false
    }

    /**
     * 是否繁体中文
     */
    fun isChineseTraditional(): Boolean {
        var locale = LocaleManager.getCurrentLocale(FenixApplication.application)
        if (locale == null) {
            locale = Locale.getDefault()
        }
        locale?.let {
            return (Locale.TRADITIONAL_CHINESE.language.equals(it.language) && ("TW" == it.country
                    || "HK" == it.country)) || "Hant" == it.script
        }
        return false
    }

}
