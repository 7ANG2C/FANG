package com.fang.arrangement.ui.screen.btmnav.employee

import com.fang.arrangement.definition.Employee
import com.fang.cosmos.foundation.mapNoNull
import com.fang.cosmos.foundation.takeIfNotBlank

internal data class EmployeeEditBundle(
    val current: Employee?,
    val edit: EmployeeEdit,
) {
    val isInsert get() = current == null
    val anyDiff
        get() =
            current?.name?.trim().takeIfNotBlank != edit.name?.trim().takeIfNotBlank ||
                current?.salaries.orEmpty().map { it.millis to it.salary }.toString() !=
                edit.salaries.mapNoNull({
                    it.millis != null && it.salary != null
                }) { it.millis to it.salary }.toString() ||
                current?.expiredMillis != edit.expire
}
