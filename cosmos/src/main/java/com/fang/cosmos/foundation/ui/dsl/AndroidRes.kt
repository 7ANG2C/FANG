package com.fang.cosmos.foundation.ui.dsl

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext

@Composable
fun getStringRes(
    name: String,
    pkgName: String,
) = LocalContext.current.createConfigurationContext(LocalConfiguration.current)
    .resources.getString(getStringResource(name, pkgName))

fun getStringResource(
    name: String,
    pkgName: String,
) = Class.forName(
    "$pkgName.R\$string",
).getDeclaredField(name).get(null) as Int
