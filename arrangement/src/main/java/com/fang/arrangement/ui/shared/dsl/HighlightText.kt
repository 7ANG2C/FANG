package com.fang.arrangement.ui.shared.dsl

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.fang.arrangement.ui.shared.component.ArrText
import com.fang.cosmos.foundation.ui.dsl.MaterialColor
import com.fang.cosmos.foundation.ui.dsl.MaterialTypography
import com.fang.cosmos.foundation.ui.ext.color
import com.fang.cosmos.foundation.ui.ext.textDp

internal interface HighlightText {
    companion object {
        val style @Composable get() = MaterialTypography.titleMedium.copy(
            fontSize = 16.textDp,
            lineHeight = 24.textDp,
        )
        val color @Composable get() = MaterialColor.tertiary
    }
}

@Composable
internal fun highlightAlphaColor(isAlpha: Boolean) =
    alphaColor(
        color = HighlightText.color,
        isAlpha = isAlpha,
    )

@Composable
internal fun highlightAlphaColor(isAlpha: () -> Boolean) = highlightAlphaColor(isAlpha())

@Composable
internal fun HighlightText(
    text: String,
    modifier: Modifier = Modifier,
) = ArrText(
    text = text,
    modifier = modifier,
) {
    HighlightText.style.color(HighlightText.color)
}

@Composable
internal fun HighlightText(
    text: String,
    modifier: Modifier = Modifier,
    isAlpha: Boolean,
) = ArrText(
    text = text,
    modifier = modifier,
) {
    HighlightText.style.color(highlightAlphaColor(isAlpha))
}

@Composable
internal fun HighlightText(
    text: String,
    modifier: Modifier = Modifier,
    isAlpha: () -> Boolean,
) = HighlightText(text = text, modifier = modifier, isAlpha = isAlpha())
