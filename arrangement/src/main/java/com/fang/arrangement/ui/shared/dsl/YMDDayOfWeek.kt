package com.fang.arrangement.ui.shared.dsl

import com.fang.cosmos.foundation.time.calendar.ChineseDayOfWeek
import com.fang.cosmos.foundation.time.transformer.TimeConverter

internal interface YMDDayOfWeek {
    companion object {
        operator fun invoke(millis: Long?) =
            millis?.let { m ->
                "${TimeConverter.format(m)} ${ChineseDayOfWeek(m)?.let { "($it)" }}"
            }
    }
}
