package com.fang.arrangement.definition

import com.fang.arrangement.definition.foundation.KeyValue
import com.google.gson.annotations.SerializedName

internal data class Loan(
    @SerializedName(LoanKey.ID)
    val id: Long,
    @SerializedName(LoanKey.EMPLOYEE)
    val employeeId: Long,
    @SerializedName(LoanKey.LOAN)
    val loan: Int,
    @SerializedName(LoanKey.MILLIS)
    val millis: Long,
    @SerializedName(LoanKey.RECORDS)
    val records: List<Record>,
    @SerializedName(LoanKey.REMARK)
    val remark: String?,
) {
    val remain get() = loan - records.sumOf { it.loan }
}

internal data class Record(
    @SerializedName(LoanKey.R_MILLIS)
    val millis: Long,
    @SerializedName(LoanKey.R_LOAN)
    val loan: Int,
    @SerializedName(LoanKey.R_REMARK)
    val remark: String?,
)

internal interface LoanKey {
    companion object {
        const val ID = "id"
        const val EMPLOYEE = "employee"
        const val LOAN = "loan"
        const val MILLIS = "millis"
        const val RECORDS = "records"
        const val REMARK = "remark"
        const val R_MILLIS = "millis"
        const val R_LOAN = "loan"
        const val R_REMARK = "remark"

        fun fold(
            id: String,
            employeeId: String,
            loan: String,
            millis: String,
            records: String,
            remark: String,
        ) = listOf(
            KeyValue(ID, id),
            KeyValue(EMPLOYEE, employeeId),
            KeyValue(LOAN, loan),
            KeyValue(MILLIS, millis),
            KeyValue(RECORDS, records),
            KeyValue(REMARK, remark),
        )
    }
}
