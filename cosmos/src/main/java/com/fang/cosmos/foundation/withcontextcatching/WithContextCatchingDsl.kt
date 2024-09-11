package com.fang.cosmos.foundation.withcontextcatching

import kotlinx.coroutines.CoroutineScope

suspend fun <T> mainCatching(block: suspend CoroutineScope.() -> T) =
    withMainCoroutine {
        kotlin.runCatching { block() }
    }

suspend fun <T> unconfinedCatching(block: suspend CoroutineScope.() -> T) =
    withUnconfinedCoroutine {
        kotlin.runCatching { block() }
    }

suspend fun <T> defaultCatching(block: suspend CoroutineScope.() -> T) =
    withDefaultCoroutine {
        kotlin.runCatching { block() }
    }

suspend fun <T> ioCatching(block: suspend CoroutineScope.() -> T) =
    withIoCoroutine {
        kotlin.runCatching { block() }
    }
