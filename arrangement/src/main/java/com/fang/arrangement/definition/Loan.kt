package com.fang.arrangement.definition

internal data class Loan(
    val id: Long,
    val employeeId: Long,
    val loan: Int,
    val millis: Long,
    val records: List<Record>,
    val remark: String?,
) {
    val remain get() = loan - records.sumOf { it.loan }
}

internal data class Record(
    val millis: Long,
    val loan: Int,
    val remark: String?,
)
