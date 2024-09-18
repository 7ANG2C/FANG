package com.fang.arrangement.ui.shared.component

import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.fang.arrangement.ui.shared.dsl.ContentText
import com.fang.cosmos.foundation.ui.component.AlignText
import com.fang.cosmos.foundation.ui.ext.color

@Composable
internal fun EmptyScreen(modifier: Modifier) {
    Box(modifier = modifier, contentAlignment = Alignment.Center) {
        AlignText(text = "空空如也", style = ContentText.style.color { outline })
    }
}
