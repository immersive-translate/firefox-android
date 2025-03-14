/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package org.mozilla.fenix.home.report

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.testTagsAsResourceId
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.mozilla.fenix.R
import org.mozilla.fenix.compose.annotation.LightDarkPreview
import org.mozilla.fenix.theme.FirefoxTheme

@OptIn(ExperimentalFoundationApi::class, ExperimentalComposeUiApi::class)
@Composable
fun BottomReport(
    onReport: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(0.dp, 18.dp, 0.dp, 20.dp),
        horizontalArrangement = Arrangement.Center,
    ) {
        Text(
            modifier = Modifier
                .semantics {
                    testTagsAsResourceId = true
                },
            color = Color(0xFF999999),
            text = stringResource(R.string.app_report_problem),
            fontSize = 14.sp,
            style = FirefoxTheme.typography.caption,
        )

        Text(
            modifier = Modifier
                .semantics {
                    testTagsAsResourceId = true
                }.combinedClickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null,
                    onClick = { onReport() },
                )
                .padding(3.dp, 0.dp, 0.dp, 0.dp),
            color = Color(0xFF4181F0),
            text = stringResource(R.string.app_report_contact),
            fontSize = 14.sp,
            style = FirefoxTheme.typography.caption,
        )
    }
}

@Composable
@LightDarkPreview
private fun TopLoginPreview() {
    FirefoxTheme {
        Box(modifier = Modifier.background(color = FirefoxTheme.colors.layer1)) {
            BottomReport(onReport = {})
        }
    }
}
