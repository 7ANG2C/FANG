package com.fang.arrangement.definition

import com.fang.arrangement.definition.foundation.KeyValue
import com.google.gson.annotations.SerializedName

internal data class Payback(
    @SerializedName(PaybackKey.ID)
    val id: Long,
    @SerializedName(PaybackKey.BOSS)
    val bossId: Long,
    @SerializedName(PaybackKey.PAYBACK)
    val payback: Int,
    @SerializedName(PaybackKey.MILLIS)
    val millis: Long,
    @SerializedName(PaybackKey.RECORDS)
    val records: List<PaybackRecord>,
    @SerializedName(PaybackKey.REMARK)
    val remark: String?,
) {
    val remain get() = payback - records.sumOf { it.payback }
}

internal data class PaybackRecord(
    @SerializedName(PaybackKey.R_MILLIS)
    val millis: Long,
    @SerializedName(PaybackKey.R_PAYBACK)
    val payback: Int,
    @SerializedName(PaybackKey.R_REMARK)
    val remark: String?,
)

internal interface PaybackKey {
    companion object {
        const val ID = "id"
        const val BOSS = "boss"
        const val PAYBACK = "loan"
        const val MILLIS = "millis"
        const val RECORDS = "records"
        const val REMARK = "remark"
        const val R_MILLIS = "millis"
        const val R_PAYBACK = "loan"
        const val R_REMARK = "remark"

        fun fold(
            id: String,
            bossId: String,
            payback: String,
            millis: String,
            records: String,
            remark: String,
        ) = listOf(
            KeyValue(ID, id),
            KeyValue(BOSS, bossId),
            KeyValue(PAYBACK, payback),
            KeyValue(MILLIS, millis),
            KeyValue(RECORDS, records),
            KeyValue(REMARK, remark),
        )
    }
}
