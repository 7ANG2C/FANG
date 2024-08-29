package com.fang.arrangement.ui.screen.btmnav.statistic

internal data class StatisticByDate(
    val date: String,
    val items: List<Pair<String, Double>>
)