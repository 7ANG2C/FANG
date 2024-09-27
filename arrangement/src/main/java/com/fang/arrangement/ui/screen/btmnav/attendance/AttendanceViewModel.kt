package com.fang.arrangement.ui.screen.btmnav.attendance

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fang.arrangement.definition.Attendance
import com.fang.arrangement.definition.AttendanceAll
import com.fang.arrangement.definition.AttendanceKey
import com.fang.arrangement.definition.Employee
import com.fang.arrangement.definition.Site
import com.fang.arrangement.definition.sheet.SheetRepository
import com.fang.arrangement.definition.sheet.sheetAttendance
import com.fang.arrangement.definition.sheet.sheetEmployee
import com.fang.arrangement.definition.sheet.sheetSite
import com.fang.arrangement.foundation.noBreathing
import com.fang.arrangement.ui.shared.dsl.Remark
import com.fang.cosmos.definition.workstate.WorkState
import com.fang.cosmos.definition.workstate.WorkStateImpl
import com.fang.cosmos.foundation.json
import com.fang.cosmos.foundation.mapNoNull
import com.fang.cosmos.foundation.takeIfNotBlank
import com.google.gson.Gson
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

@OptIn(ExperimentalCoroutinesApi::class)
internal class AttendanceViewModel(
    private val sheetRepository: SheetRepository,
    private val gson: Gson,
) : ViewModel(), WorkState by WorkStateImpl() {
    private val _bundle =
        MutableStateFlow(
            AttBundle(
                sites = emptyList(),
                employees = emptyList(),
                attAlls = emptyList(),
            ),
        )
    val bundle = _bundle.asStateFlow()

    private val _editBundle = MutableStateFlow<AttEditBundle?>(null)
    val editBundle = _editBundle.asStateFlow()

    private val _mAttEdit = MutableStateFlow(MAttendance.empty)
    val mAttEdit = _mAttEdit.asStateFlow()

    init {
        viewModelScope.launch {
            sheetRepository.workSheets
                .mapLatest { workSheets ->
                    val sites = workSheets?.sheetSite()?.values.orEmpty()
                    val employees =
                        workSheets?.sheetEmployee()?.values.orEmpty()
                            .sortedWith(
                                compareBy<Employee>(
                                    { it.isDelete },
                                    { it.isExpire },
                                ).thenByDescending { it.id },
                            )
                    val findEmployee = { id: Long ->
                        employees.find { it.id == id }
                    }
                    workSheets?.sheetAttendance()?.values?.sortedByDescending { it.id }
                        ?.map { attAll ->
                            MAttendanceAll(
                                id = attAll.id,
                                attendances =
                                    attAll.attendances.map { att ->
                                        MAttendance(
                                            siteId = att.siteId,
                                            site = sites.find { att.siteId == it.id },
                                            fulls =
                                                att.fulls.map { id ->
                                                    MEmployee(id, findEmployee(id))
                                                }
                                                    .sortedWith(
                                                        compareBy<MEmployee>(
                                                            { it.employee == null },
                                                            { it.employee?.isDelete == true },
                                                            { it.employee?.isExpire == true },
                                                        ).thenByDescending { it.employee?.id },
                                                    ),
                                            halfs =
                                                att.halfs.map { id ->
                                                    MEmployee(id, findEmployee(id))
                                                }.sortedWith(
                                                    compareBy<MEmployee>(
                                                        { it.employee == null },
                                                        { it.employee?.isDelete == true },
                                                        { it.employee?.isExpire == true },
                                                    ).thenByDescending { it.employee?.id },
                                                ),
                                            remark = att.remark.takeIfNotBlank,
                                        )
                                    }
                                        .sortedWith(
                                            compareBy<MAttendance>(
                                                { it.site == null },
                                                { it.site?.isDelete == true },
                                                { it.site?.isArchive == true },
                                            ).thenByDescending { it.site?.id },
                                        ),
                            )
                        }
                        ?.let {
                            AttBundle(sites = sites, employees = employees, attAlls = it)
                        }
                }
                .filterNotNull()
                .flowOn(Dispatchers.Default)
                .collectLatest {
                    _bundle.value = it
                }
        }
    }

    fun onInsert(sites: List<Site>) {
        _editBundle.value =
            AttEditBundle(
                current = null,
                edit =
                    AttAllEdit(
                        id = null,
                        attSiteEdits =
                            sites.mapNoNull({
                                it.notArchive && it.notDelete
                            }) { site ->
                                MAttendance(
                                    siteId = site.id,
                                    site = site,
                                    fulls = emptyList(),
                                    halfs = emptyList(),
                                    remark = null,
                                )
                            },
                    ),
            )
    }

    fun onUpdate(
        attendance: MAttendanceAll,
        sites: List<Site>,
    ) {
        val current = attendance.attendances
        val currentSiteId = current.map { it.siteId }
        // 如果有新增的 site
        val newSites =
            sites.mapNoNull({
                it.notArchive && it.notDelete && it.id !in currentSiteId
            }) { site ->
                MAttendance(
                    siteId = site.id,
                    site = site,
                    fulls = emptyList(),
                    halfs = emptyList(),
                    remark = null,
                )
            }
        _editBundle.value =
            AttEditBundle(
                current = attendance,
                edit =
                    AttAllEdit(
                        id = attendance.id,
                        attSiteEdits =
                            (newSites + current)
                                .sortedWith(
                                    compareByDescending<MAttendance> {
                                        (it.fulls.size + it.halfs.size * 0.5) > 0
                                    }
                                        .thenByDescending { it.site == null }
                                        .thenByDescending { it.site?.isDelete == true }
                                        .thenByDescending { it.site?.isArchive == true }
                                        .thenByDescending { it.site?.id },
                                ),
                    ),
            )
    }

    fun editDate(millis: Long?) {
        _editBundle.update { it?.copy(edit = it.edit.copy(id = millis)) }
    }

    fun editSingleSite(mAttendance: MAttendance) {
        _mAttEdit.value = mAttendance
    }

    fun editSingleSiteEmployee(
        isFull: Boolean,
        employee: MEmployee,
    ) {
        _mAttEdit.update { old ->
            old.copy(
                fulls =
                    if (isFull) {
                        if (employee in old.fulls) {
                            old.fulls - employee
                        } else {
                            (old.fulls + employee).sortedWith(
                                compareBy<MEmployee>(
                                    { it.employee == null },
                                    { it.employee?.isDelete == true },
                                    { it.employee?.isExpire == true },
                                ).thenByDescending { it.employee?.id },
                            )
                        }
                    } else {
                        old.fulls - employee
                    },
                halfs =
                    if (isFull) {
                        old.halfs - employee
                    } else {
                        if (employee in old.halfs) {
                            old.halfs - employee
                        } else {
                            (old.halfs + employee).sortedWith(
                                compareBy<MEmployee>(
                                    { it.employee == null },
                                    { it.employee?.isDelete == true },
                                    { it.employee?.isExpire == true },
                                ).thenByDescending { it.employee?.id },
                            )
                        }
                    },
            )
        }
    }

    fun editSingleSiteRemark(remark: String?) {
        _mAttEdit.update { old ->
            old.copy(remark = remark.takeIfNotBlank?.take(Remark.L30))
        }
    }

    fun doneSingleSite(mAtt: MAttendance) {
        _editBundle.update { old ->
            old?.copy(
                edit =
                    old.edit.copy(
                        attSiteEdits =
                            old.edit.attSiteEdits.map {
                                if (it.siteId == mAtt.siteId) mAtt else it
                            },
                    ),
            )
        }
        clearSingleSite()
    }

    fun clearSingleSite() {
        _mAttEdit.value = MAttendance.empty
    }

    fun clearEdit() {
        _editBundle.value = null
    }

    fun insert(edit: AttAllEdit) {
        if (edit.id != null && edit.savable) {
            execute {
                sheetRepository.insert<AttendanceAll>(
                    keyValues =
                        AttendanceKey.fold(
                            id = edit.id.toString(),
                            attendances =
                                gson.json(
                                    edit.attSiteEdits.mapNoNull({
                                        it.fulls.isNotEmpty() || it.halfs.isNotEmpty()
                                    }) { siteEdit ->
                                        Attendance(
                                            siteId = siteEdit.siteId,
                                            fulls = siteEdit.fulls.map { it.id },
                                            halfs = siteEdit.halfs.map { it.id },
                                            remark = siteEdit.remark.orEmpty().trim(),
                                        )
                                    },
                                ).getOrNull()?.noBreathing ?: "[]",
                        ),
                )
            }
        }
    }

    fun update(editBundle: AttEditBundle) {
        val currentId = editBundle.current?.id?.toString()
        val edit = editBundle.edit
        if (currentId != null && edit.savable && editBundle.anyDiff) {
            execute {
                sheetRepository.update<AttendanceAll>(
                    key = AttendanceKey.ID,
                    value = currentId,
                    keyValues =
                        AttendanceKey.fold(
                            id = edit.id?.toString().orEmpty(),
                            attendances =
                                gson.json(
                                    edit.attSiteEdits.mapNoNull({
                                        it.fulls.isNotEmpty() || it.halfs.isNotEmpty()
                                    }) { siteEdit ->
                                        Attendance(
                                            siteId = siteEdit.siteId,
                                            fulls = siteEdit.fulls.map { it.id },
                                            halfs = siteEdit.halfs.map { it.id },
                                            remark = siteEdit.remark.orEmpty().trim(),
                                        )
                                    },
                                ).getOrNull()?.noBreathing ?: "[]",
                        ),
                )
            }
        }
    }

    fun delete(id: String) {
        execute {
            sheetRepository.delete<AttendanceAll>(key = AttendanceKey.ID, value = id)
        }
    }

    private fun <T> execute(block: suspend CoroutineScope.() -> Result<T>) {
        loading()
        viewModelScope.launch {
            block().onSuccess {
                clearEdit()
            }.onFailure(::throwable)
            noLoading()
        }
    }
}
