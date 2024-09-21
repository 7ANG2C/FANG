package com.fang.arrangement.ui.screen.btmnav.statistic.employeeattendance

import com.fang.arrangement.definition.Employee

internal data class YearAttendance(
    val year: Int,
    val summaries: List<Summary>,
) {
    internal data class Summary(
        val employeeId: Long,
        val employee: Employee?,
        val attendance: Double,
    )
}
