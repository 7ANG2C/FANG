package com.fang.cosmos.foundation.ui.component.spacer

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlin.math.sign

@Composable
fun VerticalSpacer(dp: Dp) {
    if (dp.value.sign > 0f) {
        Spacer(modifier = Modifier.height(dp))
    }
}

@Composable
fun VerticalSpacer(dpValue: Float) = VerticalSpacer(dpValue.dp)

@Composable
fun VerticalSpacer(dpValue: Int) = VerticalSpacer(dpValue.toFloat())
