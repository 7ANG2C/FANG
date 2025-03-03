package com.fang.arrangement.ui.screen.btmnav.statistic.salary

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fang.arrangement.definition.sheet.SheetRepository
import com.fang.arrangement.definition.sheet.sheetAttendance
import com.fang.arrangement.definition.sheet.sheetEmployee
import com.fang.cosmos.foundation.time.calendar.month
import com.fang.cosmos.foundation.time.calendar.today
import com.fang.cosmos.foundation.time.calendar.year
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.launch

@OptIn(ExperimentalCoroutinesApi::class)
internal class SalaryViewModel(
    private val repository: SheetRepository,
) : ViewModel() {
    private data class Mediator(
        val employeeId: Long,
        val attMillis: Long,
        val attFactor: Double,
    )

    private val _yearSalaries = MutableStateFlow(emptyList<YearSalary>())
    val yearSalaries = _yearSalaries.asStateFlow()

    init {
        viewModelScope.launch {
            repository
                .workSheets
                .mapLatest { workSheets ->
                    val employees = workSheets?.sheetEmployee()?.values.orEmpty()
                    workSheets
                        ?.sheetAttendance()
                        ?.values
                        .orEmpty()
                        .sortedByDescending { it.id }
                        .groupBy {
                            today().apply { timeInMillis = it.id }.year
                        }.map { (year, yearAtts) ->
                            val months =
                                yearAtts
                                    .groupBy {
                                        today().apply { timeInMillis = it.id }.month
                                    }.map { (month, monthAtts) ->
                                        val employeeSalaries =
                                            monthAtts
                                                .flatMap { monthAtt ->
                                                    monthAtt.attendances.flatMap { att ->
                                                        att.fulls.map {
                                                            Mediator(
                                                                employeeId = it,
                                                                attMillis = monthAtt.id,
                                                                attFactor = 1.0,
                                                            )
                                                        } +
                                                            att.halfs.map {
                                                                Mediator(
                                                                    employeeId = it,
                                                                    attMillis = monthAtt.id,
                                                                    attFactor = 0.5,
                                                                )
                                                            }
                                                    }
                                                }.groupBy { it.employeeId }
                                                .map { (employeeId, mediators) ->
                                                    val employee = employees.find { it.id == employeeId }
                                                    val salaries = employee?.salaries.orEmpty()
                                                    val salary =
                                                        mediators
                                                            .mapNotNull { m ->
                                                                salaries
                                                                    .find {
                                                                        m.attMillis >= it.millis
                                                                    }?.salary
                                                                    ?.let {
                                                                        it to m.attFactor
                                                                    }
                                                            }.takeIf { it.isNotEmpty() }
                                                            ?.sumOf {
                                                                it.first * it.second
                                                            }
                                                    EmployeeSalary(
                                                        employeeId = employeeId,
                                                        employee = employee,
                                                        salary = salary,
                                                        attendance = mediators.sumOf { it.attFactor },
                                                    )
                                                }.sortedWith(
                                                    compareBy<EmployeeSalary>(
                                                        { it.employee == null },
                                                        { it.employee?.isDelete == true },
                                                        { it.employee?.isExpire == true },
                                                    ).thenByDescending { it.employee?.id },
                                                )
                                        MonthSalary(
                                            month = month,
                                            attendance = employeeSalaries.sumOf { it.attendance },
                                            salary =
                                                employeeSalaries
                                                    .mapNotNull { it.salary }
                                                    .takeIf { it.isNotEmpty() }
                                                    ?.sum(),
                                            employeeSalaries = employeeSalaries,
                                        )
                                    }
                            YearSalary(
                                year = year,
                                attendance = months.sumOf { it.attendance },
                                salary =
                                    months
                                        .mapNotNull { it.salary }
                                        .takeIf { it.isNotEmpty() }
                                        ?.sum(),
                                months = months,
                            )
                        }
                }.flowOn(Dispatchers.Default)
                .collectLatest { data ->
                    _yearSalaries.value = data
                }
        }
    }
}
