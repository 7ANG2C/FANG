package com.fang.arrangement.ui.shared.component.dialog

import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.fang.arrangement.ui.shared.dsl.ContentText
import com.fang.arrangement.ui.shared.ext.clickRipple
import com.fang.cosmos.foundation.Invoke
import com.fang.cosmos.foundation.ui.component.AlignText
import com.fang.cosmos.foundation.ui.ext.color
import com.fang.cosmos.foundation.ui.ext.textAlign

@Composable
internal fun SingleOptionDialog(
    text: String?,
    onDismiss: Invoke,
    content: @Composable ColumnScope.(TextStyle) -> Unit = { style ->
        HorizontalDivider(
            modifier = Modifier.fillMaxWidth(),
            thickness = 1.dp,
            color = MaterialTheme.colorScheme.surfaceVariant,
        )
        AlignText(
            text = "確定",
            modifier =
                Modifier
                    .fillMaxWidth()
                    .clickRipple(onClick = onDismiss)
                    .padding(horizontal = 20.dp, vertical = 16.dp),
            style =
                style.color(ContentText.color)
                    .textAlign(TextAlign.Center),
        )
    },
) = Dialog(text = text, options = content)
