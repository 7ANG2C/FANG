package com.fang.arrangement.ui.screen.btmnav

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.fang.cosmos.foundation.ui.dsl.ComposableInvoke

@Composable
internal fun BtmNavScreen(
    modifier: Modifier,
    content: ComposableInvoke
) {
    Column(modifier = modifier) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            color = Color.Transparent,
            content = content
        )
    }
}