package com.fang.arrangement.ui.screen.btmnav.money.loan

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fang.arrangement.definition.Employee
import com.fang.arrangement.definition.Loan
import com.fang.arrangement.definition.LoanKey
import com.fang.arrangement.definition.sheet.SheetRepository
import com.fang.arrangement.definition.sheet.sheetEmployee
import com.fang.arrangement.definition.sheet.sheetLoan
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
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@OptIn(ExperimentalCoroutinesApi::class)
internal class LoanViewModel(
    private val sheetRepository: SheetRepository,
    private val gson: Gson,
) : ViewModel(),
    WorkState by WorkStateImpl() {
    private val _bundle = MutableStateFlow(LoanBundle(emptyList(), emptyList()))
    val bundle = _bundle.asStateFlow()

    private val _editBundle = MutableStateFlow<LoanEditBundle?>(null)
    val editBundle = _editBundle.asStateFlow()

    private val _recordEdit = MutableStateFlow(RecordEdit.empty)
    val recordEdit = _recordEdit.asStateFlow()

    private val _filter = MutableStateFlow<Employee?>(null)
    val filter = _filter.asStateFlow()

    init {
        viewModelScope.launch {
            sheetRepository.workSheets
                .mapLatest { workSheets ->
                    val employees =
                        workSheets
                            ?.sheetEmployee()
                            ?.values
                            .orEmpty()
                            .sortedWith(
                                compareBy<Employee> { it.delete }
                                    .thenByDescending { it.expiredMillis ?: Long.MAX_VALUE }
                                    .thenByDescending { it.id },
                            )
                    workSheets
                        ?.sheetLoan()
                        ?.values
                        ?.map { loan ->
                            val records = loan.records
                            MLoan(
                                id = loan.id,
                                employee =
                                    employees.find {
                                        it.id == loan.employeeId
                                    } ?: Employee(
                                        id = loan.employeeId,
                                        name = "",
                                        salaries = emptyList(),
                                        expiredMillis = null,
                                        delete = 1,
                                    ),
                                loan = loan.loan,
                                millis = loan.millis,
                                records = records,
                                remark = loan.remark,
                            )
                        }?.sortedWith(
                            compareBy<MLoan> { it.isClear }
                                .thenByDescending { it.employee.id }
                                .thenByDescending { it.millis }
                                .thenByDescending { it.loan },
                        )?.let {
                            LoanBundle(employees, it)
                        }
                }.flatMapLatest { bundle ->
                    filter.mapLatest { filter ->
                        bundle?.let { b ->
                            filter?.let {
                                b.copy(loans = b.loans.filter { it.employee.id == filter.id })
                            } ?: b
                        }
                    }
                }.filterNotNull()
                .flowOn(Dispatchers.Default)
                .collectLatest {
                    _bundle.value = it
                }
        }
    }

    fun filterEmployee(employee: Employee?) {
        _filter.value = employee
    }

    fun onInsert() {
        _editBundle.value =
            LoanEditBundle(
                current = null,
                edit =
                    LoanEdit(
                        id = -1,
                        employee = null,
                        loan = null,
                        millis = null,
                        records = emptyList(),
                        remark = null,
                    ),
            )
    }

    fun onUpdate(current: MLoan) {
        _editBundle.value =
            LoanEditBundle(
                current = current,
                edit =
                    LoanEdit(
                        id = current.id,
                        employee = current.employee,
                        loan = current.loan.toString(),
                        millis = current.millis,
                        records =
                            current.records.map {
                                RecordEdit(
                                    millis = it.millis,
                                    loan = it.loan.toString(),
                                    remark = it.remark,
                                )
                            },
                        remark = current.remark,
                    ),
            )
    }

    fun editEmployee(employee: Employee?) {
        _editBundle.update {
            it?.copy(edit = it.edit.copy(employee = employee))
        }
    }

    fun editLoan(loan: String?) {
        _editBundle.update {
            it?.copy(edit = it.edit.copy(loan = loan.takeIfNotBlank))
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

    fun editRecordLoan(loan: String?) {
        _recordEdit.update { old ->
            old.copy(loan = loan.takeIfNotBlank)
        }
    }

    fun editRecordRemark(remark: String?) {
        _recordEdit.update {
            it.copy(remark = remark.takeIfNotBlank?.take(Remark.L30))
        }
    }

    fun addRecord(edit: RecordEdit) {
        if (edit.millis != null && edit.loan != null) {
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

    fun insert(edit: LoanEdit) {
        if (edit.savable) {
            execute {
                sheetRepository.insert<Loan>(
                    keyValues =
                        LoanKey.fold(
                            id = System.currentTimeMillis().toString(),
                            employeeId =
                                edit.employee
                                    ?.id
                                    ?.toString()
                                    .orEmpty(),
                            loan = edit.loan.orEmpty(),
                            millis = edit.millis?.toString().orEmpty(),
                            records = "[]",
                            remark = "\"${edit.remark.takeIfNotBlank.orEmpty().trim()}\"",
                        ),
                )
            }
        }
    }

    fun update(editBundle: LoanEditBundle) {
        val id = editBundle.current?.id?.toString()
        val edit = editBundle.edit
        if (id != null && edit.savable && editBundle.anyDiff) {
            execute {
                sheetRepository.update<Loan>(
                    key = LoanKey.ID,
                    value = id,
                    keyValues =
                        LoanKey.fold(
                            id = id,
                            employeeId =
                                edit.employee
                                    ?.id
                                    ?.toString()
                                    .orEmpty(),
                            loan = edit.loan.orEmpty(),
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
            sheetRepository.delete<Loan>(key = LoanKey.ID, value = id)
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
