package com.fang.arrangement.definition

import com.fang.arrangement.definition.foundation.KeyValue
import com.google.gson.annotations.SerializedName

internal data class Fund(
    @SerializedName(FundKey.ID)
    val id: Long,
    @SerializedName(FundKey.FUND)
    val fund: Int,
    @SerializedName(FundKey.MILLIS)
    val millis: Long,
    @SerializedName(FundKey.REMARK)
    val remark: String?,
)

internal interface FundKey {
    companion object {
        const val ID = "id"
        const val FUND = "fund"
        const val MILLIS = "millis"
        const val REMARK = "remark"

        fun fold(
            id: String,
            fund: String,
            millis: String,
            remark: String,
        ) = listOf(
            KeyValue(ID, id),
            KeyValue(FUND, fund),
            KeyValue(MILLIS, millis),
            KeyValue(REMARK, remark),
        )
    }
}
