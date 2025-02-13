package org.mozilla.fenix.home.toplogin

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
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.mozilla.fenix.compose.annotation.LightDarkPreview
import org.mozilla.fenix.theme.FirefoxTheme
import org.mozilla.fenix.R

@OptIn(ExperimentalFoundationApi::class, ExperimentalComposeUiApi::class)
@Composable
fun TopLogin(
    onLoginBtnClick: () -> Unit,
) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(0.dp, 16.dp, 0.dp, 0.dp),
        horizontalArrangement = Arrangement.Center
    ) {
        Text(
            modifier = Modifier
                .semantics {
                    testTagsAsResourceId = true
                }
                .combinedClickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null,
                    onClick = { onLoginBtnClick() },
                ),
            color = Color(0xFFEC4C8C),
            text = stringResource(R.string.app_toplogin_login),
            fontSize = 14.sp,
            style = FirefoxTheme.typography.caption,
            textDecoration = TextDecoration.Underline
        )

        Text(
            modifier = Modifier
                .semantics {
                    testTagsAsResourceId = true
                }
                .padding(6.dp, 0.dp, 0.dp, 0.dp),
            color = Color(0xFF333333),
            text = stringResource(R.string.app_toplogin_desc),
            fontSize = 14.sp,
            style = FirefoxTheme.typography.caption,
        )
    }
}

@OptIn(ExperimentalFoundationApi::class, ExperimentalComposeUiApi::class)
@Composable
@LightDarkPreview
private fun TopLoginPreview() {
    FirefoxTheme {
        Box(modifier = Modifier.background(color = FirefoxTheme.colors.layer1)) {
            TopLogin(onLoginBtnClick = {})
        }
    }
}
