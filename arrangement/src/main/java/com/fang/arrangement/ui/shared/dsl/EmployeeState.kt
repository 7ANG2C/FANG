package com.fang.arrangement.ui.shared.dsl

import com.fang.arrangement.definition.Employee

internal interface EmployeeState {
    companion object {
        operator fun invoke(employee: Employee?) =
            when {
                employee == null || employee.isDelete -> "(已刪除)"
                employee.isExpire -> "(已離職)"
                else -> ""
            }
    }
}
