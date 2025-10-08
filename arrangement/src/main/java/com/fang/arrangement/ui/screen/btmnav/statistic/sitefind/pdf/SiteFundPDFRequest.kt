package com.fang.arrangement.ui.screen.btmnav.statistic.sitefind.pdf

import com.fang.arrangement.ui.screen.btmnav.statistic.sitefind.SiteFund
import com.fang.arrangement.ui.screen.btmnav.statistic.sitefind.YearMonthFund

internal data class SiteFundPDFRequest(
    val siteId: Long,
    val siteName: String,
    val startMillis: Long?,
    val endMillis: Long?,
    val ymFunds: List<SiteFund>,
) {
    companion object {
        val default by lazy {
            SiteFundPDFRequest(0, "", null, null, emptyList())
        }
    }
}
