package com.fang.arrangement.definition.sheet

import com.fang.arrangement.definition.AttendanceAll
import com.fang.arrangement.definition.Boss
import com.fang.arrangement.definition.Employee
import com.fang.arrangement.definition.Fund
import com.fang.arrangement.definition.Loan
import com.fang.arrangement.definition.Payback
import com.fang.arrangement.definition.Site
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

internal data class Sheet<T>(
    val id: Int,
    val name: String,
    val keys: List<String>,
    val values: List<T>,
)

internal suspend fun List<WorkSheet>.sheetAttendance() = sheet<AttendanceAll>()

internal suspend fun List<WorkSheet>.sheetLoan() = sheet<Loan>()

internal suspend fun List<WorkSheet>.sheetFund() = sheet<Fund>()

internal suspend fun List<WorkSheet>.sheetPayback() = sheet<Payback>()

internal suspend fun List<WorkSheet>.sheetBoss() = sheet<Boss>()

internal suspend fun List<WorkSheet>.sheetEmployee() = sheet<Employee>()

internal suspend fun List<WorkSheet>.sheetSite() = sheet<Site>()

internal suspend inline fun <reified T> List<WorkSheet>.sheet() =
    withContext(Dispatchers.Default) {
        find { it.clazz == T::class.java }?.let {
            Sheet(
                id = it.id,
                name = it.name,
                keys = it.keys,
                values = it.values.filterIsInstance<T>(),
            )
        }
    }
