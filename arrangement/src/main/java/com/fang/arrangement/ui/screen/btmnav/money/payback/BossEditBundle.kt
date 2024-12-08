package com.fang.arrangement.ui.screen.btmnav.money.payback

import com.fang.arrangement.definition.Boss
import com.fang.cosmos.foundation.takeIfNotBlank

internal data class BossEditBundle(
    val current: Boss?,
    val edit: BossEdit,
) {
    val isInsert get() = current == null
    val anyDiff
        get() =
            current?.name?.trim().takeIfNotBlank != edit.name?.trim().takeIfNotBlank
}
