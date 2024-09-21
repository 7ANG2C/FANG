package com.fang.arrangement.ui.shared.component.button.foundation

import androidx.compose.material3.ColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.fang.cosmos.foundation.Invoke

@Composable
internal fun OutlineButton(
    modifier: Modifier = Modifier,
    text: String,
    contentColor: @Composable ColorScheme.() -> Color,
    onClick: Invoke,
) = BaseButton(
    modifier = modifier,
    text = text,
    bgColor = null,
    borderColor = contentColor,
    textColor = contentColor,
    onClick = onClick,
)
