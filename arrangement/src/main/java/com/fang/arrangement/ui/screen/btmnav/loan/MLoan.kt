package com.fang.arrangement.ui.screen.btmnav.loan

import com.fang.arrangement.definition.Employee
import com.fang.arrangement.definition.Record

internal data class MLoan(
    val id: Long,
    val employee: Employee,
    val loan: Int,
    val millis: Long,
    val records: List<Record>,
    val remark: String?,
) {
    val remain get() = loan - records.sumOf { it.loan }
    val isClear get() = remain <= 0
}
