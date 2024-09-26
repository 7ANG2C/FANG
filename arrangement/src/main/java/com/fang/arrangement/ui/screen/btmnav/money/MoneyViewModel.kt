package com.fang.arrangement.ui.screen.btmnav.money

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

internal class MoneyViewModel : ViewModel() {
    private val _money = MutableStateFlow(Money.FUND)
    val money = _money.asStateFlow()

    fun setMoney(money: Money) {
        _money.value = money
    }
}
