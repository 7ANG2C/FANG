package com.fang.arrangement.ui.screen.btmnav.building

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fang.arrangement.definition.attendancesummary.AttendanceSummaryRepository
import com.fang.arrangement.definition.building.BuildingRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.launch

@OptIn(ExperimentalCoroutinesApi::class)
internal class SiteViewModel(
    private val attendanceSummaryRepository: AttendanceSummaryRepository,
    private val repository: BuildingRepository
) : ViewModel() {

    private val _sites = MutableStateFlow(emptyList<Site>())
    val sites = _sites.asStateFlow()

    init {
        viewModelScope.launch {
            repository.invoke()
                .mapLatest { sites ->
                    sites?.map {
                        Site(id = it.id, name = it.name)
                    }?.sortedByDescending { it.id }
                }
                .flowOn(Dispatchers.Default)
                .collectLatest { data ->
                    val sites = data.orEmpty()
                    _sites.value = sites
                    attendanceSummaryRepository.removeInvalidSites(sites.map { it.id })
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