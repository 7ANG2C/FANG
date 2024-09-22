package com.fang.arrangement.ui.shared.component.button.component

import androidx.compose.material3.ColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.fang.arrangement.ui.shared.component.button.foundation.OutlineButton
import com.fang.cosmos.foundation.Invoke

@Composable
internal fun NegativeButton(
    modifier: Modifier = Modifier,
    text: String = "取消",
    color: @Composable ColorScheme.() -> Color = { outline },
    onClick: Invoke,
) = OutlineButton(
    modifier = modifier,
    text = text,
    contentColor = color,
    onClick = onClick,
)
