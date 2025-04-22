package com.fang.arrangement.ui.screen.btmnav.money.fund

import com.fang.cosmos.foundation.NumberFormat

internal data class YearMonthFund(
    val year: Int,
    val month: Int,
    val dayFunds: List<DayFund>,
) {
    data class DayFund(
        val day: Int,
        val funds: List<MFund>,
    ) {
        val selectedFund get() = funds.sumOf { if (it.selected) it.fund else 0 }.takeIf { it > 0 }
        val selectedFundDisplay get() =
            selectedFund?.let {
                "$${NumberFormat(number = it, decimal = 0)}"
            }
        val totalFund get() = funds.sumOf { it.fund }
        val totalFundDisplay get() = "$${NumberFormat(number = totalFund, decimal = 0)}"
    }

    val selectedFund get() = dayFunds.sumOf { it.selectedFund ?: 0 }.takeIf { it > 0 }
    val selectedFundDisplay get() =
        selectedFund?.let {
            "$${NumberFormat(number = it, decimal = 0)}"
        }
    val totalFund get() = dayFunds.sumOf { it.totalFund }
    val totalFundDisplay get() = "$${NumberFormat(number = totalFund, decimal = 0)}"
}

internal val List<YearMonthFund>.selectedFund get() =
    sumOf { it.selectedFund ?: 0 }.takeIf { it > 0 }?.let {
        "$${NumberFormat(number = it, decimal = 0)}"
    }
internal val List<YearMonthFund>.totalFund get() = "$${NumberFormat(number = sumOf { it.totalFund }, decimal = 0)}"
