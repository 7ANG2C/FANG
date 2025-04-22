package com.fang.arrangement.ui.shared.dsl

import com.fang.cosmos.foundation.NumberFormat

internal interface AttendanceNumFormat {
    companion object {
        operator fun invoke(
            number: Number?,
            negativePrefix: String = "-",
            positivePrefix: String = "",
            invalidText: String = "",
        ) = NumberFormat(
            number = number,
            decimal =
                if (".5" in number?.toString().orEmpty()) {
                    1
                } else {
                    0
                },
            negativePrefix = negativePrefix,
            positivePrefix = positivePrefix,
        ) ?: invalidText
    }
}
