package com.fang.arrangement.ui.screen.btmnav.schedule

import com.fang.arrangement.definition.attendancesummary.Attendance as DataStoreAttendance
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fang.arrangement.definition.attendancesummary.AttendanceSummaryRepository
import com.fang.arrangement.definition.building.BuildingRepository
import com.fang.arrangement.ui.screen.btmnav.building.Site
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.launch

@OptIn(ExperimentalCoroutinesApi::class)
internal class ScheduleViewModel(
    private val summaryRepository: AttendanceSummaryRepository,
    private val sitesRepository: BuildingRepository
) : ViewModel() {

    private val _attendanceSummaries = MutableStateFlow(emptyList<AttendanceSummary>())
    val attendanceSummaries = _attendanceSummaries.asStateFlow()
    private val _sites = MutableStateFlow(emptyList<Site>())
    val sites = _sites.asStateFlow()

    init {
        viewModelScope.launch {
            sitesRepository.invoke()
                .mapLatest { sites ->
                    sites?.map {
                        Site(id = it.id, name = it.name)
                    }?.sortedByDescending { it.id }
                }
                .flowOn(Dispatchers.Default)
                .collectLatest { data ->
                    val sites = data.orEmpty()
                    _sites.value = sites
                }
        }
        viewModelScope.launch {
            summaryRepository.invoke()
                .mapLatest { summaries ->
                    summaries?.map { summary ->
                        AttendanceSummary(
                            createTimeMillis = summary.id,
                            attendances = summary.attendances.map {
                                Attendance(
                                    siteId = it.buildingId,
                                    attendanceCount = it.attendanceCount
                                )
                            }
                        )
                    }?.sortedByDescending { it.createTimeMillis }
                }
                .collectLatest {
                    _attendanceSummaries.value = it.orEmpty()
                }
        }
    }

    fun add(createTimeMillis: Long, attendances: List<Attendance>) {
        summaryRepository.add(
            createTimeMillis = createTimeMillis,
            attendances = attendances.map {
                DataStoreAttendance(it.siteId, it.attendanceCount)
            }
        )
    }

    fun edit(createTimeMillis: Long, attendances: List<Attendance>) {
        summaryRepository.edit(
            createTimeMillis = createTimeMillis,
            attendances = attendances.map {
                DataStoreAttendance(it.siteId, it.attendanceCount)
            }
        )
    }

    fun delete(createTimeMillis: Long) {
        summaryRepository.delete(createTimeMillis)
    }

}