package com.fang.arrangement.definition.employee

import com.google.gson.annotations.SerializedName

internal data class Employee(
    @SerializedName("employeeId")
    val id: Long,
    @SerializedName("employeeName")
    val name: String,
    @SerializedName("employeeName")
    val salarys: List<Salary>,
)

internal data class Salary(
    val salary: Int,
    val date: Long
)