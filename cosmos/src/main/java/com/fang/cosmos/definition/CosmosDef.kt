package com.fang.cosmos.definition

import android.content.Context
import com.fang.cosmos.definition.datastore.QualifierAwareDataStore
import com.fang.cosmos.definition.datastore.noscoped.NON_QUALIFIER_DATA_STORE_QUALIFIER
import com.fang.cosmos.foundation.koin.getDefinition
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineScope
import org.koin.core.parameter.ParametersDefinition

/**
 * Get default definition implement by Cosmos module.
 */
object CosmosDef {
    val Context get() = getFangsDef<Context>()
    val CoroutineScope get() = getFangsDef<CoroutineScope>()
    val Gson get() = getFangsDef<Gson>()
    val NonQualifiedDataStore
        get() = getFangsDef<QualifierAwareDataStore>(NON_QUALIFIER_DATA_STORE_QUALIFIER)

    private inline fun <reified T : Any> getFangsDef(
        qualifier: String = "",
        noinline parameters: ParametersDefinition? = null,
    ) = getDefinition<T>(cosmosQualifier(qualifier), parameters)
}
