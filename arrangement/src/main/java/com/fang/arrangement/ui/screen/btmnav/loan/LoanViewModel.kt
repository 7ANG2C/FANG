package com.fang.arrangement.ui.screen.btmnav.loan

import androidx.lifecycle.ViewModel
import com.fang.arrangement.definition.loan.LoanRepository

internal class LoanViewModel(
    private val loanRepository: LoanRepository
) : ViewModel() {

    fun add(
        employeeAId: Long,
        employeeBId: Long,
        loanMillis: Long,
        remark: String,
        money: Long,
    ) {
        loanRepository.add(
            employeeAId,
            employeeBId,
            loanMillis,
            remark,
            money,
        )
    }

    fun edit(id: Long, money: Long) {
        loanRepository.edit(
            id,
            money,
        )
    }

    fun delete(id: Long) {
        loanRepository.delete(
            id,
        )
    }


}