package com.fang.arrangement.ui.screen.btmnav.money.payback

internal data class RecordEdit(
    val millis: Long?,
    val payback: String?,
    val remark: String?,
) {
    companion object {
        val empty by lazy { RecordEdit(null, null, null) }
    }

    val allBlank get() = this == empty
    val allFilled get() = millis != empty.millis && payback != empty.payback
}
