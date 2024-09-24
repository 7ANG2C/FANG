package com.fang.arrangement.ui.screen.btmnav.money.fund

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fang.arrangement.definition.Fund
import com.fang.arrangement.definition.FundKey
import com.fang.arrangement.definition.sheet.SheetRepository
import com.fang.arrangement.definition.sheet.sheetFund
import com.fang.arrangement.ui.shared.dsl.Remark
import com.fang.cosmos.definition.workstate.WorkState
import com.fang.cosmos.definition.workstate.WorkStateImpl
import com.fang.cosmos.foundation.takeIfNotBlank
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
internal class FundViewModel(
    private val sheetRepository: SheetRepository,
) : ViewModel(), WorkState by WorkStateImpl() {
    private val _funds = MutableStateFlow(emptyList<Fund>())
    val funds = _funds.asStateFlow()

    private val _editBundle = MutableStateFlow<FundEditBundle?>(null)
    val editBundle = _editBundle.asStateFlow()

    init {
        viewModelScope.launch {
            sheetRepository.workSheets
                .mapLatest { workSheets ->
                    val funds = workSheets?.sheetFund()?.values?.sortedByDescending { it.millis }
                    funds
                }
                .filterNotNull()
                .flowOn(Dispatchers.Default)
                .collectLatest {
                    _funds.value = it
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
                        millis = null,
                        remark = null,
                    ),
            )
    }

    fun onUpdate(current: Fund) {
        _editBundle.value =
            FundEditBundle(
                current = current,
                edit =
                    FundEdit(
                        id = current.id,
                        fund = current.fund.toString(),
                        millis = current.millis,
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

    fun insert(edit: FundEdit) {
        if (edit.savable) {
            execute {
                sheetRepository.insert<Fund>(
                    keyValues =
                        FundKey.fold(
                            id = System.currentTimeMillis().toString(),
                            fund = edit.fund.orEmpty(),
                            millis = edit.millis?.toString().orEmpty(),
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
                            remark = "\"${edit.remark.orEmpty().trim()}\"",
                        ),
                )
            }
        }
    }

    fun delete(id: String) {
        execute {
            sheetRepository.delete<Fund>(key = FundKey.ID, value = id)
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
