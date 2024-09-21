package com.fang.arrangement.ui.screen.btmnav.statistic.salary

internal data class YearSalary(
    val year: Int,
    val attendance: Double,
    val salary: Double?,
    val months: List<MonthSalary>,
)
