package com.fang.arrangement.definition.attendancesummary

import com.google.gson.annotations.SerializedName

internal data class AttendanceSummary(
    @SerializedName("attSumId")
    val id: Long,
    @SerializedName("attSumAttendances")
    val attendances: List<Attendance>,
)
