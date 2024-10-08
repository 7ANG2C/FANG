package com.fang.arrangement.ui.screen.btmnav.money.fund

import com.fang.cosmos.foundation.takeIfNotBlank

internal data class FundEditBundle(
    val current: MFund?,
    val edit: FundEdit,
) {
    val isInsert get() = current == null
    val anyDiff
        get() =
            current?.fund?.toString() != edit.fund ||
                current?.millis != edit.millis ||
                current?.remark?.trim().takeIfNotBlank != edit.remark?.trim().takeIfNotBlank
}
