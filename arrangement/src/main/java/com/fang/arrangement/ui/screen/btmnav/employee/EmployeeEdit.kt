package com.fang.arrangement.ui.screen.btmnav.employee

import com.fang.cosmos.foundation.takeIfNotBlank

internal data class EmployeeEdit(
    val id: Long,
    val name: String?,
    val salaries: List<SalaryEdit>,
    val expire: Long?,
) {
    val savable get() = name.takeIfNotBlank != null
}
