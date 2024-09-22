package com.fang.arrangement.ui.shared.component

import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

@Composable
internal fun EmptyScreen(modifier: Modifier) {
    Box(modifier = modifier, contentAlignment = Alignment.Center) {
//        AlignText(text = "空空如也", style = ContentText.style.color { outline })
    }
}
