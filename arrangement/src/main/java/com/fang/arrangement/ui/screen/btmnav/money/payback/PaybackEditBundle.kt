package com.fang.arrangement.ui.screen.btmnav.money.payback

import com.fang.cosmos.foundation.mapNoNull
import com.fang.cosmos.foundation.takeIfNotBlank

internal data class PaybackEditBundle(
    val current: MPayback?,
    val edit: PaybackEdit,
) {
    val isInsert get() = current == null
    val anyDiff
        get() =
            current?.boss?.id != edit.boss?.id ||
                current?.payback != edit.payback?.toIntOrNull() ||
                current?.millis != edit.millis ||
                current?.remark?.trim().takeIfNotBlank != edit.remark?.trim().takeIfNotBlank ||
                current
                    ?.records
                    .orEmpty()
                    .map { it.millis to it.payback to it.remark?.trim().takeIfNotBlank }
                    .toString() !=
                edit.records
                    .mapNoNull({
                        it.millis != null && it.payback != null
                    }) {
                        it.millis to it.payback to it.remark?.trim().takeIfNotBlank
                    }.toString()
}
