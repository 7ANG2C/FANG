package com.fang.arrangement.ui.screen.btmnav.statistic.employeeattendance

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fang.arrangement.definition.Attendance
import com.fang.arrangement.definition.sheet.SheetRepository
import com.fang.arrangement.definition.sheet.sheetAttendance
import com.fang.arrangement.definition.sheet.sheetEmployee
import com.fang.cosmos.foundation.time.calendar.dayOfMonth
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
internal class EmployeeAttendanceViewModel(
    private val repository: SheetRepository,
) : ViewModel() {
    private sealed class Mediator(
        open val employeeId: Long,
        open val attMillis: Long,
        open val attFactor: Double,
    ) {
        class Full(
            override val employeeId: Long,
            override val attMillis: Long,
        ) : Mediator(employeeId, attMillis, 1.0)

        class Half(
            override val employeeId: Long,
            override val attMillis: Long,
        ) : Mediator(employeeId, attMillis, 0.5)
    }

    private data class FlattenAtt(
        val attMillis: Long,
        val attendance: Attendance,
    )

    private val _yearAttendances = MutableStateFlow(emptyList<YearAttendance>())
    val yearAttendances = _yearAttendances.asStateFlow()

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
                        }.map { (year, yearAttAlls) ->
                            val summaries =
                                yearAttAlls
                                    .asSequence()
                                    .flatMap { yAttAll ->
                                        yAttAll.attendances.map { FlattenAtt(yAttAll.id, it) }
                                    }.flatMap { fAtt ->
                                        fAtt.attendance.fulls.map {
                                            Mediator.Full(
                                                employeeId = it,
                                                attMillis = fAtt.attMillis,
                                            )
                                        } +
                                            fAtt.attendance.halfs.map {
                                                Mediator.Half(
                                                    employeeId = it,
                                                    attMillis = fAtt.attMillis,
                                                )
                                            }
                                    }.groupBy { it.employeeId }
                                    .map { (employeeId, eMediators) ->
                                        YearAttendance.Summary(
                                            employeeId = employeeId,
                                            employee = employees.find { it.id == employeeId },
                                            attendance = eMediators.sumOf { it.attFactor },
                                            months =
                                                eMediators
                                                    .groupBy {
                                                        today(it.attMillis).month
                                                    }.map { (month, values) ->
                                                        YearAttendance.Summary.Month(
                                                            month = month,
                                                            halfDays =
                                                                values.filterIsInstance<Mediator.Half>().map {
                                                                    today(it.attMillis).dayOfMonth
                                                                },
                                                            fullDays =
                                                                values.filterIsInstance<Mediator.Full>().map {
                                                                    today(it.attMillis).dayOfMonth
                                                                },
                                                        )
                                                    },
                                        )
                                    }.sortedWith(
                                        compareByDescending<YearAttendance.Summary> { it.attendance }
                                            .thenByDescending { it.employeeId },
                                    ).toList()
                            YearAttendance(year, summaries)
                        }
                }.flowOn(Dispatchers.Default)
                .collectLatest { data ->
                    _yearAttendances.value = data
                }
        }
    }
}
