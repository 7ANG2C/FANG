package com.fang.arrangement.definition.sheet

import com.fang.cosmos.foundation.withcontextcatching.withDefaultCoroutine
import kotlinx.coroutines.runBlocking

internal data class Sheet<T>(
    val id: Int,
    val name: String,
    val keys: List<String>,
    val values: List<T>,
)

internal inline fun <reified T> List<WorkSheet>.sheet() =
    runBlocking {
        withDefaultCoroutine {
            find { it.clazz == T::class.java }?.let {
                Sheet(
                    id = it.id,
                    name = it.name,
                    keys = it.keys,
                    values = it.values.filterIsInstance<T>(),
                )
            }
        }
    }
