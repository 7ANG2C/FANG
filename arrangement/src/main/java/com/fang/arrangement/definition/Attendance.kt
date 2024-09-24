package com.fang.arrangement.definition

import com.fang.arrangement.definition.foundation.KeyValue
import com.google.gson.annotations.SerializedName

internal data class AttendanceAll(
    @SerializedName(AttendanceKey.ID)
    val id: Long,
    @SerializedName(AttendanceKey.ATTENDANCES)
    val attendances: List<Attendance>,
    @SerializedName(AttendanceKey.REMARK)
    val remark: String?,
)

internal data class Attendance(
    @SerializedName(AttendanceKey.SITE_ID)
    val siteId: Long,
    @SerializedName(AttendanceKey.FULL)
    val fulls: List<Long>,
    @SerializedName(AttendanceKey.HALF)
    val halfs: List<Long>,
    @SerializedName(AttendanceKey.A_REMARK)
    val remark: String?,
) {
    val total get() = fulls.size + halfs.size * 0.5
}

internal interface AttendanceKey {
    companion object {
        const val ID = "millis"
        const val ATTENDANCES = "attendances"
        const val REMARK = "remark"
        const val SITE_ID = "id"
        const val FULL = "full"
        const val HALF = "half"
        const val A_REMARK = "remark"

        fun fold(
            id: String,
            attendances: String,
            remark: String,
        ) = listOf(
            KeyValue(ID, id),
            KeyValue(ATTENDANCES, attendances),
            KeyValue(REMARK, remark),
        )
    }
}
