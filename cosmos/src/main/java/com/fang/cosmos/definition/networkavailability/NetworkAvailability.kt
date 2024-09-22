package com.fang.cosmos.definition.networkavailability

import kotlinx.coroutines.flow.StateFlow

/**
 * 裝置網路是否可用
 */
interface NetworkAvailability {
    val availableState: StateFlow<Boolean>
}
