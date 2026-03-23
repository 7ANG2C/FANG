package com.fang.free.tick

import com.fang.free.ta.Macd

data class Tick(
    val close: Double,
    val closeMillis: Long,
    val td: Int?,
    val macd: Macd,
    val rsi: Double?,
)
