package com.fang.arrangement.di

import com.fang.arrangement.ui.screen.ArrangementViewModel
import com.fang.arrangement.ui.screen.btmnav.attendance.AttendanceViewModel
import com.fang.arrangement.ui.screen.btmnav.employee.EmployeeViewModel
import com.fang.arrangement.ui.screen.btmnav.loan.LoanViewModel
import com.fang.arrangement.ui.screen.btmnav.site.SiteViewModel
import com.fang.arrangement.ui.screen.btmnav.statistic.StatisticViewModel
import com.fang.arrangement.ui.screen.btmnav.statistic.employee.StatisticEmployeeViewModel
import com.fang.arrangement.ui.screen.btmnav.statistic.salary.SalaryViewModel
import org.koin.androidx.viewmodel.dsl.viewModelOf
import org.koin.dsl.module

internal object ViewModelModule {
    operator fun invoke() =
        module {
            viewModelOf(::ArrangementViewModel)
            viewModelOf(::AttendanceViewModel)
            viewModelOf(::LoanViewModel)
            viewModelOf(::StatisticViewModel)
            viewModelOf(::SalaryViewModel)
            viewModelOf(::StatisticEmployeeViewModel)
            viewModelOf(::EmployeeViewModel)
            viewModelOf(::SiteViewModel)
        }
}
