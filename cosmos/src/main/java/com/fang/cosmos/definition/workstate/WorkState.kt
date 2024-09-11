package com.fang.cosmos.definition.workstate

import kotlinx.coroutines.flow.StateFlow

interface WorkState {
    // pull refresh
    val refreshState: StateFlow<Boolean>

    // loading dialog
    val loadingState: StateFlow<Boolean>

    // when throw exception
    val throwableState: StateFlow<Throwable?>

    fun refreshing() {}

    fun noRefreshing() {}

    fun loading() {}

    fun noLoading() {}

    fun throwable(t: Throwable) {}

    fun noThrowable() {}
}
