package com.fang.arrangement.ui.shared.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.fang.cosmos.foundation.ui.dsl.MaterialColor

@Composable
internal fun ArrangementCard(
    modifier: Modifier,
    content: @Composable ColumnScope.() -> Unit,
) = ElevatedCard(
    modifier = modifier,
    colors =
        CardDefaults.elevatedCardColors().copy(
            containerColor = MaterialColor.surfaceContainer,
        ),
) {
    Column(
        modifier =
            Modifier
                .fillMaxWidth()
                .padding(12.dp),
        verticalArrangement = Arrangement.spacedBy(2.8.dp),
        content = content,
    )
}
