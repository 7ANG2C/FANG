package com.fang.arrangement.ui.shared.component.chip

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import com.fang.arrangement.ui.shared.component.ArrText
import com.fang.arrangement.ui.shared.dsl.AttendanceNumFormat
import com.fang.cosmos.foundation.ui.dsl.MaterialShape
import com.fang.cosmos.foundation.ui.ext.bg
import com.fang.cosmos.foundation.ui.ext.color

@Composable
internal fun AttendanceChip(
    attendance: Double?,
    fill: Boolean = true,
    bgColor: @Composable ColorScheme.() -> Color,
    textStyle: @Composable ColorScheme.() -> TextStyle,
    placeHolder: Boolean,
) = TextChip(
    text = AttendanceNumFormat(attendance),
    fill = fill,
    bgColor = bgColor,
    textStyle = textStyle,
    placeHolder = placeHolder,
)

@Composable
internal fun TextChip(
    text: String,
    fill: Boolean = true,
    bgColor: @Composable ColorScheme.() -> Color,
    textStyle: @Composable ColorScheme.() -> TextStyle,
    placeHolder: Boolean,
) {
    Box(
        modifier =
            Modifier
                .bg(MaterialShape.extraSmall, bgColor)
                .padding(horizontal = 4.dp),
        contentAlignment = Alignment.Center,
    ) {
        ArrText(text = if (fill) "000.0" else "1") {
            textStyle(this).color(Color.Transparent)
        }
        if (placeHolder) {
            ArrText(text = text) {
                textStyle(this).color(Color.Transparent)
            }
        } else {
            ArrText(text = text) {
                textStyle(this)
            }
        }
    }
}
