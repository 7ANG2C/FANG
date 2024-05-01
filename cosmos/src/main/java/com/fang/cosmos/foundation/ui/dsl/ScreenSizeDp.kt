package com.fang.cosmos.foundation.ui.dsl

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.dp

val screenHeight @Composable get() = LocalConfiguration.current.screenHeightDp
val screenWidth @Composable get() = LocalConfiguration.current.screenWidthDp
val screenHeightDp @Composable get() = screenHeight.dp
val screenWidthDp @Composable get() = screenWidth.dp
