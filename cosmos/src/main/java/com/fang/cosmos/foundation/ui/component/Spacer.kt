package com.fang.cosmos.foundation.ui.component

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
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
