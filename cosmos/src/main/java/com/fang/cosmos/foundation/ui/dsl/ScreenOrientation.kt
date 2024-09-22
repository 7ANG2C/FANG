package com.fang.cosmos.foundation.ui.dsl

import android.content.pm.ActivityInfo
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.platform.LocalContext
import com.fang.cosmos.foundation.findActivity

/**
 * Request screen orientation in composable.
 * @param orientation [ActivityInfo.screenOrientation]
 */
@Composable
fun ScreenOrientation(
    orientation: Int,
    temporary: Boolean,
) {
    val context = LocalContext.current
    DisposableEffect(orientation) {
        val activity = context.findActivity() ?: return@DisposableEffect onDispose {}
        val originalOrientation = activity.requestedOrientation
        activity.requestedOrientation = orientation
        onDispose {
            if (temporary) activity.requestedOrientation = originalOrientation
        }
    }
}

enum class Orientation {
    PORTRAIT,
    LANDSCAPE,
    ;

    enum class Mode { UNSPECIFIED, SENSOR, REVERSE, USER }
}

@Composable
fun ScreenOrientation(
    orientation: Orientation,
    temporary: Boolean,
    mode: Orientation.Mode,
) {
    when (orientation to mode) {
        Orientation.PORTRAIT to Orientation.Mode.UNSPECIFIED -> ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        Orientation.PORTRAIT to Orientation.Mode.SENSOR -> ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT
        Orientation.PORTRAIT to Orientation.Mode.REVERSE -> ActivityInfo.SCREEN_ORIENTATION_REVERSE_PORTRAIT
        Orientation.PORTRAIT to Orientation.Mode.USER -> ActivityInfo.SCREEN_ORIENTATION_USER_PORTRAIT
        Orientation.LANDSCAPE to Orientation.Mode.UNSPECIFIED -> ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
        Orientation.LANDSCAPE to Orientation.Mode.SENSOR -> ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE
        Orientation.LANDSCAPE to Orientation.Mode.REVERSE -> ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE
        Orientation.LANDSCAPE to Orientation.Mode.USER -> ActivityInfo.SCREEN_ORIENTATION_USER_LANDSCAPE
        else -> null
    }?.let { ScreenOrientation(it, temporary) }
}

@Composable
fun ScreenPortrait(
    temporary: Boolean,
    mode: Orientation.Mode = Orientation.Mode.UNSPECIFIED,
) = ScreenOrientation(Orientation.PORTRAIT, temporary, mode)

@Composable
fun ScreenLandscape(
    temporary: Boolean,
    mode: Orientation.Mode = Orientation.Mode.UNSPECIFIED,
) = ScreenOrientation(Orientation.LANDSCAPE, temporary, mode)
