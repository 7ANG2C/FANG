package com.fang.cosmos.foundation.withcontextcatching

import kotlinx.coroutines.CoroutineScope

suspend fun <R> withMainCoroutineCatching(
    block: suspend CoroutineScope.() -> R
) = withMainCoroutine {
    kotlin.runCatching { block() }
}

suspend fun <R> withUnconfinedCoroutineCatching(
    block: suspend CoroutineScope.() -> R
) = withUnconfinedCoroutine {
    kotlin.runCatching { block() }
}

suspend fun <R> withDefaultCoroutineCatching(
    block: suspend CoroutineScope.() -> R
) = withDefaultCoroutine {
    kotlin.runCatching { block() }
}

suspend fun <R> withIoCoroutineCatching(
    block: suspend CoroutineScope.() -> R
) = withIoCoroutine {
    kotlin.runCatching { block() }
}
