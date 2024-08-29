package com.fang.arrangement.ui.graph.dsl

import androidx.navigation.NavBackStackEntry
import com.fang.arrangement.ui.screen.btmnav.BtmNavItem
import com.fang.cosmos.definition.CosmosDef
import com.fang.cosmos.foundation.gson.fromJsonTypeToken
import com.google.gson.Gson

internal val NavBackStackEntry?.currentBtmNavItem
    get() = BtmNavItem.all.find { it.route in this?.destination?.route.orEmpty() }

internal val NavBackStackEntry?.isCurrentBtmNavItem
    get() = currentBtmNavItem != null

internal inline fun <reified T> NavBackStackEntry.getArgument(
    key: String? = null, gson: Gson = CosmosDef.Gson
) = gson.fromJsonTypeToken<T>(arguments?.getString(key ?: T::class.java.simpleName).orEmpty())
    .getOrNull()
