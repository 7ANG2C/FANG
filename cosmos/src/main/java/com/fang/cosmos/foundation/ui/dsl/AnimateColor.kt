package com.fang.cosmos.foundation.ui.dsl

import androidx.compose.animation.animateColorAsState
import androidx.compose.material3.ColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

@Composable
fun animateColor(
    label: String,
    color: @Composable ColorScheme.() -> Color,
) = animateColorAsState(targetValue = color(MaterialColor), label = label).value
