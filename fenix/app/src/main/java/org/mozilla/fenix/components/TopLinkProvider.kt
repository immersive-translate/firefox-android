/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package org.mozilla.fenix.components

import org.mozilla.fenix.R
import org.mozilla.fenix.home.toplinks.TopLink

object TopLinkProvider {
    val topLinks = mutableListOf<TopLink>().apply {
        var index = 0L
        add(
            TopLink(
                id = index++,
                iconId = R.mipmap.img_onboarding_second_page_web,
                title = R.string.home_top_link_ts_web,
                url = "http://www.baidu.com?word=1",
            ),
        )
        add(
            TopLink(
                id = index++,
                iconId = R.mipmap.img_onboarding_second_page_video,
                title = R.string.home_top_link_ts_video,
                url = "http://www.baidu.com?word=1",
            ),
        )
        add(
            TopLink(
                id = index++,
                iconId = R.mipmap.img_onboarding_second_page_doc,
                title = R.string.home_top_link_ts_doc,
                url = "http://www.baidu.com?word=1",
            ),
        )
        add(
            TopLink(
                id = index++,
                iconId = R.mipmap.img_ts_comics,
                title = R.string.home_top_link_ts_comics,
                url = "http://www.baidu.com?word=1",
            ),
        )

        /*more*/
        add(
            TopLink(
                id = index,
                iconId = 0,
                title = R.string.home_top_link_ts_more,
                content = R.string.home_top_link_ts_more_content,
                isMore = true,
            ),
        )

    }
}
