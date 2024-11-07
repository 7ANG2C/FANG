package com.fang.arrangement.ui.screen.btmnav.money

import com.fang.arrangement.Arrangement

internal enum class Money(val display: String) {
    FUND("公帳代墊"),
    LOAN("員工借支"),
    ;

    companion object {
        val all by lazy {
            when (Arrangement.current) {
                Arrangement.UAT -> entries.reversed()
                else -> entries
            }
        }
        val default by lazy { all.first() }
    }
}
