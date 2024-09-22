package com.fang.arrangement.ui.shared.component.button.composition

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.fang.cosmos.foundation.ui.component.HorizontalSpacer

@Composable
internal fun BaseButtonSets(
    modifier: Modifier,
    neutral: @Composable (() -> Unit)?,
    negative: @Composable (() -> Unit)?,
    positive: @Composable (() -> Unit)?,
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        neutral?.invoke()
        Spacer(modifier = Modifier.weight(1f))
        negative?.let {
            it()
            HorizontalSpacer(12)
        }
        positive?.invoke()
    }
}
