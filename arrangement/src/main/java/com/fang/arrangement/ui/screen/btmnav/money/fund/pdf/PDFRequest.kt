package com.fang.arrangement.ui.screen.btmnav.money.fund.pdf

internal data class PDFRequest(
    val startMillis: Long?,
    val endMillis: Long?,
) {
    companion object {
        val default by lazy { PDFRequest(null, null) }
    }

    val downloadable get() = startMillis != default.startMillis && endMillis != default.endMillis
}
