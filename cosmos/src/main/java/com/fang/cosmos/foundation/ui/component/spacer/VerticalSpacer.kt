package com.fang.cosmos.foundation.ui.component.spacer

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * 垂直 spacer
 */
@Composable
fun VerticalSpacer(space: Dp) {
    Spacer(modifier = Modifier.height(space))
}

@Composable
fun VerticalSpacer(spaceDp: Double) {
    VerticalSpacer(spaceDp.dp)
}

@Composable
fun VerticalSpacer(spaceDp: Int) {
    VerticalSpacer(spaceDp.dp)
}