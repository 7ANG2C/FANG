package com.fang.arrangement.ui.screen.btmnav.money.fund

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fang.arrangement.definition.Fund
import com.fang.arrangement.definition.FundKey
import com.fang.arrangement.definition.Site
import com.fang.arrangement.definition.sheet.SheetRepository
import com.fang.arrangement.definition.sheet.sheetFund
import com.fang.arrangement.definition.sheet.sheetSite
import com.fang.arrangement.ui.screen.btmnav.money.fund.YearMonthFund.DayFund
import com.fang.arrangement.ui.shared.dsl.Remark
import com.fang.cosmos.definition.workstate.WorkState
import com.fang.cosmos.definition.workstate.WorkStateImpl
import com.fang.cosmos.foundation.replace
import com.fang.cosmos.foundation.takeIfNotBlank
import com.fang.cosmos.foundation.time.calendar.dayOfMonth
import com.fang.cosmos.foundation.time.calendar.midnight
import com.fang.cosmos.foundation.time.calendar.month
import com.fang.cosmos.foundation.time.calendar.today
import com.fang.cosmos.foundation.time.calendar.year
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.Calendar
import java.util.TimeZone
import kotlin.collections.orEmpty

@OptIn(ExperimentalCoroutinesApi::class)
internal class FundViewModel(
    private val sheetRepository: SheetRepository,
) : ViewModel(),
    WorkState by WorkStateImpl() {
    private val _ymFunds = MutableStateFlow(emptyList<YearMonthFund>())
    val ymFunds = _ymFunds.asStateFlow()

    private val _sites = MutableStateFlow(emptyList<Site>())
    val sites = _sites.asStateFlow()

    private val _editBundle = MutableStateFlow<FundEditBundle?>(null)
    val editBundle = _editBundle.asStateFlow()

    init {
        viewModelScope.launch {
            sheetRepository.workSheets
                .mapLatest { workSheets ->
                    val sites =
                        workSheets
                            ?.sheetSite()
                            ?.values
                            .orEmpty()
                            .filterNot { it.isArchive || it.isDelete }
                            .sortedWith(
                                compareBy<Site> { it.archive }
                                    .thenByDescending { it.id },
                            )
                    workSheets
                        ?.sheetFund()
                        ?.values
                        ?.map { f ->
                            MFund(
                                selected = false,
                                id = f.id,
                                fund = f.fund,
                                millis = f.millis,
                                site = sites.find { it.id == f.siteId },
                                remark = f.remark,
                            )
                        }?.sortedWith(
                            compareByDescending<MFund> { it.millis }
                                .thenByDescending { it.id },
                        )?.groupBy {
                            with(today(it.millis)) { year to month }
                        }?.map { (pair, yFunds) ->
                            val (year, month) = pair
                            val dayFunds =
                                yFunds
                                    .groupBy {
                                        today(it.millis).dayOfMonth
                                    }.map { (day, funds) ->
                                        DayFund(day = day, funds = funds)
                                    }
                            YearMonthFund(year = year, month = month, dayFunds = dayFunds)
                        }?.let {
                            it to sites
                        }
                }.filterNotNull()
                .flowOn(Dispatchers.Default)
                .collectLatest { (ymFunds, sites) ->
                    _ymFunds.value = ymFunds
                    _sites.value = sites
                }
        }
    }

    fun toggle(
        year: Int,
        month: Int,
    ) {
        _ymFunds.update { ymFunds ->
            ymFunds.replace({ it.year == year && it.month == month }) { ymFund ->
                val selected =
                    ymFund.dayFunds.any { dayFund ->
                        dayFund.funds.any { !it.selected }
                    }
                ymFund.copy(
                    dayFunds =
                        ymFund.dayFunds.map { dayFund ->
                            dayFund.copy(funds = dayFund.funds.map { it.copy(selected = selected) })
                        },
                )
            }
        }
    }

    fun toggle(
        year: Int,
        month: Int,
        day: Int,
    ) {
        _ymFunds.update { ymFunds ->
            ymFunds.map { ymFund ->
                ymFund.copy(
                    dayFunds =
                        ymFund.dayFunds.replace(
                            { ymFund.year == year && ymFund.month == month && it.day == day },
                        ) { dayFund ->
                            dayFund.copy(
                                funds =
                                    dayFund.funds.map { fund ->
                                        fund.copy(selected = dayFund.funds.any { !it.selected })
                                    },
                            )
                        },
                )
            }
        }
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

    fun onInsert() {
        _editBundle.value =
            FundEditBundle(
                current = null,
                edit =
                    FundEdit(
                        id = -1L,
                        fund = null,
                        millis =
                            Calendar
                                .getInstance(TimeZone.getTimeZone("UTC"))
                                .apply {
                                    timeInMillis = System.currentTimeMillis()
                                }.midnight.timeInMillis,
                        siteId = null,
                        remark = null,
                    ),
            )
    }

    fun onUpdate(current: MFund) {
        _editBundle.value =
            FundEditBundle(
                current = current,
                edit =
                    FundEdit(
                        id = current.id,
                        fund = current.fund.toString(),
                        millis = current.millis,
                        siteId = current.site?.id,
                        remark = current.remark,
                    ),
            )
    }

    fun editFund(fund: String?) {
        _editBundle.update {
            it?.copy(edit = it.edit.copy(fund = fund.takeIfNotBlank))
        }
    }

    fun editMillis(millis: Long?) {
        _editBundle.update {
            it?.copy(
                edit = it.edit.copy(millis = millis),
            )
        }
    }

    fun editSite(siteId: Long?) {
        _editBundle.update {
            it?.copy(edit = it.edit.copy(siteId = siteId))
        }
    }

    fun editRemark(remark: String?) {
        _editBundle.update {
            it?.copy(
                edit = it.edit.copy(remark = remark.takeIfNotBlank?.take(Remark.L30)),
            )
        }
    }

    fun clearEdit() {
        _editBundle.value = null
    }

    fun clearAllSelected() {
        _ymFunds.update { ymFunds ->
            ymFunds.map { ymFund ->
                ymFund.copy(
                    dayFunds =
                        ymFund.dayFunds.map { day ->
                            day.copy(
                                funds =
                                    day.funds.map {
                                        it.copy(selected = false)
                                    },
                            )
                        },
                )
            }
        }
    }

    fun insert(edit: FundEdit) {
        if (edit.savable) {
            execute {
                sheetRepository.insert<Fund>(
                    keyValues =
                        FundKey.fold(
                            id = System.currentTimeMillis().toString(),
                            fund = edit.fund.orEmpty(),
                            millis = edit.millis?.toString().orEmpty(),
                            siteId = edit.siteId?.toString().orEmpty(),
                            remark = "\"${edit.remark.orEmpty().trim()}\"",
                        ),
                )
            }
        }
    }

    fun update(editBundle: FundEditBundle) {
        val id = editBundle.current?.id?.toString()
        val edit = editBundle.edit
        if (id != null && edit.savable && editBundle.anyDiff) {
            execute {
                sheetRepository.update<Fund>(
                    key = FundKey.ID,
                    value = id,
                    keyValues =
                        FundKey.fold(
                            id = id,
                            fund = edit.fund.orEmpty(),
                            millis = edit.millis?.toString().orEmpty(),
                            siteId = edit.siteId?.toString().orEmpty(),
                            remark = "\"${edit.remark.orEmpty().trim()}\"",
                        ),
                )
            }
        }
    }

    fun delete(id: String) {
        execute { sheetRepository.delete<Fund>(key = FundKey.ID, value = id) }
    }

    fun deletes(ids: List<String>) {
        execute {
            sheetRepository.deletes<Fund>(
                key = FundKey.ID,
                values = ids,
            )
        }
    }

    private fun <T> execute(block: suspend CoroutineScope.() -> Result<T>) {
        loading()
        viewModelScope.launch {
            block()
                .onSuccess {
                    clearEdit()
                }.onFailure(::throwable)
            noLoading()
        }
    }
}
