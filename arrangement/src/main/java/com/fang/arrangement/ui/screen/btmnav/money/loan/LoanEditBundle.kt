package com.fang.arrangement.ui.screen.btmnav.money.loan

import com.fang.cosmos.foundation.mapNoNull
import com.fang.cosmos.foundation.takeIfNotBlank

internal data class LoanEditBundle(
    val current: MLoan?,
    val edit: LoanEdit,
) {
    val isInsert get() = current == null
    val anyDiff
        get() =
            current?.employee?.id != edit.employee?.id ||
                current?.loan != edit.loan?.toIntOrNull() ||
                current?.millis != edit.millis ||
                current?.remark?.trim().takeIfNotBlank != edit.remark?.trim().takeIfNotBlank ||
                current
                    ?.records
                    .orEmpty()
                    .map { it.millis to it.loan to it.remark?.trim().takeIfNotBlank }
                    .toString() !=
                edit.records
                    .mapNoNull({
                        it.millis != null && it.loan != null
                    }) {
                        it.millis to it.loan to it.remark?.trim().takeIfNotBlank
                    }.toString()
}
