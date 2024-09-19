package com.fang.arrangement.ui.screen.btmnav.loan

import com.fang.arrangement.definition.Employee

internal data class LoanEdit(
    val id: Long,
    val employee: Employee?,
    val loan: String?,
    val millis: Long?,
    val records: List<RecordEdit>,
    val remark: String?,
) {
    val savable get() =
        employee != null && loan != null && millis != null &&
            loan.toIntOrNull()?.let { loan ->
                records.sumOf { it.loan?.toIntOrNull() ?: 0 } <= loan
            } ?: false
}
