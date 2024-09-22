package com.fang.cosmos.foundation.ui.ext

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp

val Float.textDp: TextUnit
    @Composable get() =
        with(LocalDensity.current) {
            this@textDp.dp.toSp()
        }

val Int.textDp: TextUnit
    @Composable get() = toFloat().textDp
