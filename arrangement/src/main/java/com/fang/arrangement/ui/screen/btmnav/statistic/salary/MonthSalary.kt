package com.fang.arrangement.ui.screen.btmnav.statistic.salary

internal data class MonthSalary(
    val month: Int,
    val attendance: Double,
    val salary: Double?,
    val employeeSalaries: List<EmployeeSalary>,
)
