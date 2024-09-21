package com.fang.arrangement.ui.screen.btmnav.employee

import com.fang.arrangement.definition.EmployeeKey
import com.google.gson.annotations.SerializedName

internal data class SalaryEdit(
    @SerializedName(EmployeeKey.S_MILLIS)
    val millis: Long?,
    @SerializedName(EmployeeKey.S_SALARY)
    val salary: String?,
) {
    companion object {
        val empty by lazy { SalaryEdit(null, null) }
    }

    val allBlank get() = this == empty
    val allFilled get() = millis != empty.millis && salary != empty.salary
}
