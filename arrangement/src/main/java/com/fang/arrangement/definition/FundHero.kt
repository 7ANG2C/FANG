package com.fang.arrangement.definition

import com.fang.arrangement.definition.foundation.KeyValue
import com.google.gson.annotations.SerializedName

internal data class FundHero(
    @SerializedName(FundHeroKey.ID)
    val id: Long,
    @SerializedName(FundHeroKey.EMPLOYEE)
    val employeeId: Long,
    @SerializedName(FundHeroKey.FUND)
    val fund: Int,
    @SerializedName(FundHeroKey.MILLIS)
    val millis: Long,
    @SerializedName(FundHeroKey.REMARK)
    val remark: String?,
)

internal interface FundHeroKey {
    companion object {
        const val ID = "id"
        const val EMPLOYEE = "employee"
        const val FUND = "fund"
        const val MILLIS = "millis"
        const val REMARK = "remark"

        fun fold(
            id: String,
            employeeId: String,
            fund: String,
            millis: String,
            remark: String,
        ) = listOf(
            KeyValue(ID, id),
            KeyValue(EMPLOYEE, employeeId),
            KeyValue(FUND, fund),
            KeyValue(MILLIS, millis),
            KeyValue(REMARK, remark),
        )
    }
}
