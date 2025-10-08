package com.fang.arrangement.ui.screen.btmnav.statistic.sitefind

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fang.arrangement.definition.Site
import com.fang.arrangement.definition.sheet.SheetRepository
import com.fang.arrangement.definition.sheet.sheetFund
import com.fang.arrangement.definition.sheet.sheetSite
import com.fang.arrangement.ui.screen.btmnav.statistic.sitefind.YearMonthFund.DayFund
import com.fang.cosmos.definition.workstate.WorkState
import com.fang.cosmos.definition.workstate.WorkStateImpl
import com.fang.cosmos.foundation.replace
import com.fang.cosmos.foundation.time.calendar.dayOfMonth
import com.fang.cosmos.foundation.time.calendar.month
import com.fang.cosmos.foundation.time.calendar.today
import com.fang.cosmos.foundation.time.calendar.year
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@OptIn(ExperimentalCoroutinesApi::class)
internal class SiteFundViewModel(
    private val sheetRepository: SheetRepository,
) : ViewModel(),
    WorkState by WorkStateImpl() {
    private val _ymFunds = MutableStateFlow(emptyList<YearMonthFund>())
    val ymFunds = _ymFunds.asStateFlow()

    private val _sites = MutableStateFlow(emptyList<Site>())
    val sites = _sites.asStateFlow()

    private val _site = MutableStateFlow<Site?>(null)
    val site = _site.asStateFlow()

    init {
        viewModelScope.launch {
            sheetRepository.workSheets
                .mapNotNull { workSheets ->
                    val sites =
                        workSheets
                            ?.sheetSite()
                            ?.values
                            ?.filterNot { it.isArchive || it.isDelete }
                            ?.sortedWith(
                                compareBy<Site> { it.archive }.thenByDescending { it.id },
                            )?.takeIf { it.isNotEmpty() }
                    val funds =
                        workSheets
                            ?.sheetFund()
                            ?.values
                            ?.takeIf { it.isNotEmpty() }
                    if (sites != null && funds != null) {
                        _sites.value = sites
                        sites to funds
                    } else {
                        null
                    }
                }.flatMapLatest { (sites, funds) ->
                    site.mapLatest { s ->
                        val selected = s ?: sites.first()
                        funds
                            .mapNotNull { f ->
                                if (f.siteId == selected.id) {
                                    SiteFund(
                                        selected = false,
                                        id = f.id,
                                        fund = f.fund,
                                        millis = f.millis,
                                        site = selected,
                                        remark = f.remark,
                                    )
                                } else {
                                    null
                                }
                            }.sortedWith(
                                compareByDescending<SiteFund> { it.millis }
                                    .thenByDescending { it.id },
                            ).groupBy {
                                with(today(it.millis)) { year to month }
                            }.map { (pair, yFunds) ->
                                val (year, month) = pair
                                val dayFunds =
                                    yFunds
                                        .groupBy {
                                            today(it.millis).dayOfMonth
                                        }.map { (day, funds) ->
                                            DayFund(day = day, funds = funds)
                                        }
                                YearMonthFund(year = year, month = month, dayFunds = dayFunds)
                            }
                    }
                }.flowOn(Dispatchers.Default)
                .collectLatest {
                    _ymFunds.value = it
                }
        }
    }

    fun selectSite(site: Site) {
        _site.value = site
    }

    fun toggle(id: Long) {
        _ymFunds.update { ymFunds ->
            ymFunds.map { ymFund ->
                ymFund.copy(
                    dayFunds =
                        ymFund.dayFunds.map { dayFund ->
                            dayFund.copy(
                                funds =
                                    dayFund.funds.replace({ it.id == id }) {
                                        it.copy(selected = !it.selected)
                                    },
                            )
                        },
                )
            }
        }
    }

    fun clearToggle() {
        _ymFunds.update { ymFunds ->
            ymFunds.map { ymFund ->
                ymFund.copy(
                    dayFunds =
                        ymFund.dayFunds.map { dayFund ->
                            dayFund.copy(
                                funds =
                                    dayFund.funds.map {
                                        it.copy(selected = false)
                                    },
                            )
                        },
                )
            }
        }
    }
}
