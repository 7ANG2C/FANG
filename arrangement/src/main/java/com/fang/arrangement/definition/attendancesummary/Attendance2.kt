package com.fang.arrangement.definition.attendancesummary

import com.google.gson.annotations.SerializedName

internal data class Attendance2(
    @SerializedName("attBuildingId")
    val buildingId: Long,
    @SerializedName("attAttendanceCount")
    val attendanceCount: List<EmplyeeState>,
)

internal data class EmplyeeState(
    val e: Long,
    val state: State
) {
    enum class State {
        FULL, HALF, NONE
    }
}