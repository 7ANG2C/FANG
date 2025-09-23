package com.fang.arrangement.ui.shared.component

import androidx.compose.foundation.layout.Row
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.fang.arrangement.ui.shared.dsl.ContentText
import com.fang.cosmos.foundation.ui.component.HorizontalSpacer

@Composable
fun ToggleBox(
    modifier: Modifier,
    text: String,
    checked: Boolean,
) = Row(
    modifier = modifier,
    verticalAlignment = Alignment.CenterVertically,
) {
    ContentText(text)
    HorizontalSpacer(2)
    Checkbox(
        checked = checked,
        onCheckedChange = null,
        colors =
            CheckboxDefaults.colors().copy(
                checkedBoxColor = ContentText.color,
                checkedBorderColor = ContentText.color,
                uncheckedBorderColor = ContentText.color.copy(alpha = 0.88f),
            ),
    )
}
