package com.fang.arrangement.ui.shared.component

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.fang.cosmos.foundation.ui.dsl.MaterialColor
import com.fang.cosmos.foundation.ui.ext.bg

@Composable
fun SelectedTag() {
    Box(
        modifier =
            Modifier
                .size(16.dp)
                .border(1.dp, MaterialColor.primary, CircleShape),
        contentAlignment = Alignment.Center,
    ) {
        Box(
            modifier =
                Modifier
                    .size(8.dp)
                    .bg(CircleShape) { primary },
        )
    }
}
