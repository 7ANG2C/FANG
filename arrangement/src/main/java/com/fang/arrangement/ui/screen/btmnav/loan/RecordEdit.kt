package com.fang.arrangement.ui.screen.btmnav.loan

import com.fang.arrangement.definition.LoanKey
import com.google.gson.annotations.SerializedName

internal data class RecordEdit(
    @SerializedName(LoanKey.R_MILLIS)
    val millis: Long?,
    @SerializedName(LoanKey.R_LOAN)
    val loan: String?,
    @SerializedName(LoanKey.R_REMARK)
    val remark: String?,
) {
    companion object {
        val empty by lazy { RecordEdit(null, null, null) }
    }

    val allBlank get() = this == empty
    val allFilled get() = millis != empty.millis && loan != empty.loan
}
