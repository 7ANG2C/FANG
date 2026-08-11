package com.fang.arrangement.ui.screen.btmnav.employee

internal data class SalaryEdit(
    val millis: Long?,
    val salary: String?,
) {
    companion object {
        val empty by lazy { SalaryEdit(null, null) }
    }

    val allBlank get() = this == empty
    val allFilled get() = millis != empty.millis && salary != empty.salary
}
