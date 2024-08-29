package com.fang.cosmos.definition

import com.fang.cosmos.Cosmos
import org.koin.core.definition.Definition
import org.koin.core.definition.KoinDefinition
import org.koin.core.module.Module
import org.koin.core.qualifier.named

internal fun cosmosQualifier(name: String) = named("${Cosmos.QUALIFIER}def_mdl$name")

internal inline fun <reified T> Module.singleCosmos(
    createdAtStart: Boolean = false,
    qualifier: String = "",
    noinline definition: Definition<T>,
): KoinDefinition<T> = single<T>(cosmosQualifier(qualifier), createdAtStart, definition)
