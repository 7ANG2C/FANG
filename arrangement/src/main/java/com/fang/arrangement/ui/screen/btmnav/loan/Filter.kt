package com.fang.arrangement.ui.screen.btmnav.loan

internal data class Filter(
    val employeeAId: Long,
    val employeeBId: Long,
    val startMillis: Long,
    val endMillis: Long,
    val moneyStart: Long,
    val moneyEnd: Long,
    val isClean: Boolean
)
