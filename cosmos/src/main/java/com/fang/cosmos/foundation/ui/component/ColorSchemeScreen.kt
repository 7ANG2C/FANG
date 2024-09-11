package com.fang.cosmos.foundation.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.ColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import com.fang.cosmos.foundation.ui.dsl.MaterialTypography
import com.fang.cosmos.foundation.ui.ext.color
import com.fang.cosmos.foundation.ui.ext.textAlign

@Composable
fun ColorSchemeScreen(
    modifier: Modifier,
    dark: ColorScheme,
    light: ColorScheme,
) {
    Column(modifier) {
        listOf<Pair<String, ColorScheme.() -> Color>>(
            "primary" to { primary },
            "onPrimary" to { onPrimary },
            "primaryContainer" to { primaryContainer },
            "onPrimaryContainer" to { onPrimaryContainer },
            "inversePrimary" to { inversePrimary },
            "secondary" to { secondary },
            "onSecondary" to { onSecondary },
            "secondaryContainer" to { secondaryContainer },
            "onSecondaryContainer" to { onSecondaryContainer },
            "tertiary" to { tertiary },
            "onTertiary" to { onTertiary },
            "tertiaryContainer" to { tertiaryContainer },
            "onTertiaryContainer" to { onTertiaryContainer },
            "background" to { background },
            "onBackground" to { onBackground },
            "surface" to { surface },
            "onSurface" to { onSurface },
            "surfaceVariant" to { surfaceVariant },
            "onSurfaceVariant" to { onSurfaceVariant },
            "surfaceTint" to { surfaceTint },
            "inverseSurface" to { inverseSurface },
            "inverseOnSurface" to { inverseOnSurface },
            "error" to { error },
            "onError" to { onError },
            "errorContainer" to { errorContainer },
            "onErrorContainer" to { onErrorContainer },
            "outline" to { outline },
            "outlineVariant" to { outlineVariant },
            "scrim" to { scrim },
            "surfaceBright" to { surfaceBright },
            "surfaceDim" to { surfaceDim },
            "surfaceContainer" to { surfaceContainer },
            "surfaceContainerHigh" to { surfaceContainerHigh },
            "surfaceContainerHighest" to { surfaceContainerHighest },
            "surfaceContainerLow" to { surfaceContainerLow },
            "surfaceContainerLowest" to { surfaceContainerLowest },
        ).forEach { ColorItem(it.first, dark, light, color = it.second) }
    }
}

@Composable
private fun ColorItem(
    text: String,
    dark: ColorScheme,
    light: ColorScheme,
    modifier: Modifier = Modifier,
    color: ColorScheme.() -> Color,
) = Row(modifier.fillMaxWidth()) {
    listOf(
        dark to Color.Black,
        light to Color.White,
    ).forEach {
        AlignText(
            text = text,
            modifier =
                Modifier
                    .weight(1f)
                    .background(it.second),
            style =
                MaterialTypography.bodyMedium.color(color(it.first))
                    .textAlign(TextAlign.Center),
        )
    }
}
