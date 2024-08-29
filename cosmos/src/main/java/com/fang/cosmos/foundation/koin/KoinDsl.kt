package com.fang.cosmos.foundation.koin

import org.koin.core.Koin
import org.koin.core.parameter.ParametersDefinition
import org.koin.core.qualifier.Qualifier
import org.koin.mp.KoinPlatform.getKoin

/**
 * quick usage for [Koin.get]
 */
inline fun <reified T : Any> getDefinition(
    qualifier: Qualifier? = null,
    noinline parameters: ParametersDefinition? = null,
) = getKoin().get<T>(qualifier, parameters)

/**
 * quick usage for [Koin.getOrNull]
 */
inline fun <reified T : Any> getDefinitionOrNull(
    qualifier: Qualifier? = null,
    noinline parameters: ParametersDefinition? = null,
) = getKoin().getOrNull<T>(qualifier, parameters)
