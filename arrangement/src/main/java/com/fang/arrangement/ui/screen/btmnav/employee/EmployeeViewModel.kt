package com.fang.arrangement.ui.screen.btmnav.employee

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fang.arrangement.definition.attendancesummary.AttendanceSummaryRepository
import com.fang.arrangement.definition.employee.EmployeeRepository
import com.fang.arrangement.definition.loan.LoanRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.launch

@OptIn(ExperimentalCoroutinesApi::class)
internal class EmployeeViewModel(
    private val attendanceSummaryRepository: AttendanceSummaryRepository,
    private val loanRepository: LoanRepository,
    private val repository: EmployeeRepository
) : ViewModel() {

    private val _sites = MutableStateFlow(emptyList<Employee>())
    val sites = _sites.asStateFlow()

    init {
        viewModelScope.launch {
            combine(
                attendanceSummaryRepository.invoke(),
                repository.invoke(),
                ::Pair
            )
                .mapLatest { (aaa, sites) ->
                    aaa?.map { it.id }.orEmpty() to sites?.map {
                        Employee(id = it.id, name = it.name)
                    }?.sortedByDescending { it.id }.orEmpty()
                }
                .distinctUntilChanged()
                .flowOn(Dispatchers.Default)
                .collectLatest { (aaa, data) ->
                    val sites = data
                    _sites.value = sites
                    attendanceSummaryRepository.removeInvalidEmployees(aaa)
                    loanRepository.removeInvalidIds(sites.map { it.id })
                }
        }
    }

    fun add(name: String) {
        repository.add(name)
    }

    fun edit(id: Long, name: String) {
        repository.edit(id, name)
    }

    fun delete(id: Long) {
        repository.delete(id)
    }

}