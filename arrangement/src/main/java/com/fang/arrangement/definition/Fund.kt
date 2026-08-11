package com.fang.arrangement.definition

internal data class Fund(
    val id: Long,
    val fund: Int,
    val millis: Long,
    val siteId: Long?,
    val remark: String?,
)
