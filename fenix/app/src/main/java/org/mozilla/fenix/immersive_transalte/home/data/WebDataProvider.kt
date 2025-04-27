/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package org.mozilla.fenix.immersive_transalte.home.data

import org.mozilla.fenix.R
import org.mozilla.fenix.immersive_transalte.home.bean.FeatureItem
import org.mozilla.fenix.immersive_transalte.home.bean.WebItem

object WebDataProvider {
    private val featuredWebs = mutableListOf<FeatureItem>().apply {
        add(
            FeatureItem(
                R.mipmap.ic_web_youtube,
                R.string.web_ts_youtube,
                "https://www.youtube.com/",
            ),
        )
        add(FeatureItem(R.mipmap.ic_web_google, R.string.web_ts_google, "https://www.google.com/"))
        add(FeatureItem(R.mipmap.ic_web_chatgpt, R.string.web_ts_chatgpt, "https://chatgpt.com/"))
        add(FeatureItem(R.mipmap.ic_web_bilin, R.string.web_ts_bilin, "https://bilin.ai/"))
    }

    private val featuredWebsOfAll = mutableListOf<FeatureItem>().apply {
        add(
            FeatureItem(
                R.mipmap.ic_web_youtube,
                R.string.web_ts_youtube,
                "https://www.youtube.com/",
            ),
        )
        add(FeatureItem(R.mipmap.ic_web_google, R.string.web_ts_google, "https://www.google.com/"))
        add(FeatureItem(R.mipmap.ic_web_chatgpt, R.string.web_ts_chatgpt, "https://chatgpt.com/"))
        add(FeatureItem(R.mipmap.ic_web_bilin, R.string.web_ts_bilin, "https://bilin.ai/"))

        add(FeatureItem(R.mipmap.ic_web_reddit, R.string.web_ts_reddit, "https://www.reddit.com/"))
        add(FeatureItem(R.mipmap.ic_web_x, R.string.web_ts_x, "https://x.com/"))
        add(FeatureItem(R.mipmap.ic_web_wiki, R.string.web_ts_wiki, "https://www.wikipedia.org/"))
        add(FeatureItem(R.mipmap.ic_web_amazon, R.string.web_ts_amazon, "https://www.amazon.com/"))
    }

    private val webLinks = mutableListOf<WebItem>().apply {
        add(
            WebItem(
                WebItem.TYPE_LINKS,
                "Far From Ordinary - 2024 Year in Pictures",
                "https://s.immersivetranslate.com/assets/uploads/20241221-215417-OgDd2g.jpeg",
                "https://www.nytimes.com/interactive/2024/world/year-in-pictures.html",
            ),
        )
        add(
            WebItem(
                WebItem.TYPE_LINKS,
                "Powell's Message Brings Gloom to Stock Bulls' Party",
                "https://s.immersivetranslate.com/assets/uploads/20241221-215434-n42iPa.jpeg",
                "https://www.bloomberg.com/news/newsletters/2024-12-19/powell-s-message-brings-gloom-to-stock-bulls-party",
            ),
        )
        add(
            WebItem(
                WebItem.TYPE_LINKS,
                "China's central bank steps up currency support after Fed move",
                "https://s.immersivetranslate.com/assets/uploads/20241221-215441-D6nnHj.jpeg",
                "https://asia.nikkei.com/Business/Markets/Currencies/China-s-central-bank-steps-up-currency-support-after-Fed-move",
            ),
        )
        add(
            WebItem(
                WebItem.TYPE_LINKS,
                "The Outrage Over 100 Men Only Goes So Far",
                "https://s.immersivetranslate.com/assets/uploads/20241221-215445-Yb3bHv.jpeg",
                "https://www.theatlantic.com/ideas/archive/2024/12/lily-phillips-outrage-porn-100-men/681032/",
            ),
        )
        add(
            WebItem(
                WebItem.TYPE_LINKS,
                "How to get a free meal in China",
                "https://s.immersivetranslate.com/assets/uploads/20241221-215449-a1jHnX.jpeg",
                "https://www.economist.com/china/2024/12/19/how-to-get-a-free-meal-in-china",
            ),
        )
        add(
            WebItem(
                WebItem.TYPE_LINKS,
                "A Gathering of Ancient Stars",
                "https://s.immersivetranslate.com/assets/uploads/20241221-215558-uXAHrW.png",
                "https://www.theatlantic.com/science/archive/2024/12/day-17-2024-space-telescope-advent-calendar-gathering-ancient-stars/681027/",
            ),
        )
    }

    fun getFeaturedWebs(isAll: Boolean): MutableList<FeatureItem> {
        return if (isAll) featuredWebsOfAll else featuredWebs
    }

    fun getWebLinks(): MutableList<WebItem> {
        return webLinks
    }
}
