package com.fang.arrangement.ui.screen.btmnav.employee

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fang.arrangement.definition.Employee
import com.fang.arrangement.definition.EmployeeKey
import com.fang.arrangement.definition.sheet.SheetRepository
import com.fang.arrangement.definition.sheet.sheetEmployee
import com.fang.arrangement.foundation.Bool
import com.fang.arrangement.foundation.noBreathing
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
internal class EmployeeViewModel(
    private val sheetRepository: SheetRepository,
    private val gson: Gson,
) : ViewModel(),
    WorkState by WorkStateImpl() {
    private val _employees = MutableStateFlow(emptyList<Employee>())
    val employees = _employees.asStateFlow()

    private val _editBundle = MutableStateFlow<EmployeeEditBundle?>(null)
    val editBundle = _editBundle.asStateFlow()
    private val _salaryEdit = MutableStateFlow(SalaryEdit.empty)
    val salaryEdit = _salaryEdit.asStateFlow()

    init {
        viewModelScope.launch {
            sheetRepository.workSheets
                .mapLatest { workSheets ->
                    workSheets
                        ?.sheetEmployee()
                        ?.values
                        ?.filterNot { it.isDelete }
                        ?.sortedWith(
                            compareByDescending<Employee> {
                                it.expiredMillis ?: Long.MAX_VALUE
                            }.thenByDescending { it.id },
                        )
                }.filterNotNull()
                .flowOn(Dispatchers.Default)
                .collectLatest {
                    _employees.value = it
                }
        }
    }

    fun onInsert() {
        _editBundle.value =
            EmployeeEditBundle(
                current = null,
                edit =
                    EmployeeEdit(
                        id = -1L,
                        name = null,
                        salaries = emptyList(),
                        expire = null,
                    ),
            )
    }

    fun onUpdate(current: Employee) {
        _editBundle.value =
            EmployeeEditBundle(
                current = current,
                edit =
                    EmployeeEdit(
                        id = current.id,
                        name = current.name,
                        salaries =
                            current.salaries.map {
                                SalaryEdit(it.millis, it.salary.toString())
                            },
                        expire = current.expiredMillis,
                    ),
            )
    }

    fun editName(name: String?) {
        _editBundle.update {
            it?.copy(edit = it.edit.copy(name = name.takeIfNotBlank))
        }
    }

    fun editSalaryMillis(millis: Long?) {
        _salaryEdit.update {
            it.copy(millis = millis)
        }
    }

    fun editSalary(salary: String?) {
        _salaryEdit.update {
            it.copy(salary = salary.takeIfNotBlank)
        }
    }

    fun addSalary(edit: SalaryEdit) {
        if (edit.allFilled) {
            _editBundle.update { old ->
                val new = listOf(edit) + old?.edit?.salaries.orEmpty()
                old?.copy(
                    edit =
                        old.edit.copy(
                            salaries = new.sortedByDescending { it.millis },
                        ),
                )
            }
            clearSalary()
        }
    }

    fun removeSalary(millis: Long) {
        _editBundle.update { old ->
            old?.copy(
                edit =
                    old.edit.copy(
                        salaries =
                            old.edit.salaries.filter {
                                it.millis != millis
                            },
                    ),
            )
        }
    }

    fun clearSalary() {
        _salaryEdit.value = SalaryEdit.empty
    }

    fun editExpire(expire: Long?) {
        _editBundle.update {
            it?.copy(edit = it.edit.copy(expire = expire))
        }
    }

    fun clearEdit() {
        _editBundle.value = null
    }

    fun insert(edit: EmployeeEdit) {
        if (edit.savable) {
            execute {
                sheetRepository.insert<Employee>(
                    keyValues =
                        EmployeeKey.fold(
                            id = System.currentTimeMillis().toString(),
                            name = "\"${edit.name.orEmpty().trim()}\"",
                            salaries = gson.json(edit.salaries).getOrNull()?.noBreathing ?: "[]",
                            expire = edit.expire?.toString().orEmpty(),
                            delete = Bool.FALSE.toString(),
                        ),
                )
            }
        }
    }

    fun update(editBundle: EmployeeEditBundle) {
        val id = editBundle.current?.id?.toString()
        val edit = editBundle.edit
        if (id != null && edit.savable && editBundle.anyDiff) {
            execute {
                sheetRepository.update<Employee>(
                    key = EmployeeKey.ID,
                    value = id,
                    keyValues =
                        EmployeeKey.fold(
                            id = id,
                            name = "\"${edit.name.orEmpty().trim()}\"",
                            salaries = gson.json(edit.salaries).getOrNull()?.noBreathing ?: "[]",
                            expire = edit.expire?.toString().orEmpty(),
                            delete = Bool.FALSE.toString(),
                        ),
                )
            }
        }
    }

    fun delete(current: Employee) {
        execute {
            val id = current.id.toString()
            sheetRepository.update<Employee>(
                key = EmployeeKey.ID,
                value = id,
                keyValues =
                    EmployeeKey.fold(
                        id = id,
                        name = "\"${current.name}\"",
                        salaries = gson.json(current.salaries).getOrNull()?.noBreathing ?: "[]",
                        expire = current.expiredMillis?.toString().orEmpty(),
                        delete = Bool.TRUE.toString(),
                    ),
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
