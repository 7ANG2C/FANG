package com.fang.cosmos.foundation.ui.ext

import androidx.compose.foundation.Indication
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.layout.layout
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.fang.cosmos.foundation.Invoke

fun Modifier.clickableNoRipple(
    interactionSource: MutableInteractionSource? = null,
    enabled: Boolean = true,
    onClickLabel: String? = null,
    role: Role? = null,
    onClick: Invoke,
) = then(
    Modifier.clickableRipple(
        interactionSource = interactionSource,
        indication = null,
        enabled = enabled,
        onClickLabel = onClickLabel,
        role = role,
        onClick = onClick,
    ),
)

fun Modifier.clickableRipple(
    interactionSource: MutableInteractionSource? = null,
    indication: Indication? = null,
    enabled: Boolean = true,
    onClickLabel: String? = null,
    role: Role? = null,
    onClick: Invoke,
) = composed {
    then(
        Modifier.clickable(
            interactionSource = interactionSource ?: remember { MutableInteractionSource() },
            indication = indication,
            enabled = enabled,
            onClickLabel = onClickLabel,
            role = role,
            onClick = onClick,
        ),
    )
}

fun Modifier.crop(
    horizontal: Dp = 0.dp,
    vertical: Dp = 0.dp,
) = layout { measurable, constraints ->
    val placeable = measurable.measure(constraints)

    fun Dp.toPxInt() = toPx().toInt()
    layout(
        placeable.width - (horizontal * 2).toPxInt(),
        placeable.height - (vertical * 2).toPxInt(),
    ) {
        placeable.placeRelative(-horizontal.toPxInt(), -vertical.toPxInt())
    }
}
