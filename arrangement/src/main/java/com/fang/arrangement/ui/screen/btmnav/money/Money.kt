package com.fang.arrangement.ui.screen.btmnav.money

import com.fang.arrangement.Arrangement

internal enum class Money(
    val display: String,
) {
    FUND("公帳代墊"),
    LOAN("員工借支"),
    PAYBACK("欠錢要還"),
    ;

    companion object {
        val all by lazy {
            if (Arrangement.isFancy) entries else listOf(LOAN, FUND, PAYBACK)
        }
        val default by lazy { all.first() }
    }
}
