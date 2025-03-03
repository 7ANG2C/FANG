package com.fang.cosmos.foundation.time.transformer

interface TimePattern {
    companion object {
        /**
         * Use System Date Format Pattern (System only support y/m/d)
         */
        const val SYSTEM_Y_M_D = "SYSTEM_Y_M_D"

        /**
         * 呼叫服務所需的日期格式，目前為 yyyy-MM-dd
         */
        val default by lazy { yyyyMMdd("-") }

        fun yyyyMMdd(separator: String = ""): String = "yyyy${separator}MM${separator}dd"
    }
}
