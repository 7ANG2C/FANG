package com.fang.cosmos.foundation.time.calendar

import java.util.Calendar
import java.util.TimeZone

interface ChineseDayOfWeek {
    companion object {
        operator fun invoke(millis: Long?) =
            millis?.let {
                when (today().apply { timeInMillis = it }.dayOfWeek) {
                    Calendar.MONDAY -> "一"
                    Calendar.TUESDAY -> "二"
                    Calendar.WEDNESDAY -> "三"
                    Calendar.THURSDAY -> "四"
                    Calendar.FRIDAY -> "五"
                    Calendar.SATURDAY -> "六"
                    Calendar.SUNDAY -> "日"
                    else -> null
                }
            }
    }
}

fun today(
    millis: Long? = null,
    tz: TimeZone = TimeZone.getDefault(),
): Calendar =
    Calendar.getInstance(tz).apply {
        millis?.let {
            this.timeInMillis = it
        }
    }
