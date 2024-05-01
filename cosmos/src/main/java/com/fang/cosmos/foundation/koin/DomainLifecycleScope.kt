package com.fang.cosmos.foundation.koin

import org.koin.core.definition.Definition
import org.koin.core.module.Module
import org.koin.core.qualifier.Qualifier
import org.koin.core.qualifier.named
import org.koin.core.scope.Scope
import org.koin.mp.KoinPlatform.getKoin

/**
 * Declare 指定 [scopeId] 的 [definition] (koin [single])
 */
inline fun <reified T> Module.domainScoped(
    scopeId: String,
    qualifier: Qualifier? = null,
    noinline definition: Definition<T>
) {
    val scopeQualifier = named(scopeId)
    factory<T>(qualifier) {
        getKoin().getOrCreateScope(scopeId, scopeQualifier).get(qualifier)
    }
    scope(scopeQualifier) {
        scoped(qualifier = qualifier, definition = definition)
    }
}

/**
 * 關閉指定 [scopeId] 的 koin [Scope]
 */
fun closeDomainScope(scopeId: String) {
    runCatching {
        getKoin().getScopeOrNull(scopeId)?.close()
    }
}