package com.fang.arrangement.ui.screen.btmnav.statistic.pdf

internal data class PDFRequest(
    val startMillis: Long?,
    val endMillis: Long?,
    val includeRemark: Boolean,
) {
    companion object {
        val default = PDFRequest(null, null, false)
    }

    val downloadable get() = startMillis != default.startMillis && endMillis != default.endMillis
}
