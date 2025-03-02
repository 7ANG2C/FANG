package com.fang.arrangement.ui.shared.ext

import androidx.compose.foundation.Indication
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.material3.ripple
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.semantics.Role
import com.fang.cosmos.foundation.Invoke
import com.fang.cosmos.foundation.ui.dsl.MaterialColor
import com.fang.cosmos.foundation.ui.ext.clickableRipple

internal fun Modifier.clickRipple(
    interactionSource: MutableInteractionSource? = null,
    indication: Indication? = null,
    enabled: Boolean = true,
    onClickLabel: String? = null,
    role: Role? = null,
    onClick: Invoke,
) = composed {
    Modifier.clickableRipple(
        interactionSource = interactionSource,
        indication = indication
            ?: ripple(color = MaterialColor.inversePrimary.copy(alpha = 0.35f)),
        enabled = enabled,
        onClickLabel = onClickLabel,
        role = role,
        onClick = onClick,
    )
}
