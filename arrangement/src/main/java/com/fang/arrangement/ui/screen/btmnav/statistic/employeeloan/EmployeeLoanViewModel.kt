package com.fang.arrangement.ui.screen.btmnav.statistic.employeeloan

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fang.arrangement.definition.sheet.SheetRepository
import com.fang.arrangement.definition.sheet.sheetEmployee
import com.fang.arrangement.definition.sheet.sheetLoan
import com.fang.cosmos.foundation.time.calendar.month
import com.fang.cosmos.foundation.time.calendar.today
import com.fang.cosmos.foundation.time.calendar.year
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.launch

@OptIn(ExperimentalCoroutinesApi::class)
internal class EmployeeLoanViewModel(
    sheetRepository: SheetRepository,
) : ViewModel() {
    private val _loans = MutableStateFlow(emptyList<EmployeeLoan>())
    val loans = _loans.asStateFlow()

    init {
        viewModelScope.launch {
            sheetRepository
                .workSheets
                .mapLatest { workSheets ->
                    val employees = workSheets?.sheetEmployee()?.values
                    workSheets?.sheetLoan()?.values?.groupBy {
                        it.employeeId
                    }?.mapNotNull { (employeeId, loans) ->
                        loans.groupBy {
                            val calendar = today(it.millis)
                            calendar.year to calendar.month
                        }.mapNotNull { (ym, ymLoans) ->
                            val loan = ymLoans.sumOf { it.remain }
                            loan.takeIf { it > 0 }?.let {
                                EmployeeLoan.YMLoan(
                                    year = ym.first,
                                    month = ym.second,
                                    loan = loan,
                                )
                            }
                        }.takeIf { it.isNotEmpty() }?.let { ymLoans ->
                            EmployeeLoan(
                                employeeId = employeeId,
                                employee = employees?.find { it.id == employeeId },
                                loans = ymLoans.sortedByDescending { "${it.year}${it.month}" },
                            )
                        }
                    }
                }
                .filterNotNull()
                .flowOn(Dispatchers.Default)
                .collectLatest {
                    _loans.value = it
                }
        }
    }
}
