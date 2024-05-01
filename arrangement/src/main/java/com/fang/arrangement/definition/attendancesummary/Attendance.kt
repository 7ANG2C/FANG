package com.fang.arrangement.definition.attendancesummary

import com.google.gson.annotations.SerializedName

internal data class Attendance(
    @SerializedName("attBuildingId")
    val buildingId: Long,
    @SerializedName("attAttendanceCount")
    val attendanceCount: Double,
)