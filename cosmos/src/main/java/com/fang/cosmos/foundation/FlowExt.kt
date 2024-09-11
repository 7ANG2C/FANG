package com.fang.cosmos.foundation

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.conflate
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.retryWhen
import kotlinx.coroutines.flow.stateIn
import kotlin.math.pow
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

/**
 * @see <a href="https://bladecoder.medium.com/kotlins-flow-in-viewmodels-it-s-complicated-556b472e281a#:~:text=values%20is%20avoided.-,So%20far%20so%20good.,-However%2C%20here%20comes">re-emit</a>
 */
fun <T> Flow<T>.stateInSubscribed(
    scope: CoroutineScope,
    initialValue: T,
    stopTimeoutMillis: Long = 5_000,
) = stateIn(scope, SharingStarted.WhileSubscribed(stopTimeoutMillis), initialValue)

/**
 * Flow version of RxJava throttleLatest Operator
 */
fun <T> Flow<T>.throttleLatest(duration: Duration) =
    flow {
        conflate().collect {
            emit(it)
            delay(duration)
        }
    }.flowOn(Dispatchers.Default)

fun <T> Flow<T>.catchLog(
    tag: String,
    action: suspend FlowCollector<T>.(Throwable) -> Unit = {},
) = catch {
    logD(tag, it)
    action(it)
}

/**
 * @see <a href="https://harish-bhattbhatt.medium.com/best-practices-for-retry-pattern-f29d47cd5117#:~:text=Always%2C%20always%20use%20exponential%20back%2Doff%20for%20retries.">Exponential</a>
 */
fun <T> Flow<T>.retryExponentialWhen(predicate: suspend FlowCollector<T>.(cause: Throwable, attempt: Long) -> Boolean) =
    retryWhen { cause, attempt ->
        val seed = (1..3).random().toDouble()
        delay((seed + 2.0.pow(attempt.toDouble())).seconds)
        predicate(cause, attempt)
    }.flowOn(Dispatchers.Default)
