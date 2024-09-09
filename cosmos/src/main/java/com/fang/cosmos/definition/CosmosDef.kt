package com.fang.cosmos.definition

import android.content.Context
import com.fang.cosmos.definition.datastore.CosmosDataStore
import com.fang.cosmos.foundation.koin.getDefinition
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineScope
import org.koin.core.parameter.ParametersDefinition

/**
 * Get default definition implement by Cosmos module.
 */
object CosmosDef {
    val Context get() = getCosmosDef<Context>()
    val CoroutineScope get() = getCosmosDef<CoroutineScope>()
    val Gson get() = getCosmosDef<Gson>()
    val DataStore get() = getCosmosDef<CosmosDataStore>()

    private inline fun <reified T : Any> getCosmosDef(
        qualifier: String = "",
        noinline parameters: ParametersDefinition? = null,
    ) = getDefinition<T>(cosmosQualifier(qualifier), parameters)
}
