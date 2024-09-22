package com.fang.arrangement.ui.shared.component.fieldrow

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.unit.dp
import com.fang.arrangement.R
import com.fang.arrangement.ui.shared.component.ArrText
import com.fang.arrangement.ui.shared.dsl.ContentText
import com.fang.cosmos.foundation.ui.component.CustomIcon
import com.fang.cosmos.foundation.ui.dsl.MaterialColor
import com.fang.cosmos.foundation.ui.ext.bg
import com.fang.cosmos.foundation.ui.ext.clickableNoRipple
import com.fang.cosmos.foundation.ui.ext.color

@Composable
fun RemovableRow(
    modifier: Modifier,
    content: @Composable RowScope.() -> Unit,
    onRemove: () -> Unit,
) {
    Row(modifier = modifier.height(IntrinsicSize.Max)) {
        Row(modifier = Modifier.weight(1f), content = content)
        val focusManager = LocalFocusManager.current
        Box(
            modifier =
                Modifier
                    .clickableNoRipple {
                        focusManager.clearFocus()
                        onRemove()
                    }
                    .padding(start = 4.dp),
        ) {
            ArrText(text = "1") {
                ContentText.style.color(Color.Transparent)
            }
            CustomIcon(
                drawableResId = R.drawable.arr_r24_remove,
                modifier = Modifier.bg(CircleShape) { error.copy(alpha = 0.18f) },
                tint = MaterialColor.error,
            )
        }
    }
}
