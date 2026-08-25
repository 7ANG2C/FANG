package com.fang.arrangement.definition

internal data class AttendanceAll(
    val id: Long,
    val attendances: List<Attendance>,
)

internal data class Attendance(
    val siteId: Long,
    val fulls: List<Long>,
    val halfs: List<Long>,
    val overtimes: List<Overtime>,
    val remark: String?,
    val images: List<Image>,
) {
    internal data class Overtime(
        val employeeId: Long,
        val count: Double,
    )

    internal data class Image(
        val path: String,
        val downloadUrl: String,
    )

    val total get() = fulls.size + halfs.size * 0.5 + overtimes.sumOf { it.count }
}
