package com.fang.arrangement.definition.sheet

import com.fang.arrangement.definition.AttendanceAll
import com.fang.arrangement.definition.Employee
import com.fang.arrangement.definition.Loan
import com.fang.arrangement.definition.Site

internal interface SpreadSheet {
    companion object {
        private val SITE = Request("Site", Site::class.java)
        private val ATTENDANCE = Request("Attendance", AttendanceAll::class.java)
        private val LOAN = Request("Loan", Loan::class.java)
        private val EMPLOYEE = Request("Employee", Employee::class.java)
        val all by lazy { invoke(SITE, ATTENDANCE, LOAN, EMPLOYEE) }

        operator fun invoke(vararg request: Request) = request.toList()

        operator fun invoke(vararg name: String) =
            all.mapNotNull {
                if (it.name in name) it else null
            }
    }

    data class Request(val name: String, val clazz: Class<out Any>)

    data class Property(val id: Int, val name: String)
}
