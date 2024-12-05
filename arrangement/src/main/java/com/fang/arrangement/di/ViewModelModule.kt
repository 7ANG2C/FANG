package com.fang.arrangement.di

import com.fang.arrangement.ui.screen.ArrangementViewModel
import com.fang.arrangement.ui.screen.btmnav.attendance.AttendanceViewModel
import com.fang.arrangement.ui.screen.btmnav.employee.EmployeeViewModel
import com.fang.arrangement.ui.screen.btmnav.money.MoneyViewModel
import com.fang.arrangement.ui.screen.btmnav.money.fund.FundViewModel
import com.fang.arrangement.ui.screen.btmnav.money.fund.pdf.FundPDFViewModel
import com.fang.arrangement.ui.screen.btmnav.money.loan.LoanViewModel
import com.fang.arrangement.ui.screen.btmnav.site.SiteViewModel
import com.fang.arrangement.ui.screen.btmnav.statistic.StatisticViewModel
import com.fang.arrangement.ui.screen.btmnav.statistic.employeeattendance.EmployeeAttendanceViewModel
import com.fang.arrangement.ui.screen.btmnav.statistic.employeeloan.EmployeeLoanViewModel
import com.fang.arrangement.ui.screen.btmnav.statistic.pdf.PDFViewModel
import com.fang.arrangement.ui.screen.btmnav.statistic.salary.SalaryViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

internal object ViewModelModule {
    operator fun invoke() =
        module {
            viewModelOf(::ArrangementViewModel)
            viewModelOf(::AttendanceViewModel)
            viewModelOf(::MoneyViewModel)
            viewModelOf(::LoanViewModel)
            viewModelOf(::FundViewModel)
            viewModelOf(::FundPDFViewModel)
            viewModelOf(::StatisticViewModel)
            viewModelOf(::SalaryViewModel)
            viewModelOf(::PDFViewModel)
            viewModelOf(::EmployeeAttendanceViewModel)
            viewModelOf(::EmployeeLoanViewModel)
            viewModelOf(::EmployeeViewModel)
            viewModelOf(::SiteViewModel)
        }
}
