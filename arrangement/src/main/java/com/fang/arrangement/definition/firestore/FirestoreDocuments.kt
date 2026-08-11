package com.fang.arrangement.definition.firestore

import com.fang.arrangement.definition.Attendance
import com.fang.arrangement.definition.AttendanceAll
import com.fang.arrangement.definition.Boss
import com.fang.arrangement.definition.Employee
import com.fang.arrangement.definition.Fund
import com.fang.arrangement.definition.Loan
import com.fang.arrangement.definition.Payback
import com.fang.arrangement.definition.PaybackRecord
import com.fang.arrangement.definition.Record
import com.fang.arrangement.definition.Salary
import com.fang.arrangement.definition.Site

/**
 * Mutable Firestore-only shapes. Their exact property names mirror document
 * fields, and their defaults make absent historical fields safe to read.
 */
internal data class FsAttendanceAll(
    var millis: Long = 0L,
    var attendances: List<FsAttendance> = emptyList(),
)

internal data class FsAttendance(
    var id: Long = 0L,
    var full: List<Long> = emptyList(),
    var half: List<Long> = emptyList(),
    var remark: String? = null,
)

internal data class FsEmployee(
    var id: Long = 0L,
    var name: String = "",
    var salaries: List<FsSalary> = emptyList(),
    var expired: Long? = null,
    var delete: Int = 0,
    var order: Int = 0,
)

internal data class FsSalary(
    var millis: Long = 0L,
    var salary: Int = 0,
)

internal data class FsLoan(
    var id: Long = 0L,
    var employee: Long = 0L,
    var loan: Int = 0,
    var millis: Long = 0L,
    var records: List<FsLoanRecord> = emptyList(),
    var remark: String? = null,
)

internal data class FsLoanRecord(
    var millis: Long = 0L,
    var loan: Int = 0,
    var remark: String? = null,
)

internal data class FsFund(
    var id: Long = 0L,
    var fund: Int = 0,
    var millis: Long = 0L,
    var site: Long? = null,
    var remark: String? = null,
)

internal data class FsPayback(
    var id: Long = 0L,
    var boss: Long = 0L,
    var loan: Int = 0,
    var millis: Long = 0L,
    var records: List<FsPaybackRecord> = emptyList(),
    var remark: String? = null,
)

internal data class FsPaybackRecord(
    var millis: Long = 0L,
    var loan: Int = 0,
    var remark: String? = null,
)

internal data class FsBoss(
    var id: Long = 0L,
    var name: String = "",
    var delete: Int = 0,
)

internal data class FsSite(
    var id: Long = 0L,
    var name: String = "",
    var address: String? = null,
    var income: Int? = null,
    var start: Long? = null,
    var end: Long? = null,
    var archive: Int = 0,
    var delete: Int = 0,
)

internal fun FsAttendanceAll.toDomain() =
    AttendanceAll(
        id = millis,
        attendances = attendances.map(FsAttendance::toDomain),
    )

internal fun AttendanceAll.toFirestore() =
    FsAttendanceAll(
        millis = id,
        attendances = attendances.map(Attendance::toFirestore),
    )

internal fun FsAttendance.toDomain() = Attendance(id, full, half, remark)

internal fun Attendance.toFirestore() = FsAttendance(siteId, fulls, halfs, remark)

internal fun FsEmployee.toDomain() = Employee(id, name, salaries.map(FsSalary::toDomain), expired, delete, order)

internal fun Employee.toFirestore() = FsEmployee(id, name, salaries.map(Salary::toFirestore), expiredMillis, delete, order)

internal fun FsSalary.toDomain() = Salary(millis, salary)

internal fun Salary.toFirestore() = FsSalary(millis, salary)

internal fun FsLoan.toDomain() = Loan(id, employee, loan, millis, records.map(FsLoanRecord::toDomain), remark)

internal fun Loan.toFirestore() = FsLoan(id, employeeId, loan, millis, records.map(Record::toFirestore), remark)

internal fun FsLoanRecord.toDomain() = Record(millis, loan, remark)

internal fun Record.toFirestore() = FsLoanRecord(millis, loan, remark)

internal fun FsFund.toDomain() = Fund(id, fund, millis, site, remark)

internal fun Fund.toFirestore() = FsFund(id, fund, millis, siteId, remark)

internal fun FsPayback.toDomain() = Payback(id, boss, loan, millis, records.map(FsPaybackRecord::toDomain), remark)

internal fun Payback.toFirestore() = FsPayback(id, bossId, payback, millis, records.map(PaybackRecord::toFirestore), remark)

internal fun FsPaybackRecord.toDomain() = PaybackRecord(millis, loan, remark)

internal fun PaybackRecord.toFirestore() = FsPaybackRecord(millis, payback, remark)

internal fun FsBoss.toDomain() = Boss(id, name, delete)

internal fun Boss.toFirestore() = FsBoss(id, name, delete)

internal fun FsSite.toDomain() = Site(id, name, address, income, start, end, archive, delete)

internal fun Site.toFirestore() = FsSite(id, name, address, income, startMillis, endMillis, archive, delete)
