package com.fang.arrangement.ui.screen.btmnav.statistic

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fang.arrangement.definition.attendancesummary.AttendanceSummaryRepository
import com.fang.arrangement.definition.building.BuildingRepository
import com.fang.cosmos.foundation.time.calendar.month
import com.fang.cosmos.foundation.time.calendar.today
import com.fang.cosmos.foundation.time.calendar.year
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.launch

@OptIn(ExperimentalCoroutinesApi::class)
internal class StatisticViewModel(
    private val attendanceSummaryRepository: AttendanceSummaryRepository,
    private val repository: BuildingRepository
) : ViewModel() {

    private data class Mediator(
        val createMillis: Long,
        val siteId: Long,
        val attendanceCount: Double,
    )

    private val _byDates = MutableStateFlow(emptyList<StatisticByDate>())
    val byDates = _byDates.asStateFlow()

    private val _bySites = MutableStateFlow(emptyList<StatisticBySite>())
    val bySites = _bySites.asStateFlow()

    init {
        viewModelScope.launch {
            combine(
                attendanceSummaryRepository.invoke().filterNotNull(),
                repository.invoke().filterNotNull(),
                ::Pair
            )
                .mapLatest { (summaries, sites) ->
                    // 按照月份
                    val byDates = summaries.groupBy {
                        val calendar = today().apply {
                            timeInMillis = it.id
                        }
                        "${calendar.year}/${calendar.month + 1}"
                    }.map { entry ->
                        val idToCount =
                            entry.value.flatMap { it.attendances }.groupBy { it.buildingId }
                                .map { e ->
                                    e.key to e.value.sumOf { it.attendanceCount }
                                }
                        val items = sites.mapNotNull { site ->
                            idToCount.find { it.first == site.id }?.let {
                                site.name to it.second
                            }
                        }
                        StatisticByDate(entry.key, items)
                    }.sortedByDescending { it.date.replace("/", "") }

                    // 按照工地
                    val bySites = summaries.flatMap { s ->
                        s.attendances.map {
                            Mediator(s.id, it.buildingId, it.attendanceCount)
                        }
                    }
                        .sortedByDescending { it.siteId }
                        .groupBy { it.siteId }.map { entry ->
                            val name = sites.find { it.id == entry.key }?.name ?: "-"
                            val items = entry.value.groupBy {
                                val calendar = today().apply {
                                    timeInMillis = it.createMillis
                                }
                                "${calendar.year}/${calendar.month + 1}"
                            }
                                .map { e ->
                                    e.key to e.value.sumOf { it.attendanceCount }
                                }.sortedByDescending { it.first.replace("/", "") }
                            StatisticBySite(name, items)
                        }
                    byDates to bySites
                }
                .flowOn(Dispatchers.Default)
                .collectLatest { (byDates, bySites) ->
                    _byDates.value = byDates
                    _bySites.value = bySites
                }
        }
    }

}