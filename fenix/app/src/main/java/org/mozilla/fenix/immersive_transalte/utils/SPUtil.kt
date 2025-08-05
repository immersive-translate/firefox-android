/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package org.mozilla.fenix.immersive_transalte.utils

import android.content.Context
import androidx.core.content.edit

object SPUtil {

    private const val SP_APP_CONFIG = "app_config"
    private const val APP_VER_NOTIFY = "app_ver_notify"
    private const val GOOGLE_NEW_VERSION_SHOW = "google_new_version_show"

    fun saveAppVersionNotify(context: Context, versionCode: Long) {
        val sp = context.getSharedPreferences(SP_APP_CONFIG, Context.MODE_PRIVATE)
        sp.edit { putLong(APP_VER_NOTIFY, versionCode) }
    }

    fun getAppVersionNotify(context: Context): Long {
        val sp = context.getSharedPreferences(SP_APP_CONFIG, Context.MODE_PRIVATE)
        return sp.getLong(APP_VER_NOTIFY, 0)
    }

    fun isGoogleNewVersionShown(context: Context): Boolean {
        val sp = context.getSharedPreferences(SP_APP_CONFIG, Context.MODE_PRIVATE)
        return sp.getBoolean(GOOGLE_NEW_VERSION_SHOW, false)
    }

    fun saveGoogleNewVersionShown(context: Context) {
        val sp = context.getSharedPreferences(SP_APP_CONFIG, Context.MODE_PRIVATE)
        sp.edit { putBoolean(GOOGLE_NEW_VERSION_SHOW, true) }
    }
}
