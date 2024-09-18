package com.fang.arrangement.ui.screen.btmnav.site

import com.fang.cosmos.foundation.takeIfNotBlank

internal data class SiteEdit(
    val id: Long,
    val name: String?,
    val address: String?,
    val income: String?,
    val startMillis: Long?,
    val endMillis: Long?,
    val archive: Boolean,
) {
    val valid get() = name.takeIfNotBlank != null
}
