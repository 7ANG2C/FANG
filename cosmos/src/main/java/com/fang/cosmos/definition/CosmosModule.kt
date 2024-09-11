package com.fang.cosmos.definition

import com.fang.cosmos.definition.context.cosmosModuleContext
import com.fang.cosmos.definition.datastore.cosmosModuleDataStore
import com.fang.cosmos.definition.externalcoroutinescope.cosmosModuleExternalCoroutineScope
import com.fang.cosmos.definition.gson.cosmosModuleGson
import org.koin.dsl.ModuleDeclaration
import org.koin.dsl.module

/**
 * Default Cosmos koin module definition for DI.
 */
object CosmosModule {
    /**
     * @param override override moduleDeclaration if needed.
     *
     * Call androidContext(YourApplication) first.
     */
    operator fun invoke(override: ModuleDeclaration? = null) =
        module {
            cosmosModuleContext()
            cosmosModuleExternalCoroutineScope()
            cosmosModuleGson()
            cosmosModuleDataStore()
            // override placed last
            override?.invoke(this)
        }
}
