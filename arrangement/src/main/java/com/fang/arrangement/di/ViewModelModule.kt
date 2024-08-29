package com.fang.arrangement.di

import com.fang.arrangement.ui.screen.btmnav.building.SiteViewModel
import com.fang.arrangement.ui.screen.btmnav.employee.EmployeeViewModel
import com.fang.arrangement.ui.screen.btmnav.loan.LoanViewModel
import com.fang.arrangement.ui.screen.btmnav.schedule.ScheduleViewModel
import com.fang.arrangement.ui.screen.btmnav.statistic.StatisticViewModel
import org.koin.androidx.viewmodel.dsl.viewModelOf
import org.koin.dsl.module

internal object ViewModelModule {
    operator fun invoke() = module {
        viewModelOf(::ScheduleViewModel)
        viewModelOf(::StatisticViewModel)
        viewModelOf(::SiteViewModel)
        viewModelOf(::LoanViewModel)
        viewModelOf(::EmployeeViewModel)
    }
}
