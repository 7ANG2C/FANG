package com.fang.arrangement.ui.shared.dsl

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.fang.arrangement.definition.Employee
import com.fang.arrangement.ui.shared.component.chip.DeletedTag
import com.fang.arrangement.ui.shared.component.chip.ExpiredTag

@Composable
internal fun EmployeeTag(
    employee: Employee?,
    modifier: Modifier = Modifier,
) {
    when {
        employee == null || employee.isDelete -> DeletedTag(modifier)
        employee.isExpire -> ExpiredTag(modifier)
        else -> {}
    }
}

internal fun employeeState(employee: Employee?) =
    when {
        employee == null || employee.isDelete -> true
        employee.isExpire -> true
        else -> false
    }
