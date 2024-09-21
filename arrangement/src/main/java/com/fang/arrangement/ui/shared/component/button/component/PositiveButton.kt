package com.fang.arrangement.ui.shared.component.button.component

import androidx.compose.material3.ColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.fang.arrangement.ui.shared.component.button.foundation.FilledButton
import com.fang.cosmos.foundation.Invoke
import com.fang.cosmos.foundation.ui.dsl.animateColor

@Composable
internal fun PositiveButton(
    modifier: Modifier = Modifier,
    text: String = "確定",
    onClick: Invoke?,
    bgColor: @Composable ColorScheme.() -> Color = {
        animateColor(label = "PositiveBg") {
            primary
                .copy(alpha = if (onClick == null) 0.4f else 1f)
        }
    },
    textColor: @Composable ColorScheme.() -> Color = {
        animateColor(label = "PositiveText") {
            surfaceDim.copy(alpha = if (onClick == null) 0.95f else 1f)
        }
    },
) = FilledButton(
    modifier = modifier,
    text = text,
    bgColor = bgColor,
    textColor = textColor,
    onClick = onClick ?: {},
)
