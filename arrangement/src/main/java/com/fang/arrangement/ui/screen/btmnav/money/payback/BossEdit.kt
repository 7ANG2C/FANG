package com.fang.arrangement.ui.screen.btmnav.money.payback

import com.fang.cosmos.foundation.takeIfNotBlank

internal data class BossEdit(
    val id: Long,
    val name: String?,
) {
    val savable get() = name.takeIfNotBlank != null
}
