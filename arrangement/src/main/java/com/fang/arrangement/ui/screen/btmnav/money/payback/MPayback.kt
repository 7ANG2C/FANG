package com.fang.arrangement.ui.screen.btmnav.money.payback

import com.fang.arrangement.definition.Boss
import com.fang.arrangement.definition.PaybackRecord

internal data class MPayback(
    val id: Long,
    val boss: Boss,
    val payback: Int,
    val millis: Long,
    val records: List<PaybackRecord>,
    val remark: String?,
) {
    val remain get() = payback - records.sumOf { it.payback }
    val isClear get() = remain <= 0
}
