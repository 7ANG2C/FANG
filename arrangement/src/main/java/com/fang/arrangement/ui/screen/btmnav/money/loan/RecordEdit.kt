package com.fang.arrangement.ui.screen.btmnav.money.loan

internal data class RecordEdit(
    val millis: Long?,
    val loan: String?,
    val remark: String?,
) {
    companion object {
        val empty by lazy { RecordEdit(null, null, null) }
    }

    val allBlank get() = this == empty
    val allFilled get() = millis != empty.millis && loan != empty.loan
}
