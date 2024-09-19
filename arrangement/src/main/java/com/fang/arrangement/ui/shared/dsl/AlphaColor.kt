package com.fang.arrangement.ui.shared.dsl

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.fang.cosmos.foundation.ui.dsl.animateColor

internal interface AlphaColor {
    companion object {
        const val DEFAULT = 0.5f
    }
}

@Composable
internal fun alphaColor(
    color: Color,
    alpha: Float = AlphaColor.DEFAULT,
    isAlpha: Boolean = true,
) = animateColor(label = "AlphaColor") {
    color.copy(alpha = if (isAlpha) alpha else 1f)
}

@Composable
internal fun alphaColor(
    color: Color,
    alpha: Float = AlphaColor.DEFAULT,
    isAlpha: () -> Boolean,
) = alphaColor(color, alpha = alpha, isAlpha())
