package com.fang.arrangement.ui.screen.btmnav.statistic.salary

import com.fang.arrangement.definition.Employee

internal data class EmployeeSalary(
    val employeeId: Long,
    val employee: Employee?,
    val salary: Double?,
    val attendance: Double,
)
