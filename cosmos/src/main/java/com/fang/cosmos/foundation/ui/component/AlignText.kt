package com.fang.cosmos.foundation.ui.component

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.PlatformTextStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.TextUnit
import com.fang.cosmos.foundation.ui.ext.textDp
import kotlin.math.sign

/**
 * Matched line height with text no material theme support.
 */
@Composable
fun AlignText(
    text: String,
    modifier: Modifier = Modifier,
    style: TextStyle,
) = Text(
    text = text,
    modifier = modifier,
    style =
        style.copy(
            lineHeight =
                style.lineHeight.takeIf {
                    it != TextUnit.Unspecified
                } ?: style.fontSize,
            platformStyle = PlatformTextStyle(includeFontPadding = true),
        ),
)

/**
 * Matched line height with text no material theme support.
 */
@Composable
fun AlignText(
    text: String,
    modifier: Modifier = Modifier,
    color: Color = Color.Unspecified,
    size: Float? = null,
    weight: Int? = null,
) = AlignText(
    text = text,
    modifier = modifier,
    style =
        TextStyle(
            color = color,
            fontSize = size?.takeIf { it.sign > 0 }?.textDp ?: TextUnit.Unspecified,
            fontWeight = weight?.let { FontWeight(it) },
        ),
)

/**
 * Matched line height with text no material theme support.
 */
@Composable
fun AlignText(
    text: String,
    modifier: Modifier = Modifier,
    color: Color = Color.Unspecified,
    size: Int? = null,
    weight: Int? = null,
) = AlignText(
    text = text,
    modifier = modifier,
    color = color,
    size = size?.toFloat(),
    weight = weight,
)
