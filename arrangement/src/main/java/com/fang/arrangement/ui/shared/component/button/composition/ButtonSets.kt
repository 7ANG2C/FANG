package com.fang.arrangement.ui.shared.component.button.composition

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.fang.arrangement.ui.shared.component.button.component.DeleteButton
import com.fang.arrangement.ui.shared.component.button.component.NegativeButton
import com.fang.arrangement.ui.shared.component.button.component.PositiveButton
import com.fang.cosmos.foundation.Invoke

@Composable
internal fun ButtonSets(
    modifier: Modifier,
    onDelete: Invoke? = null,
    onNegative: Invoke,
    onPositive: Invoke?,
) = BaseButtonSets(
    modifier,
    neutral =
        onDelete?.let { delete ->
            { DeleteButton(onClick = delete) }
        },
    negative = {
        NegativeButton(onClick = onNegative)
    },
    positive = {
        PositiveButton(onClick = onPositive)
    },
)
