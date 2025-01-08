/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package org.mozilla.fenix.home.toplinks

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.testTagsAsResourceId
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.mozilla.fenix.R
import org.mozilla.fenix.compose.PagerIndicator
import org.mozilla.fenix.compose.annotation.LightDarkPreview
import org.mozilla.fenix.home.topsites.TopSitesTestTag
import org.mozilla.fenix.theme.FirefoxTheme
import kotlin.math.ceil

private const val TOP_SITES_PER_PAGE = 8
private const val TOP_SITES_PER_ROW = 4
private const val TOP_SITES_ITEM_SIZE = 95
private const val TOP_SITES_ROW_WIDTH = TOP_SITES_PER_ROW * TOP_SITES_ITEM_SIZE
private const val TOP_SITES_FAVICON_CARD_SIZE = 60
private const val TOP_SITES_FAVICON_SIZE = 60


@OptIn(ExperimentalFoundationApi::class, ExperimentalComposeUiApi::class)
@Composable
@Suppress("LongParameterList", "LongMethod")
fun TopLinks(
    topLinks: List<TopLink>,
    onTopLinkClick: (TopLink) -> Unit,
) {
    val pageCount = ceil((topLinks.size.toDouble() / TOP_SITES_PER_PAGE)).toInt()

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .semantics {
                testTagsAsResourceId = true
            }
            .testTag(TopSitesTestTag.topSites),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        val pagerState = rememberPagerState(
            pageCount = { pageCount },
        )

        Box(
            modifier = Modifier.fillMaxWidth(),
            contentAlignment = Alignment.Center,
        ) {
            HorizontalPager(
                state = pagerState,
            ) { page ->
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    val topSitesWindows = topLinks.windowed(
                        size = TOP_SITES_PER_PAGE,
                        step = TOP_SITES_PER_PAGE,
                        partialWindows = true,
                    )[page].chunked(TOP_SITES_PER_ROW)

                    for (items in topSitesWindows) {
                        Row(modifier = Modifier.defaultMinSize(minWidth = TOP_SITES_ROW_WIDTH.dp)) {
                            items.forEachIndexed { _, topLink ->
                                TopSiteItem(
                                    topLink = topLink,
                                    onTopSiteClick = { item -> onTopLinkClick(item) },
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))
                    }
                }
            }
        }

        if (pagerState.pageCount > 1) {
            Spacer(modifier = Modifier.height(8.dp))

            PagerIndicator(
                pagerState = pagerState,
                modifier = Modifier.padding(horizontal = 16.dp),
                spacing = 4.dp,
            )
        }
    }
}

@Suppress("LongParameterList", "LongMethod")
@OptIn(ExperimentalFoundationApi::class, ExperimentalComposeUiApi::class)
@Composable
private fun TopSiteItem(
    topLink: TopLink,
    onTopSiteClick: (TopLink) -> Unit,
) {

    Box(
        modifier = Modifier
            .semantics {
                testTagsAsResourceId = true
            }
            .testTag(TopSitesTestTag.topSiteItemRoot),
    ) {
        Column(
            modifier = Modifier
                .combinedClickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null,
                    onClick = { onTopSiteClick(topLink) },
                )
                .width(TOP_SITES_ITEM_SIZE.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Spacer(modifier = Modifier.height(4.dp))

            if (!topLink.isMore) {
                TopSiteFaviconCard(
                    topLink = topLink
                )
            } else {
                TopSiteMoreCard(
                    topLink = topLink
                )
            }

            /*TopSiteFaviconCard(
                topLink = topLink,
            )*/

            Spacer(modifier = Modifier.height(6.dp))

            Row(
                modifier = Modifier.width(TOP_SITES_ITEM_SIZE.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically,
            ) {

                Text(
                    modifier = Modifier
                        .semantics {
                            testTagsAsResourceId = true
                        }
                        .testTag(TopSitesTestTag.topSiteTitle)
                        .padding(start = 2.dp, end = 2.dp),
                    text = stringResource(topLink.title),
                    color = if (!topLink.isMore) FirefoxTheme.colors.textPrimary
                    else Color(0xFF999999),
                    fontSize = 14.sp,
                    overflow = TextOverflow.Ellipsis,
                    maxLines = 2,
                    style = FirefoxTheme.typography.caption,
                )
            }
        }

    }
}

@Composable
private fun TopSiteFaviconCard(
    topLink: TopLink,
) {
    Card(
        modifier = Modifier.size(TOP_SITES_FAVICON_CARD_SIZE.dp),
        shape = RoundedCornerShape((TOP_SITES_FAVICON_CARD_SIZE / 2).dp),
        elevation = 6.dp,
    ) {
        Box(contentAlignment = Alignment.Center) {
            Surface(
                modifier = Modifier.size(TOP_SITES_FAVICON_SIZE.dp),
                //shape = RoundedCornerShape(4.dp),
            ) {
                Image(
                    painter = painterResource(topLink.iconId),
                    contentDescription = null,
                    modifier = Modifier.size(TOP_SITES_FAVICON_SIZE.dp),
                    contentScale = ContentScale.Crop,
                )
            }
        }
    }
}

@Suppress("LongParameterList", "LongMethod")
@OptIn(ExperimentalComposeUiApi::class)
@Composable
private fun TopSiteMoreCard(
    topLink: TopLink,
) {
    Card(
        modifier = Modifier.size(TOP_SITES_FAVICON_CARD_SIZE .dp),
        shape = RoundedCornerShape((TOP_SITES_FAVICON_CARD_SIZE / 2).dp),
        elevation = 6.dp,
        backgroundColor = Color(0xFFF4F4F4),
    ) {
        Box(contentAlignment = Alignment.Center) {
            Text(
                modifier = Modifier
                    .semantics { testTagsAsResourceId = true }
                    .testTag(TopSitesTestTag.topSiteTitle),
                textAlign = TextAlign.Center,
                text = stringResource(topLink.content),
                fontSize = 13.sp,
                color = Color(0xFFCCCCCC),
                overflow = TextOverflow.Ellipsis,
                maxLines = 2,
                style = FirefoxTheme.typography.caption,
            )
        }
    }
}

@Composable
@LightDarkPreview
private fun TopSitesPreview() {
    FirefoxTheme {
        Box(modifier = Modifier.background(color = FirefoxTheme.colors.layer1)) {
            TopLinks(
                topLinks = mutableListOf<TopLink>().apply {
                    for (index in 0 until 4) {
                        add(
                            TopLink(
                                id = index.toLong(),
                                iconId = R.drawable.ic_baidu,
                                title = R.string.app_name,
                                url = "mozilla.com",
                                isMore = false,
                            ),
                        )
                    }

                    add(
                        TopLink(
                            id = 4,
                            iconId = R.drawable.ic_baidu,
                            title = R.string.app_name,
                            content = R.string.home_top_link_ts_more_content,
                            url = "mozilla.com",
                            isMore =  true
                        ),
                    )
                },
                onTopLinkClick = {},
            )
        }
    }
}
