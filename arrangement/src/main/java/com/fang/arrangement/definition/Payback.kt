package com.fang.arrangement.definition

internal data class Payback(
    val id: Long,
    val bossId: Long,
    val payback: Int,
    val millis: Long,
    val records: List<PaybackRecord>,
    val remark: String?,
) {
    val remain get() = payback - records.sumOf { it.payback }
}

internal data class PaybackRecord(
    val millis: Long,
    val payback: Int,
    val remark: String?,
)
