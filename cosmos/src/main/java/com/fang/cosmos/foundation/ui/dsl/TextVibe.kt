package com.fang.cosmos.foundation.ui.dsl

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.PlatformTextStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.LineHeightStyle
import androidx.compose.ui.text.style.LineHeightStyle.Trim
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.sp

val LineHeightAlignmentProportional =
    LineHeightStyle(alignment = LineHeightStyle.Alignment.Center, trim = Trim.None)
val TextExcludeFontPadding = PlatformTextStyle(includeFontPadding = false)

val FONT_WEIGHT_NORMAL = FontWeight.W400
val FONT_WEIGHT_NORMAL_VALUE = FONT_WEIGHT_NORMAL.weight
val FONT_WEIGHT_BOLD = FontWeight.W600
val FONT_WEIGHT_BOLD_VALUE = FONT_WEIGHT_BOLD.weight

object TextVibe {

    operator fun invoke(apply: TextStyle.() -> TextStyle = { this }) =
        apply(TextStyle().adjustLineHeight())

    operator fun invoke(
        color: Color,
        fontSize: Int,
        fontWeight: FontWeight,
        apply: TextStyle.() -> TextStyle = { this },
    ) =
        apply(
            TextStyle(
                color = color,
                fontSize = fontSize.sp,
                fontWeight = fontWeight,
                lineHeight =
                    when (fontSize) {
                        14 -> 19.6
                        16 -> 25.6
                        18 -> 28.8
                        24 -> 38.4
                        32 -> 51.2
                        else -> null
                    }?.sp ?: TextUnit.Unspecified,
            )
                .adjustLineHeight()
        )

    operator fun invoke(
        color: Color,
        fontSize: Int,
        fontWeight: Int,
        apply: TextStyle.() -> TextStyle = { this },
    ) =
        invoke(
            color = color,
            fontSize = fontSize,
            fontWeight = FontWeight(fontWeight),
            apply = apply,
        )

    fun normal(color: Color, fontSize: Int, apply: TextStyle.() -> TextStyle = { this }) =
        invoke(color, fontSize, FONT_WEIGHT_NORMAL, apply)

    fun bold(color: Color, fontSize: Int, apply: TextStyle.() -> TextStyle = { this }) =
        invoke(color, fontSize, FONT_WEIGHT_BOLD, apply)
}

fun TextStyle.adjustLineHeight() =
    copy(platformStyle = TextExcludeFontPadding, lineHeightStyle = LineHeightAlignmentProportional)

fun TextStyle.color(color: Color) = copy(color = color)

fun TextStyle.fontSize(fontSize: TextUnit) = copy(fontSize = fontSize)

fun TextStyle.fontSize(fontSize: Int) = copy(fontSize = fontSize.sp)

fun TextStyle.fontWeight(fontWeight: FontWeight?) = copy(fontWeight = fontWeight)

fun TextStyle.fontWeight(fontWeight: Int?) = fontWeight(fontWeight?.let(::FontWeight))

fun TextStyle.normal() = fontWeight(FONT_WEIGHT_NORMAL)

fun TextStyle.bold() = fontWeight(FONT_WEIGHT_BOLD)

fun TextStyle.textAlign(textAlign: TextAlign) = copy(textAlign = textAlign)

fun TextStyle.textAlignCenter() = textAlign(TextAlign.Center)

fun TextStyle.textAlignEnd() = textAlign(TextAlign.End)
