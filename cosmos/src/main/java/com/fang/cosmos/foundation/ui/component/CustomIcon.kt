package com.fang.cosmos.foundation.ui.component

import androidx.annotation.DrawableRes
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.res.vectorResource

/**
 * 客製化 Icon ，解決原生 Composable 一定要輸入 contentDescription 的問題
 */
@Composable
fun CustomIcon(
    @DrawableRes drawableResId: Int,
    modifier: Modifier = Modifier,
    tint: Color = Color.Unspecified,
    contentDescription: String = ""
) {
    val painter = rememberVectorPainter(
        ImageVector.vectorResource(id = drawableResId)
    )
    Icon(
        painter = painter,
        contentDescription = contentDescription,
        modifier = modifier,
        tint = tint
    )
}
