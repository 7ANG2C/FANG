package com.fang.arrangement.ui.screen.btmnav.statistic.salary.pdf

import android.graphics.pdf.PdfDocument

internal data class PdfBundle(
    val pdfDocument: PdfDocument,
    val startMillis: Long,
    val endMillis: Long,
)
