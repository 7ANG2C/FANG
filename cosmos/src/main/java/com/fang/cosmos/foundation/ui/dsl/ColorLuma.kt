package com.fang.cosmos.foundation.ui.dsl

import androidx.annotation.ColorInt
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import android.graphics.Color as GraphicsColor

/**
 * @see <a href="https://en.wikipedia.org/wiki/Luma_%28video%29">Luma</a>
 */
fun isColorDark(
    @ColorInt color: Int,
): Boolean {
    val r = 0.299 * GraphicsColor.red(color)
    val g = 0.587 * GraphicsColor.green(color)
    val b = 0.114 * GraphicsColor.blue(color)
    return 1 - (r + g + b) / 255 >= 0.5
}

fun isColorDark(color: Color) = isColorDark(color.toArgb())

fun isColorLight(
    @ColorInt color: Int,
) = !isColorDark(color)

fun isColorLight(color: Color) = !isColorDark(color)
