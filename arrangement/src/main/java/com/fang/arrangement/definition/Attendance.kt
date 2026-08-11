package com.fang.arrangement.definition

internal data class AttendanceAll(
    val id: Long,
    val attendances: List<Attendance>,
)

internal data class Attendance(
    val siteId: Long,
    val fulls: List<Long>,
    val halfs: List<Long>,
    val remark: String?,
    val images: List<AttendanceImage>,
) {
    val total get() = fulls.size + halfs.size * 0.5
}

internal data class AttendanceImage(
    val path: String,
    val downloadUrl: String,
)
