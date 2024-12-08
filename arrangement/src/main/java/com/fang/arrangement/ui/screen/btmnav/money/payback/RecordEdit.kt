package com.fang.arrangement.ui.screen.btmnav.money.payback

import com.fang.arrangement.definition.PaybackKey
import com.google.gson.annotations.SerializedName

internal data class RecordEdit(
    @SerializedName(PaybackKey.R_MILLIS)
    val millis: Long?,
    @SerializedName(PaybackKey.R_PAYBACK)
    val payback: String?,
    @SerializedName(PaybackKey.R_REMARK)
    val remark: String?,
) {
    companion object {
        val empty by lazy { RecordEdit(null, null, null) }
    }

    val allBlank get() = this == empty
    val allFilled get() = millis != empty.millis && payback != empty.payback
}
