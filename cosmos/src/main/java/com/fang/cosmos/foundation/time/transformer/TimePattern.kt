package com.fang.cosmos.foundation.time.transformer

object TimePattern {
    /**
     * Use System Date Format Pattern (System only support y/m/d)
     */
    const val SYSTEM_Y_M_D = "SYSTEM_Y_M_D"

    /**
     * 呼叫服務所需的日期格式，目前為 yyyy-MM-dd
     */
    val api by lazy { yyyyMMdd("-") }

    fun yyyyMMdd(separator: String = ""): String {
        return "yyyy${separator}MM${separator}dd"
    }
}
