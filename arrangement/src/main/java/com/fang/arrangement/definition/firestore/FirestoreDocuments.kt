package com.fang.arrangement.definition.firestore

import com.fang.arrangement.definition.Attendance
import com.fang.arrangement.definition.Attendance.Overtime
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
    var overtimes: List<Overtime> = emptyList(),
    var remark: String? = null,
    var images: List<Image> = emptyList(),
) {
    internal data class Overtime(
        val employeeId: Long = 0L,
        val count: Double = 0.0,
    )

    internal data class Image(
        var path: String = "",
        var downloadUrl: String = "",
    )
}

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

internal fun FsAttendanceAll.domain() =
    AttendanceAll(
        id = millis,
        attendances = attendances.map(FsAttendance::domain),
    )

internal fun AttendanceAll.firestore() =
    FsAttendanceAll(
        millis = id,
        attendances = attendances.map(Attendance::firestore),
    )

internal fun FsAttendance.domain() =
    Attendance(
        siteId = id,
        fulls = full,
        halfs = half,
        overtimes = overtimes.map(FsAttendance.Overtime::domain),
        remark = remark,
        images = images.map(FsAttendance.Image::domain),
    )

internal fun Attendance.firestore() =
    FsAttendance(
        id = siteId,
        full = fulls,
        half = halfs,
        overtimes = overtimes.filter { it.count != 0.0 }.map(Overtime::firestore),
        remark = remark,
        images = images.map(Attendance.Image::firestore),
    )

internal fun FsAttendance.Image.domain() = Attendance.Image(path, downloadUrl)

internal fun Attendance.Image.firestore() = FsAttendance.Image(path, downloadUrl)

internal fun Overtime.firestore() = FsAttendance.Overtime(employeeId, count)

internal fun FsAttendance.Overtime.domain() = Overtime(employeeId, count)

internal fun FsEmployee.domain() = Employee(id, name, salaries.map(FsSalary::domain), expired, delete, order)

internal fun Employee.firestore() = FsEmployee(id, name, salaries.map(Salary::firestore), expiredMillis, delete, order)

internal fun FsSalary.domain() = Salary(millis, salary)

internal fun Salary.firestore() = FsSalary(millis, salary)

internal fun FsLoan.domain() = Loan(id, employee, loan, millis, records.map(FsLoanRecord::domain), remark)

internal fun Loan.firestore() = FsLoan(id, employeeId, loan, millis, records.map(Record::firestore), remark)

internal fun FsLoanRecord.domain() = Record(millis, loan, remark)

internal fun Record.firestore() = FsLoanRecord(millis, loan, remark)

internal fun FsFund.domain() = Fund(id, fund, millis, site, remark)

internal fun Fund.firestore() = FsFund(id, fund, millis, siteId, remark)

internal fun FsPayback.domain() = Payback(id, boss, loan, millis, records.map(FsPaybackRecord::domain), remark)

internal fun Payback.firestore() = FsPayback(id, bossId, payback, millis, records.map(PaybackRecord::firestore), remark)

internal fun FsPaybackRecord.domain() = PaybackRecord(millis, loan, remark)

internal fun PaybackRecord.firestore() = FsPaybackRecord(millis, payback, remark)

internal fun FsBoss.domain() = Boss(id, name, delete)

internal fun Boss.firestore() = FsBoss(id, name, delete)

internal fun FsSite.domain() = Site(id, name, address, income, start, end, archive, delete)

internal fun Site.firestore() = FsSite(id, name, address, income, startMillis, endMillis, archive, delete)
