/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package org.mozilla.fenix.components

import android.content.Context
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import org.mozilla.fenix.components.appstate.AppAction
import org.mozilla.fenix.ext.components
import org.mozilla.fenix.home.toplinks.TopLink
import org.mozilla.fenix.immersive_transalte.net.service.HomePageService

object TopLinkProvider {
    var topLinks = mutableListOf<TopLink>().apply {
        /*var index = 0L
        add(
            TopLink(
                id = index++,
                iconId = R.mipmap.img_onboarding_second_page_web,
                title = R.string.home_top_link_ts_web,
                url = "https://browser.immersivetranslate.com/web",
            ),
        )
        add(
            TopLink(
                id = index++,
                iconId = R.mipmap.img_onboarding_second_page_video,
                title = R.string.home_top_link_ts_video,
                url = "https://browser.immersivetranslate.com/video",
            ),
        )
        add(
            TopLink(
                id = index++,
                iconId = R.mipmap.img_onboarding_second_page_doc,
                title = R.string.home_top_link_ts_doc,
                //url = "https://browser.immersivetranslate.com/novel",
                url = "https://app.immersivetranslate.com/",
            ),
        )
        add(
            TopLink(
                id = index++,
                iconId = R.mipmap.img_ts_comics,
                title = R.string.home_top_link_ts_comics,
                url = "https://browser.immersivetranslate.com/manga",
            ),
        )
        add(
            TopLink(
                id = index,
                iconId = R.mipmap.img_ts_rednote,
                title = R.string.home_top_link_ts_rednote,
                url = "https://browser.immersivetranslate.com/xiaohongshu",
            ),
        )*/

        /*more*/
        /*add(
            TopLink(
                id = index,
                iconId = 0,
                title = R.string.home_top_link_ts_more,
                content = R.string.home_top_link_ts_more_content,
                isMore = true,
            ),
        )*/

    }

    fun fetchTopLinks(context: Context) {
        MainScope().launch(Dispatchers.IO) {
            val topLinkData = HomePageService.fetchHomeTopLinks().data?.data
            topLinkData?.topLinks?.let {
                val tls = mutableListOf<TopLink>()
                it.forEach { topLink ->
                    val tl = TopLink(
                        id = topLink.id,
                        iconUrl = topLink.iconUrl, linkUrl = topLink.linkUrl,
                        title_zh = topLink.title_zh, title_en = topLink.title_en,
                        title_tr = topLink.title_tr, title_ko = topLink.title_ko,
                    )
                    tls.add(tl)
                }

                context.components.appStore.dispatch(
                    AppAction.TopLinksChange(tls),
                )

                topLinks = tls
            }
        }
    }

}
