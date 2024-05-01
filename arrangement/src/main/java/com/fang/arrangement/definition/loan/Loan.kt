package com.fang.arrangement.definition.loan

import com.google.gson.annotations.SerializedName

internal data class Loan(
    @SerializedName("loanId")
    val id: Long,
    @SerializedName("employeeAId")
    val employeeAId: Long,
    @SerializedName("employeeBId")
    val employeeBId: Long,
    @SerializedName("loanMillis")
    val loanMillis: Long,
    @SerializedName("money")
    val money: Long,
    @SerializedName("remark")
    val remark: String,
    @SerializedName("records")
    val records: List<Record>
)

internal data class Record(
    @SerializedName("recordId")
    val id: Long,
    @SerializedName("recordMoney")
    val money: Long,
)
