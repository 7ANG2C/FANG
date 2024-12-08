package com.fang.arrangement.ui.screen.btmnav.money.payback

import com.fang.arrangement.definition.Boss

internal data class PaybackEdit(
    val id: Long,
    val boss: Boss?,
    val payback: String?,
    val millis: Long?,
    val records: List<RecordEdit>,
    val remark: String?,
) {
    val savable
        get() =
            boss != null && payback != null && millis != null &&
                payback.toIntOrNull()?.let { p ->
                    records.sumOf { it.payback?.toIntOrNull() ?: 0 } <= p
                } ?: false
}
