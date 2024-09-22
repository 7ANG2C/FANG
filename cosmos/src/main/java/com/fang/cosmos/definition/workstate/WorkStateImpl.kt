package com.fang.cosmos.definition.workstate

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class WorkStateImpl : WorkState {
    private val _refreshState = MutableStateFlow(false)
    override val refreshState = _refreshState.asStateFlow()

    private val _loadingState = MutableStateFlow(false)
    override val loadingState = _loadingState.asStateFlow()

    private val _throwableState = MutableStateFlow<Throwable?>(null)
    override val throwableState = _throwableState.asStateFlow()

    override fun refreshing() {
        _refreshState.value = true
    }

    override fun noRefreshing() {
        _refreshState.value = false
    }

    override fun loading() {
        _loadingState.value = true
    }

    override fun noLoading() {
        _loadingState.value = false
    }

    override fun throwable(t: Throwable) {
        _throwableState.value = t
    }

    override fun noThrowable() {
        _throwableState.value = null
    }
}
