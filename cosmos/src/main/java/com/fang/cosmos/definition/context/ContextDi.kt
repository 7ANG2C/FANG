package com.fang.cosmos.definition.context

import android.content.Context
import com.fang.cosmos.definition.singleCosmos
import org.koin.core.definition.Definition
import org.koin.core.module.Module

/**
 * Koin [Module] extension for [Context] D.I. .
 *
 * Call androidContext(YourApplication) first.
 */
fun Module.cosmosModuleContext(
    createdAtStart: Boolean = false,
    override: Definition<Context>? = null,
) = singleCosmos<Context>(createdAtStart) {
    override?.invoke(this, it) ?: get()
}
