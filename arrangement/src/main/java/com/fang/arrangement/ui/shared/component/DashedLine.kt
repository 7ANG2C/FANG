package com.fang.arrangement.ui.shared.component

import androidx.compose.foundation.Canvas
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.unit.dp

@Composable
fun DashedLine(
    modifier: Modifier,
    color: Color = Color.Gray,
) {
    Canvas(modifier) {
        drawLine(
            color = color,
            start = Offset(0f, 0f),
            end = Offset(size.width, 0f),
            strokeWidth = 1.dp.toPx(),
            pathEffect =
                PathEffect.dashPathEffect(
                    intervals = floatArrayOf(6.dp.toPx(), 6.dp.toPx()),
                ),
        )
    }
}
