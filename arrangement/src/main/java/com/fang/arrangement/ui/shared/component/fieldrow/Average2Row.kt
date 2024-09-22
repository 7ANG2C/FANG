package com.fang.arrangement.ui.shared.component.fieldrow

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Row
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.fang.cosmos.foundation.ui.component.HorizontalSpacer

@Composable
internal fun Average2Row(
    modifier: Modifier,
    first: @Composable BoxScope.() -> Unit,
    second: @Composable BoxScope.() -> Unit,
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(modifier = Modifier.weight(1f), content = first)
        HorizontalSpacer(4)
        Box(modifier = Modifier.weight(1f), content = second)
    }
}
