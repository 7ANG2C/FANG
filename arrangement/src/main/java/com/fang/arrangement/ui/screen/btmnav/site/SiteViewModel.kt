package com.fang.arrangement.ui.screen.btmnav.site

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fang.arrangement.definition.Site
import com.fang.arrangement.definition.SiteKey
import com.fang.arrangement.definition.sheet.SheetRepository
import com.fang.arrangement.definition.sheet.sheetAttendance
import com.fang.arrangement.definition.sheet.sheetEmployee
import com.fang.arrangement.definition.sheet.sheetSite
import com.fang.arrangement.foundation.Bool
import com.fang.cosmos.definition.workstate.WorkState
import com.fang.cosmos.definition.workstate.WorkStateImpl
import com.fang.cosmos.foundation.takeIfNotBlank
import com.fang.cosmos.foundation.time.calendar.month
import com.fang.cosmos.foundation.time.calendar.today
import com.fang.cosmos.foundation.time.calendar.year
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@OptIn(ExperimentalCoroutinesApi::class)
internal class SiteViewModel(
    private val sheetRepository: SheetRepository,
) : ViewModel(),
    WorkState by WorkStateImpl() {
    private val _sites = MutableStateFlow(emptyList<Site>())
    val sites = _sites.asStateFlow()

    private val _attendanceMap = MutableStateFlow(mapOf<Long, SiteMoney>())
    val attendanceMap = _attendanceMap.asStateFlow()

    private val _editBundle = MutableStateFlow<SiteEditBundle?>(null)
    val editBundle = _editBundle.asStateFlow()

    init {
        viewModelScope.launch {
            sheetRepository.workSheets
                .mapLatest { workSheets ->
                    val (sitesDeferred, attMapDeferred) =
                        async(Dispatchers.Default) {
                            workSheets
                                ?.sheetSite()
                                ?.values
                                ?.filterNot { it.isDelete }
                                ?.sortedWith(
                                    compareBy<Site> { it.archive }
                                        .thenByDescending { it.id },
                                )
                        } to
                            async(Dispatchers.Default) {
                                val employees = workSheets?.sheetEmployee()?.values
                                workSheets
                                    ?.sheetAttendance()
                                    ?.values
                                    ?.flatMap { all ->
                                        all.attendances.map { all.id to it }
                                    }?.groupBy { it.second.siteId }
                                    ?.mapValues { (_, atts) ->
                                        val full =
                                            atts
                                                .sumOf { p ->
                                                    p.second.fulls.sumOf { eId ->
                                                        employees
                                                            ?.find { it.id == eId }
                                                            ?.salaries
                                                            ?.find { s ->
                                                                p.first >= s.millis
                                                            }?.salary ?: 0
                                                    }
                                                }.toDouble()
                                        val half =
                                            atts.sumOf { p ->
                                                p.second.halfs.sumOf { eId ->
                                                    employees
                                                        ?.find { it.id == eId }
                                                        ?.salaries
                                                        ?.find { s ->
                                                            p.first >= s.millis
                                                        }?.salary ?: 0
                                                }
                                            } * 0.5
                                        SiteMoney(
                                            att = atts.sumOf { it.second.total }.takeIf { it > 0.0 },
                                            years =
                                                atts
                                                    .sortedByDescending { it.first }
                                                    .groupBy {
                                                        today(it.first).year
                                                    }.map { (year, yearAtts) ->
                                                        SiteMoney.Year(
                                                            year = year,
                                                            months =
                                                                yearAtts
                                                                    .groupBy {
                                                                        today(it.first).month
                                                                    }.map { (month, monthAtts) ->
                                                                        SiteMoney.Month(
                                                                            month = month,
                                                                            days =
                                                                                monthAtts.map { pair ->
                                                                                    SiteMoney.Day(
                                                                                        dateMillis = today(pair.first).timeInMillis,
                                                                                        att = pair.second.total,
                                                                                        fulls =
                                                                                            pair.second.fulls
                                                                                                .mapNotNull { eId ->
                                                                                                    employees?.find { e -> e.id == eId }
                                                                                                }.takeIf { it.isNotEmpty() },
                                                                                        halfs =
                                                                                            pair.second.halfs
                                                                                                .mapNotNull { eId ->
                                                                                                    employees?.find { e -> e.id == eId }
                                                                                                }.takeIf { it.isNotEmpty() },
                                                                                    )
                                                                                },
                                                                        )
                                                                    },
                                                        )
                                                    },
                                            salary = (full + half).takeIf { it > 0.0 },
                                        )
                                    }.orEmpty()
                            }

                    val (sites, attMap) = sitesDeferred.await() to attMapDeferred.await()
                    sites?.let { attMap to it }
                }.filterNotNull()
                .flowOn(Dispatchers.Default)
                .collectLatest { (attMap, sites) ->
                    _sites.value = sites
                    _attendanceMap.value = attMap
                }
        }
    }

    fun onInsert() {
        _editBundle.value =
            SiteEditBundle(
                current = null,
                edit =
                    SiteEdit(
                        id = -1L,
                        name = null,
                        address = null,
                        income = null,
                        startMillis = null,
                        endMillis = null,
                        archive = false,
                    ),
            )
    }

    fun onUpdate(current: Site) {
        _editBundle.value =
            SiteEditBundle(
                current = current,
                edit =
                    SiteEdit(
                        id = current.id,
                        name = current.name,
                        address = current.address,
                        income = current.income?.toString(),
                        startMillis = current.startMillis,
                        endMillis = current.endMillis,
                        archive = current.isArchive,
                    ),
            )
    }

    fun editName(name: String?) =
        updateEditBundle {
            it.copy(name = name.takeIfNotBlank)
        }

    fun editAddress(address: String?) =
        updateEditBundle {
            it.copy(address = address.takeIfNotBlank)
        }

    fun editIncome(income: String?) =
        updateEditBundle {
            it.copy(income = income.takeIfNotBlank)
        }

    fun editStartMillis(millis: Long?) =
        updateEditBundle {
            it.copy(startMillis = millis)
        }

    fun editEndMillis(millis: Long?) =
        updateEditBundle {
            it.copy(endMillis = millis)
        }

    fun toggleArchive() =
        updateEditBundle {
            it.copy(archive = !it.archive)
        }

    fun clearEdit() {
        _editBundle.value = null
    }

    fun insert(edit: SiteEdit) {
        if (edit.savable) {
            execute {
                sheetRepository.insert<Site>(
                    keyValues =
                        SiteKey.fold(
                            id = System.currentTimeMillis().toString(),
                            name = "\"${edit.name.orEmpty().trim()}\"",
                            address = "\"${edit.address.orEmpty().trim()}\"",
                            income = edit.income.orEmpty(),
                            startMillis = edit.startMillis?.toString().orEmpty(),
                            endMillis = edit.endMillis?.toString().orEmpty(),
                            archive = Bool.FALSE.toString(),
                            delete = Bool.FALSE.toString(),
                        ),
                )
            }
        }
    }

    fun update(editBundle: SiteEditBundle) {
        val id = editBundle.current?.id?.toString()
        val edit = editBundle.edit
        if (id != null && edit.savable && editBundle.anyDiff) {
            execute {
                sheetRepository.update<Site>(
                    key = SiteKey.ID,
                    value = id,
                    keyValues =
                        SiteKey.fold(
                            id = id,
                            name = "\"${edit.name.orEmpty().trim()}\"",
                            address = "\"${edit.address.orEmpty().trim()}\"",
                            income = edit.income.orEmpty(),
                            startMillis = edit.startMillis?.toString().orEmpty(),
                            endMillis = edit.endMillis?.toString().orEmpty(),
                            archive = Bool(edit.archive).toString(),
                            delete = Bool.FALSE.toString(),
                        ),
                )
            }
        }
    }

    fun delete(current: Site) {
        execute {
            val id = current.id.toString()
            sheetRepository.update<Site>(
                key = SiteKey.ID,
                value = id,
                keyValues =
                    SiteKey.fold(
                        id = id,
                        name = "\"${current.name}\"",
                        address = "\"${current.address.orEmpty()}\"",
                        income = current.income?.toString().orEmpty(),
                        startMillis = current.startMillis?.toString().orEmpty(),
                        endMillis = current.endMillis?.toString().orEmpty(),
                        archive = Bool(current.isArchive).toString(),
                        delete = Bool.TRUE.toString(),
                    ),
            )
        }
    }

    private fun updateEditBundle(sss: (sdf: SiteEdit) -> SiteEdit) {
        _editBundle.update {
            it?.copy(edit = sss(it.edit))
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
