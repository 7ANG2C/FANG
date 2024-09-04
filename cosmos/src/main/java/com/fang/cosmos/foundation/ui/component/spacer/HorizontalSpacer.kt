package com.fang.cosmos.foundation.ui.component.spacer

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlin.math.sign

@Composable
fun HorizontalSpacer(dp: Dp) {
    if (dp.value.sign > 0f) {
        Spacer(modifier = Modifier.width(dp))
    }
}

@Composable
fun HorizontalSpacer(dpValue: Float) = HorizontalSpacer(dpValue.dp)

@Composable
fun HorizontalSpacer(dpValue: Int) = HorizontalSpacer(dpValue.toFloat())
