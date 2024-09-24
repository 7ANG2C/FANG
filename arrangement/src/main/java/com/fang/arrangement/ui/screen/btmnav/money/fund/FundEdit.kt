package com.fang.arrangement.ui.screen.btmnav.money.fund

internal data class FundEdit(
    val id: Long,
    val fund: String?,
    val millis: Long?,
    val remark: String?,
) {
    val savable
        get() = fund != null && millis != null
}
