package com.fang.cosmos.foundation

import com.fang.cosmos.definition.CosmosDef
import com.fang.cosmos.definition.networkavailability.NetworkAvailability
import com.fang.cosmos.foundation.withcontextcatching.withDefaultCoroutine
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.mapLatest
import kotlin.math.pow
import kotlin.time.Duration
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Duration.Companion.seconds

interface RequestConst {
    companion object {
        /**
         * http timeout duration
         */
        val HTTP_TIMEOUT = 30.seconds

        /**
         * 等待網路恢復的最大時長
         */
        val NETWORK_RESUMING = 1.minutes

        /**
         * 如果遇到 錯誤 的重新嘗試次數
         */
        const val RETRY_TIME = 2
    }
}

/**
 * 重試 n 次後仍失敗
 */
class ApiRetryException(throwable: Throwable?) : Throwable(throwable)

/**
 * 無網路
 */
class ConnectionException : Throwable()

suspend fun <D> retry(
    networkResuming: Duration = RequestConst.NETWORK_RESUMING,
    retryTime: Int = RequestConst.RETRY_TIME,
    predicate: suspend () -> Result<D>,
): Result<D> =
    if (networkResuming.isPositive()) {
        networkRetry {
            if (retryTime > 0) {
                apiRetry(retryTime, predicate)
            } else {
                predicate()
            }
        }
    } else if (retryTime > 0) {
        apiRetry(retryTime, predicate)
    } else {
        predicate()
    }

/**
 * retry when call api timeout
 */
private suspend fun <D> apiRetry(
    retryTime: Int = RequestConst.RETRY_TIME,
    predicate: suspend () -> Result<D>,
): Result<D> =
    withDefaultCoroutine {
        require(retryTime >= 0)
        val firstTake = 1
        val allTime = retryTime + firstTake
        for (attempt in 0 until allTime) {
            if (attempt != 0) delay(2.0.pow(attempt.toDouble()).seconds)
            val result = predicate()
            when (val t = result.exceptionOrNull()) {
                null -> return@withDefaultCoroutine result
                else ->
                    if (attempt == allTime - 1) {
                        return@withDefaultCoroutine Result.failure(t)
                    }
            }
        }
        Result.failure(ApiRetryException(null))
    }

/**
 * retry when network lost
 */
@OptIn(ExperimentalCoroutinesApi::class)
private suspend fun <D> networkRetry(
    networkResuming: Duration = RequestConst.NETWORK_RESUMING,
    networkAvailability: NetworkAvailability = CosmosDef.NetworkAvailability,
    predicate: suspend () -> Result<D>,
): Result<D> =
    withDefaultCoroutine {
        networkAvailability
            .availableState.mapLatest { available ->
                if (available) {
                    predicate()
                } else {
                    delay(networkResuming)
                    Result.failure(ConnectionException())
                }
            }.first()
    }
