package com.fang.cosmos.foundation.time.calendar

import java.util.Calendar

val Calendar.copy get() = this.clone() as Calendar
val Calendar.year get() = get(Calendar.YEAR)
val Calendar.month get() = get(Calendar.MONTH)
val Calendar.dayOfMonth get() = get(Calendar.DAY_OF_MONTH)
val Calendar.dayOfWeek get() = get(Calendar.DAY_OF_WEEK)
val Calendar.toDayStart
    get() =
        copy.apply {
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
val Calendar.isWeekend
    get() = dayOfWeek == Calendar.SATURDAY || dayOfWeek == Calendar.SUNDAY
val Calendar.isNotWeekend get() = !isWeekend
