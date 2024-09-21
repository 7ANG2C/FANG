package com.fang.arrangement.ui.shared.dsl

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.fang.arrangement.ui.shared.component.ArrText
import com.fang.cosmos.foundation.ui.dsl.MaterialColor
import com.fang.cosmos.foundation.ui.dsl.MaterialTypography
import com.fang.cosmos.foundation.ui.ext.color

internal interface ContentText {
    companion object {
        val style @Composable get() = MaterialTypography.bodyLarge
        val color @Composable get() = MaterialColor.secondary
    }
}

@Composable
internal fun contentAlphaColor(isAlpha: Boolean) =
    alphaColor(
        color = ContentText.color,
        isAlpha = isAlpha,
    )

@Composable
internal fun contentAlphaColor(isAlpha: () -> Boolean) = contentAlphaColor(isAlpha())

@Composable
internal fun ContentText(
    text: String,
    modifier: Modifier = Modifier,
) = ArrText(
    text = text,
    modifier = modifier,
) {
    ContentText.style.color(ContentText.color)
}

@Composable
internal fun ContentText(
    text: String,
    modifier: Modifier = Modifier,
    isAlpha: Boolean,
) = ArrText(
    text = text,
    modifier = modifier,
) {
    ContentText.style.color(contentAlphaColor(isAlpha))
}

@Composable
internal fun ContentText(
    text: String,
    modifier: Modifier = Modifier,
    isAlpha: () -> Boolean,
) = ContentText(text = text, modifier = modifier, isAlpha = isAlpha())
