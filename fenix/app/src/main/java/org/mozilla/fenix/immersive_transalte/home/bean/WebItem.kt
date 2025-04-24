/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package org.mozilla.fenix.immersive_transalte.home.bean

class WebItem(
    val type: Int,
    val title: String? = null,
    val imageUrl: String? = null,
    val url: String? = null,
) {
    companion object {
        var TYPE_TRANSLATE: Int = 0
        var TYPE_FEATURED_WEBS: Int = 1
        var TYPE_LINKS: Int = 2
    }
}
