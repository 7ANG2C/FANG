package com.fang.cosmos.foundation.ui.dsl

import android.app.Activity
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

@Composable
fun StatusBarColor(
    color: Color,
    isAppearanceLight: Boolean = isColorLight(color)
) {
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = color.toArgb()
            WindowCompat.getInsetsController(window, view)
                .isAppearanceLightStatusBars = isAppearanceLight
        }
    }
}

@Composable
fun NavigationBarColor(
    color: Color,
    isAppearanceLight: Boolean = isColorLight(color)
) {
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.navigationBarColor = color.toArgb()
            WindowCompat.getInsetsController(window, view)
                .isAppearanceLightNavigationBars = isAppearanceLight
        }
    }
}