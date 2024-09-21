package com.fang.arrangement.ui.shared.component

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.PlatformTextStyle
import androidx.compose.ui.unit.dp
import com.fang.arrangement.R
import com.fang.arrangement.ui.shared.dsl.ContentText
import com.fang.cosmos.foundation.Invoke
import com.fang.cosmos.foundation.ui.component.CustomIcon
import com.fang.cosmos.foundation.ui.component.VerticalSpacer
import com.fang.cosmos.foundation.ui.dsl.MaterialColor
import com.fang.cosmos.foundation.ui.dsl.MaterialTypography
import com.fang.cosmos.foundation.ui.ext.clickableNoRipple
import com.fang.cosmos.foundation.ui.ext.color

@Composable
internal fun Field(
    modifier: Modifier,
    titleText: String,
    text: String,
    onClear: Invoke? = null,
) = BaseField(
    modifier = modifier,
    title = titleText,
    onClear = onClear,
) {
    ContentText(text = text)
}

@Composable
internal fun BaseField(
    modifier: Modifier,
    title: String,
    onClear: Invoke? = null,
    content: @Composable BoxScope.() -> Unit,
) {
    Column(modifier) {
        val hintColor = MaterialColor.outline
        FieldLabelText(
            text = title,
            color = hintColor,
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(modifier = Modifier.weight(1f), content = content)
            onClear?.let {
                CustomIcon(
                    drawableResId = R.drawable.arr_r24_cancel,
                    modifier =
                        Modifier
                            .clickableNoRipple(onClick = onClear)
                            .padding(4.dp),
                    tint = hintColor.copy(alpha = 0.82f),
                )
            }
        }
        VerticalSpacer(1)
        HorizontalDivider(
            modifier = Modifier.fillMaxWidth(),
            thickness = 1.dp,
            color = hintColor.copy(alpha = 0.72f),
        )
    }
}

@Composable
internal fun FieldLabelText(
    text: String,
    modifier: Modifier = Modifier,
    color: Color = MaterialColor.outline,
) {
    Text(
        text = text,
        modifier = modifier,
        style =
            MaterialTypography.labelLarge.color(color)
                .copy(platformStyle = PlatformTextStyle(includeFontPadding = false)),
    )
}
