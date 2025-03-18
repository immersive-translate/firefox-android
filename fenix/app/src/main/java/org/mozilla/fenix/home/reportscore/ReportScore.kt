/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package org.mozilla.fenix.home.reportscore

import android.content.Context
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.mozilla.fenix.R
import org.mozilla.fenix.compose.annotation.LightDarkPreview
import org.mozilla.fenix.ext.settings
import org.mozilla.fenix.theme.FirefoxTheme

private const val PANEL_WIDTH = 324
private const val PANEL_HEIGHT = 140
private const val LOW_SCORE = 3

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ReportScore(
    onScopeBtnClick: (isAppReport: Boolean) -> Unit,
    onCloseBtnClick: () -> Unit,
    context: Context,
) {
    var userFeedbackScope by remember { mutableStateOf(false) }
    if (userFeedbackScope) {
        return
    }

    Row(
        modifier = Modifier
            .background(color = FirefoxTheme.colors.layer1)
            .padding(0.dp, 8.dp, 0.dp, 8.dp)
            .fillMaxWidth()
            .height(PANEL_HEIGHT.dp),
        horizontalArrangement = Arrangement.Center,
    ) {
        Box(
            modifier = Modifier
                .background(color = FirefoxTheme.colors.layer1)
                .width(PANEL_WIDTH.dp)
                .height(PANEL_HEIGHT.dp),
        ) {
            Image(
                painter = painterResource(R.mipmap.img_home_report_scope_bg),
                contentDescription = null,
                modifier = Modifier
                    .width(PANEL_WIDTH.dp)
                    .height(PANEL_HEIGHT.dp),
                contentScale = ContentScale.Inside,
            )

            // val hasScore = false
            var selectScore = context.settings().userAppScope
            var hasScore by remember { mutableStateOf(selectScore > 0) }

            if (!hasScore) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .fillMaxHeight()
                        .padding(15.dp, 15.dp, 15.dp, 12.dp),
                ) {
                    Text(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(0.dp),
                        color = Color(0xFF333333),
                        text = stringResource(R.string.report_score_title),
                        fontSize = 14.sp,
                        style = FirefoxTheme.typography.caption,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                    )

                    ScopeButton(
                        onScopeBtnClick = { score ->
                            selectScore = score
                        },
                        selectScore = selectScore,
                        modifier = Modifier.padding(0.dp, 2.dp, 0.dp, 0.dp),
                    )

                    CommitButton(
                        btnClick = {
                            hasScore = selectScore > 0
                            if (selectScore > 0) {
                                context.settings().userAppScope = selectScore
                            }
                        },
                        btnText = stringResource(R.string.report_score_btn_commit),
                        modifier = Modifier.padding(0.dp, 16.dp, 0.dp, 0.dp),
                    )
                }
            } else {
                val descResId = if (selectScore <= 3)
                    R.string.report_score_low_desc
                else R.string.report_score_high_desc
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .fillMaxHeight()
                        .padding(15.dp, 25.dp, 15.dp, 12.dp),
                ) {
                    Text(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(0.dp),
                        color = Color(0xFF333333),
                        text = stringResource(descResId),
                        fontSize = 14.sp,
                        style = FirefoxTheme.typography.caption,
                        fontWeight = FontWeight.Medium,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                    )

                    CommitButton(
                        btnClick = {
                            context.settings().userFeedbackScope = true
                            userFeedbackScope = true
                            onScopeBtnClick(selectScore <= LOW_SCORE)
                        },
                        btnText = stringResource(R.string.report_score_go_now),
                        modifier = Modifier.padding(0.dp, 16.dp, 0.dp, 0.dp),
                    )
                }
            }

            Image(
                painter = painterResource(R.mipmap.img_home_report_scope_close),
                contentDescription = null,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(0.dp, 4.dp, 4.dp, 0.dp)
                    .combinedClickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null,
                        onClick = {
                            context.settings().userFeedbackScope = true
                            userFeedbackScope = true
                            onCloseBtnClick()
                        },
                    ),
            )
        }
    }
}

@Suppress("LongParameterList", "LongMethod")
@Composable
private fun CommitButton(
    btnClick: () -> Unit,
    btnText: String,
    modifier: Modifier = Modifier,
) {
    Button(
        onClick = btnClick,
        colors = ButtonDefaults.buttonColors(
            backgroundColor = Color(0xFFEC4C8C),
        ),
        shape = RoundedCornerShape(8.dp), // 设置圆角
        modifier = modifier
            .width(128.dp)
            .height(32.dp),
        contentPadding = PaddingValues(0.dp),
    ) {
        Text(
            text = btnText,
            color = Color.White,
        )
    }
}

@Composable
private fun ScopeButton(
    onScopeBtnClick: (score: Int) -> Unit,
    selectScore: Int,
    modifier: Modifier = Modifier,
) {
    var selectedStars by remember { mutableIntStateOf(selectScore) }

    Row(
        modifier = modifier.padding(0.dp, 0.dp, 0.dp, 0.dp),
        horizontalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        repeat(5) { index ->
            val starResId = if (index < selectedStars) R.mipmap.img_scope_star_selected
                            else R.mipmap.img_scope_star_unselect
            Image(
                painter = painterResource(starResId),
                contentDescription = null,
                modifier = Modifier
                    .width(30.dp)
                    .height(30.dp)
                    .clickable {
                        selectedStars = index + 1
                        onScopeBtnClick(selectedStars)
                    },
            )
        }
    }
}

@Composable
@LightDarkPreview
private fun TopLoginPreview() {
    FirefoxTheme {
        Box(modifier = Modifier.background(color = FirefoxTheme.colors.layer1)) {
            ReportScore(
                onScopeBtnClick = {},
                onCloseBtnClick = {},
                context = LocalContext.current,
            )
        }
    }
}
