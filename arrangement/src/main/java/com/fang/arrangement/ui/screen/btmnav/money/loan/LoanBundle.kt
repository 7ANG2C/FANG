package com.fang.arrangement.ui.screen.btmnav.money.loan

import com.fang.arrangement.definition.Employee

internal data class LoanBundle(
    val employees: List<Employee>,
    val loans: List<MLoan>,
)
