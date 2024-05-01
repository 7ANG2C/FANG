package com.fang.cosmos.definition.datastore.noscoped

import com.fang.cosmos.definition.datastore.QualifierAwareDataStore
import com.fang.cosmos.definition.singleCosmos
import org.koin.core.definition.Definition
import org.koin.core.module.Module

/**
 * Koin [Module] extension for [QualifierAwareDataStore] D.I. .
 */
fun Module.cosmosModuleNonQualifierDataStore(
    createdAtStart: Boolean = false,
    override: Definition<NonQualifierDataStore>? = null
) {
    singleCosmos<QualifierAwareDataStore>(createdAtStart, NonQualifierDataStoreQualifier) {
        override?.invoke(this, it) ?: object : NonQualifierDataStore() {}
    }
}

internal const val NonQualifierDataStoreQualifier = "NonQualifierDataStore"
