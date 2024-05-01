package com.fang.cosmos.foundation.ui.component.spacer

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun HorizontalSpacer(space: Dp) {
    Spacer(modifier = Modifier.width(space))
}

@Composable
fun HorizontalSpacer(spaceDp: Double) {
    HorizontalSpacer(spaceDp.dp)
}

@Composable
fun HorizontalSpacer(spaceDp: Int) {
    HorizontalSpacer(spaceDp.dp)
}