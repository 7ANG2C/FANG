package com.fang.arrangement.ui.screen.btmnav.statistic.employeeloan

import com.fang.arrangement.definition.Employee

internal data class EmployeeLoan(
    val employeeId: Long,
    val employee: Employee?,
    val loans: List<YMLoan>,
) {
    internal data class YMLoan(
        val year: Int,
        val month: Int,
        val loan: Int,
    )

    val loan get() = loans.sumOf { it.loan }.takeIf { it > 0 }
}
