package com.fang.free.tick

data class MediatorTick(
    val close: Double,
    val tickMillis: Long,
    val closeMillis: Long,
)
