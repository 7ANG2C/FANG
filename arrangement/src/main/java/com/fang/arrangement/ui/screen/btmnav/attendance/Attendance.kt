package com.fang.arrangement.ui.screen.btmnav.attendance

import android.net.Uri
import com.fang.arrangement.definition.Attendance
import com.fang.arrangement.definition.Employee
import com.fang.arrangement.definition.Site

internal data class MAttendanceAll(
    val id: Long,
    val attendances: List<MAttendance>,
)

internal data class MAttendance(
    val siteId: Long,
    val site: Site?,
    val fulls: List<MEmployee>,
    val halfs: List<MEmployee>,
    val overtimes: List<Overtime>,
    val remark: String?,
    val images: List<Image>,
) {
    internal data class Overtime(
        val employee: MEmployee,
        val count: Double,
    )

    internal data class Image(
        val remote: Attendance.Image? = null,
        val localUri: Uri? = null,
    ) {
        val displayModel get() = localUri ?: remote?.downloadUrl
    }

    internal data class MEmployee(
        val id: Long,
        val employee: Employee?,
    )

    companion object {
        const val MAX_IMAGE_COUNT = 3
        val empty by lazy { MAttendance(-1L, null, emptyList(), emptyList(), emptyList(), null, emptyList()) }
    }

    val total get() = fulls.size + halfs.size * 0.5 + overtimes.sumOf { it.count }
}
