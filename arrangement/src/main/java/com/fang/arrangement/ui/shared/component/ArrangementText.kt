package com.fang.arrangement.ui.shared.component

import androidx.compose.material3.ColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import com.fang.cosmos.foundation.ui.component.AlignText
import com.fang.cosmos.foundation.ui.dsl.MaterialColor

@Composable
internal fun ArrText(
    text: String,
    modifier: Modifier = Modifier,
    style: @Composable ColorScheme.() -> TextStyle,
) = AlignText(text = text, modifier = modifier, style = style(MaterialColor))
