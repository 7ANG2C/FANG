package com.fang.cosmos.foundation.ui.ext

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp

val Double.textDp: TextUnit
    @Composable get() = with(LocalDensity.current) {
        this@textDp.dp.toSp()
    }

val Float.textDp: TextUnit
    @Composable get() = toDouble().textDp

val Int.textDp: TextUnit
    @Composable get() = toDouble().textDp
