package com.fang.arrangement.ui.shared.component.chip

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.PlatformTextStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.fang.cosmos.foundation.ui.dsl.ColorLuma
import com.fang.cosmos.foundation.ui.dsl.MaterialColor
import com.fang.cosmos.foundation.ui.dsl.MaterialShape
import com.fang.cosmos.foundation.ui.ext.bg

@Composable
internal fun FullChip(
    modifier: Modifier = Modifier,
    tint: Color = MaterialColor.primary,
) {
    FilledTag(modifier = modifier, text = '全', tint = tint)
}

@Composable
internal fun HalfChip(
    modifier: Modifier = Modifier,
    tint: Color = MaterialColor.primary,
) {
    OutlinedTag(modifier = modifier, text = '半', tint = tint)
}

@Composable
internal fun DeletedTag(
    modifier: Modifier = Modifier,
    tint: Color = Color(0xFFC46D90),
) {
    FilledTag(modifier = modifier, text = '刪', tint = tint)
}

@Composable
internal fun ExpiredTag(
    modifier: Modifier = Modifier,
    tint: Color = Color(0xFFC08F64),
) {
    FilledTag(modifier = modifier, text = '離', tint = tint)
}

@Composable
internal fun ArchivedTag(
    modifier: Modifier = Modifier,
    tint: Color = Color(0xFF77C2B2),
) {
    FilledTag(modifier = modifier, text = '封', tint = tint)
}

@Composable
internal fun UnarchivedTag(
    modifier: Modifier = Modifier,
    tint: Color = Color(0xFF77C2B2),
) {
    OutlinedTag(modifier = modifier, text = '封', tint = tint)
}

@Composable
internal fun RemarkTag(
    modifier: Modifier = Modifier,
    tint: Color = Color(0xFF1F8CBD),
) {
    OutlinedTag(modifier = modifier, text = '註', tint = tint)
}

@Composable
private fun FilledTag(
    modifier: Modifier,
    text: Char,
    tint: Color,
) {
    Box(modifier = modifier) {
        Column(
            modifier =
                Modifier
                    .width(IntrinsicSize.Min)
                    .bg(MaterialShape.extraSmall) { tint },
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                modifier =
                    Modifier
                        .aspectRatio(1f)
                        .padding(0.6.dp)
                        .wrapContentHeight(Alignment.CenterVertically)
                        .wrapContentWidth(Alignment.CenterHorizontally),
                text = text.toString(),
                style =
                    TextStyle(
                        fontWeight = FontWeight.W600,
                        platformStyle = PlatformTextStyle(includeFontPadding = false),
                        color =
                            if (ColorLuma.isLight(tint)) {
                                Color.Black
                            } else {
                                Color.White
                            },
                    ),
            )
        }
    }
}

@Composable
private fun OutlinedTag(
    modifier: Modifier,
    text: Char,
    tint: Color,
) {
    Box(modifier = modifier) {
        Column(
            modifier =
                Modifier
                    .width(IntrinsicSize.Min)
                    .border(0.8.dp, tint, MaterialShape.extraSmall),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                modifier =
                    Modifier
                        .aspectRatio(1f)
                        .padding(0.6.dp)
                        .wrapContentHeight(Alignment.CenterVertically)
                        .wrapContentWidth(Alignment.CenterHorizontally),
                text = text.toString(),
                style =
                    TextStyle(
                        fontWeight = FontWeight.W600,
                        platformStyle = PlatformTextStyle(includeFontPadding = false),
                        color = tint,
                    ),
            )
        }
    }
}
