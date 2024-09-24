package com.fang.arrangement.definition.sheet

import com.fang.arrangement.definition.AttendanceAll
import com.fang.arrangement.definition.Employee
import com.fang.arrangement.definition.FundHero
import com.fang.arrangement.definition.Loan
import com.fang.arrangement.definition.Site
import com.fang.cosmos.foundation.withcontextcatching.withDefaultCoroutine
import kotlinx.coroutines.runBlocking

internal data class Sheet<T>(
    val id: Int,
    val name: String,
    val keys: List<String>,
    val values: List<T>,
)

internal fun List<WorkSheet>.sheetAttendance() = sheet<AttendanceAll>()

internal fun List<WorkSheet>.sheetLoan() = sheet<Loan>()

internal fun List<WorkSheet>.sheetFundHero() = sheet<FundHero>()

internal fun List<WorkSheet>.sheetEmployee() = sheet<Employee>()

internal fun List<WorkSheet>.sheetSite() = sheet<Site>()

internal inline fun <reified T> List<WorkSheet>.sheet() =
    runBlocking {
        withDefaultCoroutine {
            find { it.clazz == T::class.java }?.let {
                Sheet(
                    id = it.id,
                    name = it.name,
                    keys = it.keys,
                    values = it.values.filterIsInstance<T>(),
                )
            }
        }
    }
