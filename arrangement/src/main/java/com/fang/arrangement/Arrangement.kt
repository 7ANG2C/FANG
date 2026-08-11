package com.fang.arrangement

enum class Arrangement(
    val id: String,
) {
    PROD("prod"),
    SIT("sit"),
    ;

    companion object {
        val current by lazy { valueOf(BuildConfig.ARRANGEMENT_ENVIRONMENT.uppercase()) }
        val isFancy get() = true
    }
}
