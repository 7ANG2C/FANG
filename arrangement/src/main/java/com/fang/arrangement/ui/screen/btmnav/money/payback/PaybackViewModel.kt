package com.fang.arrangement.ui.screen.btmnav.money.payback

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fang.arrangement.definition.Boss
import com.fang.arrangement.definition.BossKey
import com.fang.arrangement.definition.Payback
import com.fang.arrangement.definition.PaybackKey
import com.fang.arrangement.definition.sheet.SheetRepository
import com.fang.arrangement.definition.sheet.sheetBoss
import com.fang.arrangement.definition.sheet.sheetPayback
import com.fang.arrangement.foundation.Bool
import com.fang.arrangement.foundation.noBreathing
import com.fang.arrangement.ui.shared.dsl.Remark
import com.fang.cosmos.definition.workstate.WorkState
import com.fang.cosmos.definition.workstate.WorkStateImpl
import com.fang.cosmos.foundation.json
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
internal class PaybackViewModel(
    private val sheetRepository: SheetRepository,
    private val gson: Gson,
) : ViewModel(), WorkState by WorkStateImpl() {
    private val _bundle = MutableStateFlow(PaybackBundle(emptyList(), emptyList()))
    val bundle = _bundle.asStateFlow()

    private val _editBundle = MutableStateFlow<PaybackEditBundle?>(null)
    val editBundle = _editBundle.asStateFlow()

    private val _recordEdit = MutableStateFlow(RecordEdit.empty)
    val recordEdit = _recordEdit.asStateFlow()

    init {
        viewModelScope.launch {
            sheetRepository.workSheets
                .mapLatest { workSheets ->
                    val bosses =
                        workSheets?.sheetBoss()?.values.orEmpty()
                            .sortedWith(
                                compareBy<Boss> { it.delete }
                                    .thenByDescending { it.id },
                            )
                    workSheets?.sheetPayback()?.values
                        ?.map { payback ->
                            val records = payback.records
                            MPayback(
                                id = payback.id,
                                boss =
                                    bosses.find {
                                        it.id == payback.bossId
                                    } ?: Boss(
                                        id = payback.bossId,
                                        name = "",
                                        delete = 1,
                                    ),
                                payback = payback.payback,
                                millis = payback.millis,
                                records = records,
                                remark = payback.remark,
                            )
                        }
                        ?.sortedWith(
                            compareBy<MPayback> { it.isClear }
                                .thenByDescending { it.millis }
                                .thenByDescending { it.payback }
                                .thenByDescending { it.boss.id },
                        )?.let {
                            PaybackBundle(bosses, it)
                        }
                }
                .filterNotNull()
                .flowOn(Dispatchers.Default)
                .collectLatest {
                    _bundle.value = it
                }
        }
    }

    fun onInsert() {
        _editBundle.value =
            PaybackEditBundle(
                current = null,
                edit =
                    PaybackEdit(
                        id = -1,
                        boss = null,
                        payback = null,
                        millis = null,
                        records = emptyList(),
                        remark = null,
                    ),
            )
    }

    fun onUpdate(current: MPayback) {
        _editBundle.value =
            PaybackEditBundle(
                current = current,
                edit =
                    PaybackEdit(
                        id = current.id,
                        boss = current.boss,
                        payback = current.payback.toString(),
                        millis = current.millis,
                        records =
                            current.records.map {
                                RecordEdit(
                                    millis = it.millis,
                                    payback = it.payback.toString(),
                                    remark = it.remark,
                                )
                            },
                        remark = current.remark,
                    ),
            )
    }

    fun editBoss(boss: Boss?) {
        _editBundle.update {
            it?.copy(edit = it.edit.copy(boss = boss))
        }
    }

    fun editPayback(payback: String?) {
        _editBundle.update {
            it?.copy(edit = it.edit.copy(payback = payback.takeIfNotBlank))
        }
    }

    fun editMillis(millis: Long?) {
        _editBundle.update {
            it?.copy(edit = it.edit.copy(millis = millis))
        }
    }

    fun editRecordMillis(millis: Long?) {
        _recordEdit.update {
            it.copy(millis = millis)
        }
    }

    fun editRecordPayback(payback: String?) {
        _recordEdit.update { old ->
            old.copy(payback = payback.takeIfNotBlank)
        }
    }

    fun editRecordRemark(remark: String?) {
        _recordEdit.update {
            it.copy(remark = remark.takeIfNotBlank?.take(Remark.L30))
        }
    }

    fun addRecord(edit: RecordEdit) {
        if (edit.millis != null && edit.payback != null) {
            _editBundle.update { old ->
                val new = listOf(edit) + old?.edit?.records.orEmpty()
                old?.copy(
                    edit = old.edit.copy(records = new.sortedByDescending { it.millis }),
                )
            }
            clearRecord()
        }
    }

    fun removeRecord(millis: Long) {
        _editBundle.update { old ->
            old?.copy(
                edit =
                    old.edit.copy(
                        records = old.edit.records.filterNot { it.millis == millis },
                    ),
            )
        }
    }

    fun clearRecord() {
        _recordEdit.value = RecordEdit.empty
    }

    fun editRemark(remark: String?) {
        _editBundle.update {
            it?.copy(
                edit = it.edit.copy(remark = remark.takeIfNotBlank?.take(Remark.L50)),
            )
        }
    }

    fun clearEdit() {
        _editBundle.value = null
    }

    fun insert(edit: PaybackEdit) {
        if (edit.savable) {
            execute {
                sheetRepository.insert<Payback>(
                    keyValues =
                        PaybackKey.fold(
                            id = System.currentTimeMillis().toString(),
                            bossId = edit.boss?.id?.toString().orEmpty(),
                            payback = edit.payback.orEmpty(),
                            millis = edit.millis?.toString().orEmpty(),
                            records = "[]",
                            remark = "\"${edit.remark.takeIfNotBlank.orEmpty().trim()}\"",
                        ),
                )
            }
        }
    }

    fun update(editBundle: PaybackEditBundle) {
        val id = editBundle.current?.id?.toString()
        val edit = editBundle.edit
        if (id != null && edit.savable && editBundle.anyDiff) {
            execute {
                sheetRepository.update<Payback>(
                    key = PaybackKey.ID,
                    value = id,
                    keyValues =
                        PaybackKey.fold(
                            id = id,
                            bossId = edit.boss?.id?.toString().orEmpty(),
                            payback = edit.payback.orEmpty(),
                            millis = edit.millis?.toString().orEmpty(),
                            records = gson.json(edit.records).getOrNull()?.noBreathing ?: "[]",
                            remark = "\"${edit.remark.takeIfNotBlank.orEmpty().trim()}\"",
                        ),
                )
            }
        }
    }

    fun delete(id: String) {
        execute {
            sheetRepository.delete<Payback>(key = PaybackKey.ID, value = id)
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

    private val _bossEditBundle = MutableStateFlow<BossEditBundle?>(null)
    val bossEditBundle = _bossEditBundle.asStateFlow()

    fun bossOnInsert() {
        _bossEditBundle.value =
            BossEditBundle(
                current = null,
                edit =
                    BossEdit(
                        id = -1L,
                        name = null,
                    ),
            )
    }

    fun bossOnUpdate(current: Boss) {
        _bossEditBundle.value =
            BossEditBundle(
                current = current,
                edit =
                    BossEdit(
                        id = current.id,
                        name = current.name,
                    ),
            )
    }

    fun editName(name: String?) {
        _bossEditBundle.update {
            it?.copy(edit = it.edit.copy(name = name.takeIfNotBlank))
        }
    }

    fun bossClearEdit() {
        _bossEditBundle.value = null
    }

    fun bossInsert(edit: BossEdit) {
        if (edit.savable) {
            bossExecute {
                sheetRepository.insert<Boss>(
                    keyValues =
                        BossKey.fold(
                            id = System.currentTimeMillis().toString(),
                            name = "\"${edit.name.orEmpty().trim()}\"",
                            delete = Bool.FALSE.toString(),
                        ),
                )
            }
        }
    }

    fun bossUpdate(editBundle: BossEditBundle) {
        val id = editBundle.current?.id?.toString()
        val edit = editBundle.edit
        if (id != null && edit.savable && editBundle.anyDiff) {
            bossExecute {
                sheetRepository.update<Boss>(
                    key = BossKey.ID,
                    value = id,
                    keyValues =
                        BossKey.fold(
                            id = id,
                            name = "\"${edit.name.orEmpty().trim()}\"",
                            delete = Bool.FALSE.toString(),
                        ),
                )
            }
        }
    }

    fun bossDelete(current: Boss) {
        bossExecute {
            val id = current.id.toString()
            sheetRepository.update<Boss>(
                key = BossKey.ID,
                value = id,
                keyValues =
                    BossKey.fold(
                        id = id,
                        name = "\"${current.name}\"",
                        delete = Bool.TRUE.toString(),
                    ),
            )
        }
    }

    private fun <T> bossExecute(block: suspend CoroutineScope.() -> Result<T>) {
        loading()
        viewModelScope.launch {
            block().onSuccess {
                bossClearEdit()
            }.onFailure(::throwable)
            noLoading()
        }
    }
}
