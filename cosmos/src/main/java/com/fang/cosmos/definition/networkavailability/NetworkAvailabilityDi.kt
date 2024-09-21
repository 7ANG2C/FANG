package com.fang.cosmos.definition.networkavailability

import com.fang.cosmos.definition.singleCosmos
import org.koin.core.definition.Definition
import org.koin.core.module.Module

/**
 * Koin [Module] extension for [NetworkAvailability] D.I. .
 */
fun Module.cosmosModuleNetworkAvailability(
    createdAtStart: Boolean = false,
    override: Definition<NetworkAvailability>? = null,
) {
    singleCosmos<NetworkAvailability>(createdAtStart) {
        override?.invoke(this, it) ?: object : DefaultNetworkAvailability() {}
    }
}
