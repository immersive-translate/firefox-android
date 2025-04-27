/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package org.mozilla.fenix.immersive_transalte.home.data

import org.mozilla.fenix.R
import org.mozilla.fenix.immersive_transalte.home.bean.FeatureItem
import org.mozilla.fenix.immersive_transalte.home.bean.WebItem

object VideoDataProvider {
    private val featuredWebs = mutableListOf<FeatureItem>().apply {
        add(FeatureItem(R.mipmap.ic_web_youtube, R.string.web_ts_youtube, "https://www.youtube.com/"))
        add(FeatureItem(R.mipmap.ic_web_x, R.string.web_ts_google, "https://x.com/"))
        add(FeatureItem(R.mipmap.ic_web_udemy, R.string.web_ts_udemy, "https://www.udemy.com/"))
        add(FeatureItem(R.mipmap.ic_web_nebula, R.string.web_ts_nebula, "https://nebula.tv/featured"))
    }

    private val webLinks = mutableListOf<WebItem>().apply {
        add(
            WebItem(
                WebItem.TYPE_LINKS,
                "Powell Speaks After the Fed Cut Rates | The Fed Decides",
                "https://s.immersivetranslate.com/assets/uploads/3PS00D-CO273v.png",
                "https://www.youtube.com/watch?v=MLN-Otn5omc",
            ),
        )
        add(
            WebItem(
                WebItem.TYPE_LINKS,
                "Powell's Message Brings Gloom to Stock Bulls' Party",
                "https://s.immersivetranslate.com/assets/uploads/IhyLHK-Cy6xfp.png",
                "https://www.youtube.com/watch?v=UXaVNyXV_CI",
            ),
        )
        add(
            WebItem(
                WebItem.TYPE_LINKS,
                "Full interview: Donald Trump details his plans for Day 1 and beyond in the White House",
                "https://s.immersivetranslate.com/assets/uploads/a3e27l-Qza5au.png",
                "https://www.youtube.com/watch?v=b607aDHUu2I",
            ),
        )
        add(
            WebItem(
                WebItem.TYPE_LINKS,
                "Superman - Teaser Trailer Tomorrow",
                "https://s.immersivetranslate.com/assets/uploads/RxP8Rm-ukjnR9.png",
                "https://www.youtube.com/watch?v=KbE8n146umc",
            ),
        )
        add(
            WebItem(
                WebItem.TYPE_LINKS,
                "KARATE KID: LEGENDS - Official Trailer (HD)",
                "https://s.immersivetranslate.com/assets/uploads/TyWgfs-pcMwju.png",
                "https://www.youtube.com/watch?v=uPzOyzsnmio",
            ),
        )
        add(
            WebItem(
                WebItem.TYPE_LINKS,
                "Where I've been for the past year...",
                "https://s.immersivetranslate.com/assets/uploads/DVBEYS-VcHUsS.png",
                "https://www.youtube.com/watch?v=bgrwYFuNib0",
            ),
        )
        add(
            WebItem(
                WebItem.TYPE_LINKS,
                "NL MVP! The BEST MOMENTS from Shohei Ohtani's 2024 season! | 大谷翔平ハイライト",
                "https://s.immersivetranslate.com/assets/uploads/6Gkhr8-HjSaL8.png",
                "https://www.youtube.com/watch?v=3_gDAF2GzCs",
            ),
        )
        add(
            WebItem(
                WebItem.TYPE_LINKS,
                "Real Madrid (ESP) vs Pachuca (MEX) | Intercontinental Cup Final | 12/18/2024 | beIN SPORTS USA",
                "https://s.immersivetranslate.com/assets/uploads/ylHsvt-6culPB.png",
                "https://www.youtube.com/watch?v=JestHTufnVU",
            ),
        )
        add(
            WebItem(
                WebItem.TYPE_LINKS,
                "How Employees Are Coffee Badging To Avoid Full Days At The Office",
                "https://s.immersivetranslate.com/assets/uploads/37tKJG-CODXms.png",
                "https://www.youtube.com/watch?v=mJG4MdepNSA",
            ),
        )
        add(
            WebItem(
                WebItem.TYPE_LINKS,
                "The Aston Martin Valkyrie Is a $4.5 Million Insane Hypercar",
                "https://s.immersivetranslate.com/assets/uploads/G9oW2q-M7kB3D.png",
                "https://www.youtube.com/watch?v=n68z7e8YGGs",
            ),
        )
        add(
            WebItem(
                WebItem.TYPE_LINKS,
                "Every Home Alone Is Worse Than The Last",
                "https://s.immersivetranslate.com/assets/uploads/DUjf5v-fivcDe.png",
                "https://www.youtube.com/watch?v=oUcE_5Gv_YE",
            ),
        )
        add(
            WebItem(
                WebItem.TYPE_LINKS,
                "Stray Kids \"Walkin On Water\" M/V",
                "https://s.immersivetranslate.com/assets/uploads/5SFrWM-nXG7KZ.png",
                "https://www.youtube.com/watch?v=ovHoY8UBIu8",
            ),
        )
    }

    fun getFeaturedWebs(): MutableList<FeatureItem> {
        return featuredWebs
    }

    fun getWebLinks(): MutableList<WebItem> {
        return webLinks
    }
}
