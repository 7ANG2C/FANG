package com.fang.arrangement.ui.screen.btmnav.attendance

import android.net.Uri
import com.fang.arrangement.definition.AttendanceImage
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
    val remark: String?,
    val images: List<MAttendanceImage>,
) {
    companion object {
        const val MAX_IMAGE_COUNT = 3
        val empty by lazy { MAttendance(-1L, null, emptyList(), emptyList(), null, emptyList()) }
    }
}

internal data class MAttendanceImage(
    val remote: AttendanceImage? = null,
    val localUri: Uri? = null,
) {
    val displayModel get() = localUri ?: remote?.downloadUrl
}

internal data class MEmployee(
    val id: Long,
    val employee: Employee?,
)
