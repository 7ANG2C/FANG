package com.fang.arrangement.ui.shared.component.button.foundation

import androidx.compose.material3.ColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.fang.cosmos.foundation.Invoke

@Composable
internal fun FilledButton(
    modifier: Modifier = Modifier,
    text: String,
    bgColor: @Composable ColorScheme.() -> Color,
    textColor: @Composable ColorScheme.() -> Color,
    onClick: Invoke,
) = BaseButton(
    modifier = modifier,
    text = text,
    bgColor = bgColor,
    borderColor = null,
    textColor = textColor,
    onClick = onClick,
)
