package com.fang.cosmos.foundation.withcontextcatching

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

suspend fun <T> withMainCoroutine(
    block: suspend CoroutineScope.() -> T
) = withContext(Dispatchers.Main, block)

suspend fun <T> withUnconfinedCoroutine(
    block: suspend CoroutineScope.() -> T
) = withContext(Dispatchers.Unconfined, block)

suspend fun <T> withDefaultCoroutine(
    block: suspend CoroutineScope.() -> T
) = withContext(Dispatchers.Default, block)

suspend fun <T> withIoCoroutine(
    block: suspend CoroutineScope.() -> T
) = withContext(Dispatchers.IO, block)
