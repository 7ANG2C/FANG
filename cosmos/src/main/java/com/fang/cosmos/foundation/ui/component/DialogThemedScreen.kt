package com.fang.cosmos.foundation.ui.component

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.fang.cosmos.foundation.typealiaz.Invoke
import com.fang.cosmos.foundation.ui.ext.clickableNoRipple

/**
 * DialogThemed Base
 */
@Composable
fun DialogThemedScreen(
    isShow: Boolean,
    overlayColor: Long = 0x40000000,
    onClickOutsideDismiss: Invoke = {},
    content: @Composable AnimatedVisibilityScope.() -> Unit,
) {
    Box(
        modifier =
            Modifier
                .fillMaxSize()
                .then(
                    if (isShow) {
                        Modifier.clickableNoRipple(onClick = onClickOutsideDismiss)
                    } else {
                        Modifier
                    },
                )
                .background(
                    animateColorAsState(
                        targetValue = if (isShow) Color(overlayColor) else Color.Transparent,
                        label = "DialogOverlayColor",
                    ).value,
                ),
        contentAlignment = Alignment.Center,
    ) {
        AnimatedVisibility(
            visible = isShow,
            modifier =
                Modifier.clickableNoRipple {
                    // intercept
                },
            enter = scaleIn(initialScale = 0.85f) + fadeIn(),
            exit = scaleOut(targetScale = 0.9f) + fadeOut(),
            content = content,
        )
    }
}
