package com.fang.arrangement.definition.sheet

import com.fang.arrangement.definition.AttendanceAll
import com.fang.arrangement.definition.Boss
import com.fang.arrangement.definition.Employee
import com.fang.arrangement.definition.Fund
import com.fang.arrangement.definition.Loan
import com.fang.arrangement.definition.Payback
import com.fang.arrangement.definition.Site

internal interface SpreadSheet {
    companion object {
        private val ATTENDANCE = Request("Attendance", AttendanceAll::class.java)
        private val LOAN = Request("Loan", Loan::class.java)
        private val FUND = Request("Fund", Fund::class.java)
        private val PAYBACK = Request("Payback", Payback::class.java)
        private val BOSS = Request("Boss", Boss::class.java)
        private val EMPLOYEE = Request("Employee", Employee::class.java)
        private val SITE = Request("Site", Site::class.java)
        val all by lazy {
            invoke(ATTENDANCE, LOAN, FUND, PAYBACK, BOSS, EMPLOYEE, SITE)
        }

        operator fun invoke(vararg request: Request) = request.toList()

        operator fun invoke(vararg name: String) = all.filter { it.name in name }
    }

    data class Request(
        val name: String,
        val clazz: Class<out Any>,
    )

    data class Property(
        val id: Int,
        val name: String,
    )
}
