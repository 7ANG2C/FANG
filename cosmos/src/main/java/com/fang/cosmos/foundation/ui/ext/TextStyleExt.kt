package com.fang.cosmos.foundation.ui.ext

import androidx.compose.material3.ColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import com.fang.cosmos.foundation.ui.dsl.MaterialColor

@Composable
fun TextStyle.color(color: ColorScheme.() -> Color) = color(color = color(MaterialColor))

fun TextStyle.color(color: Color) = copy(color = color)

fun TextStyle.textAlign(textAlign: TextAlign) = copy(textAlign = textAlign)
