package com.fang.arrangement.di

import com.fang.arrangement.definition.attendancesummary.AttendanceSummaryRepository
import com.fang.arrangement.definition.building.BuildingRepository
import com.fang.arrangement.definition.employee.EmployeeRepository
import com.fang.arrangement.definition.loan.LoanRepository
import com.fang.cosmos.definition.CosmosDef
import org.koin.dsl.module

internal object CoreModule {
    operator fun invoke() = module {
        single {
            AttendanceSummaryRepository(
                dataStore = CosmosDef.NonQualifiedDataStore,
                scope = CosmosDef.CoroutineScope,
            )
        }
        single {
            BuildingRepository(
                dataStore = CosmosDef.NonQualifiedDataStore,
                scope = CosmosDef.CoroutineScope,
            )
        }
        single {
            EmployeeRepository(
                dataStore = CosmosDef.NonQualifiedDataStore,
                scope = CosmosDef.CoroutineScope,
            )
        }
        single {
            LoanRepository(
                dataStore = CosmosDef.NonQualifiedDataStore,
                scope = CosmosDef.CoroutineScope,
            )
        }
    }
}
