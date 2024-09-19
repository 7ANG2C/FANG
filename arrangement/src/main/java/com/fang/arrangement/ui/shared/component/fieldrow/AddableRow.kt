package com.fang.arrangement.ui.shared.component.fieldrow

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.fang.arrangement.R
import com.fang.cosmos.foundation.Invoke
import com.fang.cosmos.foundation.ui.component.CustomIcon
import com.fang.cosmos.foundation.ui.dsl.animateColor
import com.fang.cosmos.foundation.ui.ext.bg
import com.fang.cosmos.foundation.ui.ext.clickableNoRipple

@Composable
internal fun AddableRow(
    modifier: Modifier,
    content: @Composable RowScope.() -> Unit,
    onAdd: Invoke?,
    decorationAdd: @Composable (innerTextField: @Composable () -> Unit) -> Unit = @Composable { innerAddIcon -> innerAddIcon() },
) {
    Row(
        modifier = modifier.height(IntrinsicSize.Max),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Row(modifier = Modifier.weight(1f), content = content)
        decorationAdd {
            Box(
                Modifier
                    .clickableNoRipple { onAdd?.invoke() }
                    .padding(start = 4.dp),
            ) {
                CustomIcon(
                    drawableResId = R.drawable.arr_r24_add,
                    modifier =
                        Modifier.bg(CircleShape) {
                            animateColor(label = "AddableBg") {
                                primary.copy(alpha = if (onAdd != null) 0.38f else 0f)
                            }
                        },
                    tint =
                        animateColor(label = "AddableTint") {
                            secondary.copy(alpha = if (onAdd != null) 1f else 0f)
                        },
                )
            }
        }
    }
}
