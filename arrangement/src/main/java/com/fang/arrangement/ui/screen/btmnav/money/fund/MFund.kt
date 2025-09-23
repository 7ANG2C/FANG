package com.fang.arrangement.ui.screen.btmnav.money.fund

import com.fang.arrangement.definition.Site

internal data class MFund(
    val selected: Boolean,
    val id: Long,
    val fund: Int,
    val millis: Long,
    val site: Site?,
    val remark: String?,
)
