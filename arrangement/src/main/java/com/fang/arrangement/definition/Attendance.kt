package com.fang.arrangement.definition

import com.fang.arrangement.definition.foundation.KeyValue
import com.google.gson.annotations.SerializedName

internal data class AttendanceAll(
    @SerializedName(AttendanceKey.ID)
    val id: Long,
    @SerializedName(AttendanceKey.ATTENDANCES)
    val attendances: List<Attendance>,
)

internal data class Attendance(
    @SerializedName(AttendanceKey.SITE_ID)
    val siteId: Long,
    @SerializedName(AttendanceKey.FULL)
    val fulls: List<Long>,
    @SerializedName(AttendanceKey.HALF)
    val halfs: List<Long>,
)

internal interface AttendanceKey {
    companion object {
        const val ID = "millis"
        const val ATTENDANCES = "attendances"
        const val SITE_ID = "id"
        const val FULL = "full"
        const val HALF = "half"

        fun fold(
            id: String,
            attendances: String,
        ) = listOf(
            KeyValue(ID, id),
            KeyValue(ATTENDANCES, attendances),
        )
    }
}