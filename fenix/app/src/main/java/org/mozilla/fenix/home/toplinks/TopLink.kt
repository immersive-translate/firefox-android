/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package org.mozilla.fenix.home.toplinks

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes

data class TopLink(
    val id: Long?,
    @DrawableRes val iconId: Int = 0,
    @StringRes val title: Int = 0,
    @StringRes val content: Int = 0,

    val iconUrl: String = "",
    val linkUrl: String = "",
    val title_zh: String = "",
    val title_tr: String = "",
    val title_en: String = "",
    val title_ko: String = "",

    val isMore: Boolean = false
)
