package com.fang.cosmos.foundation.time.calendar

import java.util.Calendar
import java.util.TimeZone

fun today(tz: TimeZone = TimeZone.getDefault()): Calendar = Calendar.getInstance(tz)
