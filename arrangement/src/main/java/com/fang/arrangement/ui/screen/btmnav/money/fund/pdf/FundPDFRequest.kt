package com.fang.arrangement.ui.screen.btmnav.money.fund.pdf

internal data class FundPDFRequest(
    val startMillis: Long?,
    val endMillis: Long?,
) {
    companion object {
        val default by lazy { FundPDFRequest(null, null) }
    }

    val downloadable get() = startMillis != default.startMillis && endMillis != default.endMillis
}
