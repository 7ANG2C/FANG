package com.fang.cosmos.foundation.ui.dsl

import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat
import com.fang.cosmos.foundation.context.findActivity

@Composable
fun SystemBarColor(
    status: Color? = null,
    appearanceLightStatus: Boolean? = status?.let { isColorLight(it) },
    navigation: Color? = null,
    appearanceLightNav: Boolean? = navigation?.let { isColorLight(it) },
) {
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            view.context.findActivity()?.window?.let { window ->
                status?.let {
                    window.statusBarColor = it.toArgb()
                }
                navigation?.let {
                    window.navigationBarColor = it.toArgb()
                }
                appearanceLightStatus?.let {
                    WindowCompat.getInsetsController(window, view)
                        .isAppearanceLightStatusBars = it
                }
                appearanceLightNav?.let {
                    WindowCompat.getInsetsController(window, view)
                        .isAppearanceLightNavigationBars = it
                }
            }
        }
    }
}
