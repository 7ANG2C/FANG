package com.fang.arrangement.ui.shared.component

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.FloatingActionButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.unit.dp
import com.fang.arrangement.R
import com.fang.arrangement.ui.shared.ext.clickRipple
import com.fang.cosmos.foundation.Action
import com.fang.cosmos.foundation.Invoke
import com.fang.cosmos.foundation.ui.component.CustomIcon
import com.fang.cosmos.foundation.ui.dsl.MaterialColor

@Composable
internal inline fun <reified T> ArrangementList(
    modifier: Modifier = Modifier,
    items: List<T>,
    noinline key: ((item: T) -> Any)? = null,
    noinline contentType: (item: T) -> Any? = { null },
    noinline onSelect: Action<T>,
    noinline onAdd: Invoke,
    crossinline content: @Composable ColumnScope.(item: T) -> Unit,
) = Box(Modifier.fillMaxSize()) {
    Crossfade(
        targetState = items,
        label = "ArrangementList${T::class.java.simpleName}",
    ) { innerItems ->
        if (innerItems.isEmpty()) {
            EmptyScreen(modifier = Modifier.fillMaxSize())
        } else {
            LazyColumn(
                modifier = modifier,
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                items(
                    items = innerItems,
                    key = key,
                    contentType = contentType,
                ) { item ->
                    ArrangementCard(
                        modifier =
                            Modifier
                                .animateItem()
                                .fillMaxWidth()
                                .clickRipple { onSelect(item) },
                    ) {
                        content(this, item)
                    }
                }
                item {
                    Fab(
                        modifier =
                            Modifier
                                .padding(bottom = 8.dp)
                                .alpha(0f),
                        onClick = {},
                    )
                }
            }
        }
    }
    Fab(
        modifier =
            Modifier
                .padding(20.dp)
                .align(Alignment.BottomEnd),
        onClick = onAdd,
    )
}

@Composable
internal fun Fab(
    modifier: Modifier,
    onClick: Invoke,
) {
    val iconTint = MaterialColor.onSecondaryContainer
    FloatingActionButton(
        onClick = onClick,
        modifier = modifier,
        containerColor = MaterialColor.inversePrimary,
        contentColor = iconTint,
    ) {
        CustomIcon(
            drawableResId = R.drawable.arr_r24_add,
            tint = iconTint,
        )
    }
}
