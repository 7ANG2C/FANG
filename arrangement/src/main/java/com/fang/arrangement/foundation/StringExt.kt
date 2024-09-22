package com.fang.arrangement.foundation

internal const val DASH = "-"
internal val String?.orDash get() = this ?: DASH
internal val String.noBreathing get() = replace("\\s+".toRegex(), "")
