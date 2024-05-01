package com.fang.cosmos.definition.externalcoroutinescope

import com.fang.cosmos.definition.singleCosmos
import com.fang.cosmos.foundation.log.logD
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import org.koin.core.definition.Definition
import org.koin.core.module.Module

/**
 * Koin [Module] extension for [CoroutineScope] D.I. .
 */
fun Module.cosmosModuleExternalCoroutineScope(
    createdAtStart: Boolean = true,
    override: Definition<CoroutineScope>? = null
) = singleCosmos(createdAtStart) {
    override?.invoke(this, it) ?: createExternalCoroutineScope()
}

private fun createExternalCoroutineScope(): CoroutineScope {
    val exceptionHandler = CoroutineExceptionHandler { _, t ->
        logD("ExternalCoroutineScope", t)
    }
    return CoroutineScope(SupervisorJob() + Dispatchers.Main + exceptionHandler)
}