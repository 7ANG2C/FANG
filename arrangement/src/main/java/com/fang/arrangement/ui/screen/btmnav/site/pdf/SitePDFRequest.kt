package com.fang.arrangement.ui.screen.btmnav.site.pdf

internal data class SitePDFRequest(
    val siteId: Long,
    val startMillis: Long?,
    val endMillis: Long?,
    val showSiteName: Boolean,
    val showStartEnd: Boolean,
    val showTotal: Boolean,
    val showEmployeeSummary: Boolean,
    val showDailyEmployee: Boolean,
    val includeRemark: Boolean,
) {
    companion object {
        val default = SitePDFRequest(
            siteId = 0,
            startMillis = null,
            endMillis = null,
            showSiteName = true,
            showStartEnd = true,
            showTotal = true,
            showEmployeeSummary = true,
            showDailyEmployee = true,
            includeRemark = false
        )
    }
}
