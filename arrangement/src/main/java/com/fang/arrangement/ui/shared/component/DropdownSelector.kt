package com.fang.arrangement.ui.shared.component

import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.PopupProperties
import com.fang.arrangement.ui.shared.ext.clickRipple
import com.fang.cosmos.foundation.Action
import com.fang.cosmos.foundation.Invoke
import com.fang.cosmos.foundation.ui.dsl.MaterialShape
import com.fang.cosmos.foundation.ui.ext.crop

@Composable
internal fun <T> DropdownSelector(
    items: Collection<T>,
    selected: T?,
    expandedState: MutableState<Boolean>,
    modifier: Modifier = Modifier,
    onDismissRequest: Invoke = {},
    offset: DpOffset = DpOffset(0.dp, 6.dp),
    scrollState: ScrollState = rememberScrollState(),
    properties: PopupProperties = PopupProperties(focusable = true),
    shape: Shape = MaterialShape.small,
    onSelected: Action<T>,
    content: @Composable ColumnScope.(T) -> Unit,
) = DropdownMenu(
    expanded = expandedState.value,
    onDismissRequest = {
        onDismissRequest()
        expandedState.value = false
    },
    modifier =
        modifier.crop(vertical = 8.dp).clip(RoundedCornerShape(8.dp)),
    offset = offset,
    scrollState = scrollState,
    properties = properties,
    shape = shape,
) {
    items.forEachIndexed { i, item ->
        Column(
            modifier =
                Modifier.fillMaxWidth()
                    .clickRipple {
                        if (selected != item) onSelected(item)
                        expandedState.value = false
                    },
        ) {
            content(item)
        }
        if (i != items.size - 1) {
            HorizontalDivider(
                Modifier.fillMaxWidth()
                    .padding(horizontal = 16.dp),
            )
        }
    }
}
