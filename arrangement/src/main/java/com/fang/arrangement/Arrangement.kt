package com.fang.arrangement

enum class Arrangement(
    val id: String,
) {
    PROD("prod"),
    SIT("sit"),
    ;

    companion object {
        val current by lazy { SIT }
        val isFancy get() = true
    }
}
