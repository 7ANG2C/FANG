package com.fang.cosmos.foundation.ui.ext

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.TextUnit

fun AnnotatedString.Builder.appendStyle(
    text: String,
    color: Color = Color.Unspecified,
    fontSize: TextUnit = TextUnit.Unspecified,
    fontWeight: FontWeight? = null,
) = withStyle(
    SpanStyle(color = color, fontSize = fontSize, fontWeight = fontWeight)
) { append(text) }
