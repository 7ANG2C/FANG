package com.fang.arrangement.ui.shared.component.button.foundation

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.fang.arrangement.ui.shared.dsl.ContentText
import com.fang.arrangement.ui.shared.ext.clickRipple
import com.fang.cosmos.foundation.Invoke
import com.fang.cosmos.foundation.ui.component.AlignText
import com.fang.cosmos.foundation.ui.dsl.MaterialColor
import com.fang.cosmos.foundation.ui.dsl.MaterialShape
import com.fang.cosmos.foundation.ui.ext.bg
import com.fang.cosmos.foundation.ui.ext.color
import com.fang.cosmos.foundation.ui.ext.fontSize

@Composable
internal fun BaseButton(
    modifier: Modifier = Modifier,
    text: String,
    bgColor: @Composable (ColorScheme.() -> Color)?,
    borderColor: @Composable (ColorScheme.() -> Color)?,
    textColor: @Composable ColorScheme.() -> Color,
    onClick: Invoke,
) {
    Box(modifier = modifier) {
        val shape = MaterialShape.small
        AlignText(
            text = text,
            modifier =
                Modifier
                    .clip(shape)
                    .then(
                        bgColor?.let { Modifier.bg(shape, it) } ?: Modifier,
                    )
                    .then(
                        borderColor?.let {
                            Modifier.border(
                                1.dp,
                                borderColor(MaterialColor),
                                shape,
                            )
                        } ?: Modifier,
                    )
                    .clickRipple(onClick = onClick)
                    .padding(horizontal = 16.dp, vertical = 6.dp),
            style =
                ContentText.style.color(textColor).fontSize(15.2.sp),
        )
    }
}
