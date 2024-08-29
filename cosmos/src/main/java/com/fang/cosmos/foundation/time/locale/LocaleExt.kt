package com.fang.cosmos.foundation.time.locale

import java.util.Locale

fun Locale.isSame(compare: Locale) = toString() in compare.toString() || compare.toString() in toString()

fun Locale.isSystem() = isSame(Locale.getDefault())
