package com.fang.arrangement.ui.screen.btmnav.attendance

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fang.arrangement.definition.Attendance
import com.fang.arrangement.definition.AttendanceAll
import com.fang.arrangement.definition.Site
import com.fang.arrangement.definition.sheet.SheetRepository
import com.fang.arrangement.definition.sheet.sheetAttendance
import com.fang.arrangement.definition.sheet.sheetEmployee
import com.fang.arrangement.definition.sheet.sheetSite
import com.fang.arrangement.definition.storage.AttendanceImageRepository
import com.fang.arrangement.ui.shared.dsl.Remark
import com.fang.cosmos.definition.workstate.WorkState
import com.fang.cosmos.definition.workstate.WorkStateImpl
import com.fang.cosmos.foundation.logD
import com.fang.cosmos.foundation.mapNoNull
import com.fang.cosmos.foundation.takeIfNotBlank
import com.fang.cosmos.foundation.time.calendar.midnight
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
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

@OptIn(ExperimentalCoroutinesApi::class)
internal class AttendanceViewModel(
    private val sheetRepository: SheetRepository,
    private val attendanceImageRepository: AttendanceImageRepository,
) : ViewModel(),
    WorkState by WorkStateImpl() {
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
                        workSheets
                            ?.sheetEmployee()
                            ?.values
                            .orEmpty()
                    val findEmployee = { id: Long ->
                        employees.find { it.id == id }
                    }
                    workSheets
                        ?.sheetAttendance()
                        ?.values
                        ?.sortedByDescending { it.id }
                        ?.map { attAll ->
                            MAttendanceAll(
                                id = attAll.id,
                                attendances =
                                    attAll.attendances
                                        .map { att ->
                                            MAttendance(
                                                siteId = att.siteId,
                                                site = sites.find { att.siteId == it.id },
                                                fulls =
                                                    att.fulls
                                                        .map { id ->
                                                            MEmployee(id, findEmployee(id))
                                                        }.sortedWith(
                                                            compareBy<MEmployee>(
                                                                { it.employee == null },
                                                                { it.employee?.isDelete == true },
                                                                { it.employee?.isExpire == true },
                                                            ).thenBy { it.employee?.order },
                                                        ),
                                                halfs =
                                                    att.halfs
                                                        .map { id ->
                                                            MEmployee(id, findEmployee(id))
                                                        }.sortedWith(
                                                            compareBy<MEmployee>(
                                                                { it.employee == null },
                                                                { it.employee?.isDelete == true },
                                                                { it.employee?.isExpire == true },
                                                            ).thenBy { it.employee?.order },
                                                        ),
                                                remark = att.remark.takeIfNotBlank,
                                                images = att.images.map { MAttendanceImage(remote = it) },
                                            )
                                        }.sortedWith(
                                            compareBy<MAttendance>(
                                                { it.site == null },
                                                { it.site?.isDelete == true },
                                                { it.site?.isArchive == true },
                                            ).thenByDescending { it.site?.id },
                                        ),
                            )
                        }?.let {
                            AttBundle(sites = sites, employees = employees, attAlls = it)
                        }
                }.filterNotNull()
                .flowOn(Dispatchers.Default)
                .collectLatest {
                    _bundle.value = it
                }
        }
    }

    fun onInsert(sites: List<Site>) {
        val todayMillis =
            Calendar
                .getInstance(TimeZone.getTimeZone("UTC"))
                .apply {
                    timeInMillis = System.currentTimeMillis()
                }.midnight.timeInMillis
        _editBundle.value =
            AttEditBundle(
                current = null,
                edit =
                    AttAllEdit(
                        id =
                            todayMillis.takeIf { millis ->
                                millis !in bundle.value.attAlls.map { it.id }
                            },
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
                                    images = emptyList(),
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
                    images = emptyList(),
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
                                    }.thenByDescending { it.site == null }
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
                                ).thenBy { it.employee?.order },
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
                                ).thenBy { it.employee?.order },
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

    fun addImages(
        siteId: Long,
        uris: List<Uri>,
    ) {
        _editBundle.update { bundle ->
            bundle?.copy(
                edit =
                    bundle.edit.copy(
                        attSiteEdits =
                            bundle.edit.attSiteEdits.map { attendance ->
                                if (attendance.siteId != siteId) {
                                    attendance
                                } else if (attendance.fulls.isEmpty() && attendance.halfs.isEmpty()) {
                                    attendance
                                } else {
                                    attendance.copy(
                                        images =
                                            (attendance.images + uris.map { MAttendanceImage(localUri = it) })
                                                .take(MAttendance.MAX_IMAGE_COUNT),
                                    )
                                }
                            },
                    ),
            )
        }
    }

    fun removeImage(
        siteId: Long,
        image: MAttendanceImage,
    ) {
        _editBundle.update { bundle ->
            bundle?.copy(
                edit =
                    bundle.edit.copy(
                        attSiteEdits =
                            bundle.edit.attSiteEdits.map { attendance ->
                                if (attendance.siteId != siteId) attendance else attendance.copy(images = attendance.images - image)
                            },
                    ),
            )
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
                sheetRepository.insert(attendanceAll(edit, edit.id))
            }
        }
    }

    fun update(editBundle: AttEditBundle) {
        val current = editBundle.current
        val edit = editBundle.edit
        if (current != null && edit.savable && editBundle.anyDiff) {
            execute {
                val updated = attendanceAll(edit, current.id)
                sheetRepository.update(updated).getOrThrow()
                deleteImagesNotIn(current, updated)
                Result.success(Unit)
            }
        }
    }

    fun delete(current: MAttendanceAll) {
        execute {
            sheetRepository.delete<AttendanceAll>(current.id.toString()).getOrThrow()
            current.attendances
                .flatMap { it.images }
                .mapNotNull { it.remote?.path }
                .forEach { path -> runCatching { attendanceImageRepository.delete(path) } }
            Result.success(Unit)
        }
    }

    private suspend fun deleteImagesNotIn(
        current: MAttendanceAll,
        updated: AttendanceAll,
    ) {
        val retainedPaths = updated.attendances.flatMap { attendance -> attendance.images.map { it.path } }.toSet()
        current.attendances
            .flatMap { it.images }
            .mapNotNull { it.remote?.path }
            .filterNot(retainedPaths::contains)
            .forEach { path -> runCatching { attendanceImageRepository.delete(path) } }
    }

    private suspend fun attendanceAll(
        edit: AttAllEdit,
        id: Long,
    ) = coroutineScope {
            AttendanceAll(
                id = id,
                attendances =
                    edit.attSiteEdits.mapNoNull({ it.fulls.isNotEmpty() || it.halfs.isNotEmpty() }) { siteEdit ->
                        Attendance(
                            siteId = siteEdit.siteId,
                            fulls = siteEdit.fulls.map { it.id },
                            halfs = siteEdit.halfs.map { it.id },
                            remark = siteEdit.remark.orEmpty().trim(),
                            images =
                                siteEdit.images.map { image ->
                                    async {
                                        image.remote ?: attendanceImageRepository.upload(id, siteEdit.siteId, requireNotNull(image.localUri))
                                    }
                                }.awaitAll(),
                        )
                    },
            )
        }

    private fun <T> execute(block: suspend CoroutineScope.() -> Result<T>) {
        loading()
        viewModelScope.launch {
            runCatching {
                block().getOrThrow()
            }.onSuccess {
                clearEdit()
            }.onFailure(::throwable)
            noLoading()
        }
    }
}
