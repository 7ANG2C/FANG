package com.fang.arrangement.definition.attendancesummary

import com.fang.arrangement.definition.DataStoreKey
import com.fang.cosmos.definition.datastore.QualifierAwareDataStore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

internal class AttendanceSummaryRepository(
    private val dataStore: QualifierAwareDataStore,
    private val scope: CoroutineScope
) {
    private val key = DataStoreKey.attendanceSummary

    fun invoke() = dataStore.getDataFlow<List<AttendanceSummary>>(key)
    fun add(createTimeMillis: Long, attendances: List<Attendance>) {
        scope.launch {
            dataStore.update<List<AttendanceSummary>>(key = key) { old ->
                old.orEmpty() + AttendanceSummary(
                    id = createTimeMillis, attendances = attendances
                )
            }
        }
    }

    fun edit(createTimeMillis: Long, attendances: List<Attendance>) {
        scope.launch {
            dataStore.update<List<AttendanceSummary>>(key = key) { old ->
                old?.map {
                    if (it.id == createTimeMillis) {
                        it.copy(attendances = attendances)
                    } else it
                }
            }
        }
    }

    fun delete(createTimeMillis: Long) {
        scope.launch {
            dataStore.update<List<AttendanceSummary>>(key = key) { old ->
                old?.mapNotNull {
                    if (it.id == createTimeMillis) null else it
                }
            }
        }
    }

    fun removeInvalidSites(ids: List<Long>) {
        scope.launch {
            dataStore.update<List<AttendanceSummary>>(key = key) { old ->
                old?.mapNotNull { summary ->
                    summary.attendances.mapNotNull {
                        if (it.buildingId in ids && it.attendanceCount > 0.0) it else null
                    }.takeIf { it.isNotEmpty() }?.let {
                        summary.copy(attendances = it)
                    }
                }
            }
        }
    }

    fun removeInvalidEmployees(ids: List<Long>) {
        scope.launch {
            dataStore.update<List<AttendanceSummary>>(key = key) { old ->
                old?.mapNotNull { summary ->
                    summary.attendances.mapNotNull {
                        if (it.buildingId in ids && it.attendanceCount > 0.0) it else null
                    }.takeIf { it.isNotEmpty() }?.let {
                        summary.copy(attendances = it)
                    }
                }
            }
        }
    }

}