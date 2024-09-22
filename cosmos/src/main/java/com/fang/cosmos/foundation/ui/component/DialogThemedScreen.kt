package com.fang.cosmos.foundation.ui.component

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.ColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import com.fang.cosmos.foundation.Invoke
import com.fang.cosmos.foundation.ui.dsl.animateColor
import com.fang.cosmos.foundation.ui.ext.bg
import com.fang.cosmos.foundation.ui.ext.clickableNoRipple

/**
 * DialogThemed Base
 */
@Composable
fun DialogThemedScreen(
    isShow: Boolean,
    overlayColor: @Composable ColorScheme.() -> Color = over@{
        animateColor("DialogOverlayColor") {
            if (isShow) this@over.scrim.copy(alpha = 0.42f) else Color.Transparent
        }
    },
    onClickOutsideDismiss: Invoke? = null,
    content: @Composable AnimatedVisibilityScope.() -> Unit,
) {
    val focusManager = LocalFocusManager.current
    Box(
        modifier =
            Modifier
                .fillMaxSize()
                .then(
                    if (isShow) {
                        Modifier.clickableNoRipple {
                            focusManager.clearFocus()
                            onClickOutsideDismiss?.invoke()
                        }
                    } else {
                        Modifier
                    },
                )
                .bg { overlayColor(this) },
        contentAlignment = Alignment.Center,
    ) {
        AnimatedVisibility(
            visible = isShow,
            modifier =
                Modifier.clickableNoRipple {
                    // intercept
                },
            enter =
                scaleIn(initialScale = 0.85f) + fadeIn(),
            exit =
                scaleOut(targetScale = 0.9f) + fadeOut(),
            content = content,
        )
    }
}
