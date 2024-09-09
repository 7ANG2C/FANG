package com.fang.cosmos.definition.datastore

import com.fang.cosmos.definition.singleCosmos
import org.koin.core.definition.Definition
import org.koin.core.module.Module

/**
 * Koin [Module] extension for [CosmosDataStore] D.I. .
 */
fun Module.cosmosModuleDataStore(
    createdAtStart: Boolean = false,
    override: Definition<CosmosDataStore>? = null,
) {
    singleCosmos<CosmosDataStore>(createdAtStart) {
        override?.invoke(this, it) ?: CosmosDataStore()
    }
}
