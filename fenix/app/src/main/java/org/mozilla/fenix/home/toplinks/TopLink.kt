/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package org.mozilla.fenix.home.toplinks

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes

data class TopLink(
    val id: Long?,
    @DrawableRes val iconId: Int,
    @StringRes val title: Int,
    @StringRes val content: Int = 0,
    val url: String? = null,
    val isMore: Boolean = false
)
