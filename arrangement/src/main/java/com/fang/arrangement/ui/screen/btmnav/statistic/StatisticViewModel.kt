package com.fang.arrangement.ui.screen.btmnav.statistic

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

internal class StatisticViewModel : ViewModel() {
    private val _statistic = MutableStateFlow(Statistic.SALARY)
    val statistic = _statistic.asStateFlow()

    fun setStatistic(statistic: Statistic) {
        _statistic.value = statistic
    }
}
