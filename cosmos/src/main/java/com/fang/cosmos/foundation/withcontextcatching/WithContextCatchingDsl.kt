package com.fang.cosmos.foundation.withcontextcatching

import kotlinx.coroutines.CoroutineScope

suspend fun <T> withMainCoroutineCatching(
    block: suspend CoroutineScope.() -> T
) = withMainCoroutine {
    kotlin.runCatching { block() }
}

suspend fun <T> withUnconfinedCoroutineCatching(
    block: suspend CoroutineScope.() -> T
) = withUnconfinedCoroutine {
    kotlin.runCatching { block() }
}

suspend fun <T> withDefaultCoroutineCatching(
    block: suspend CoroutineScope.() -> T
) = withDefaultCoroutine {
    kotlin.runCatching { block() }
}

suspend fun <T> withIoCoroutineCatching(
    block: suspend CoroutineScope.() -> T
) = withIoCoroutine {
    kotlin.runCatching { block() }
}
