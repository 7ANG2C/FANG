package com.fang.arrangement.ui.screen.btmnav.attendance

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
) {
    companion object {
        val empty by lazy { MAttendance(-1L, null, emptyList(), emptyList(), null) }
    }
}

internal data class MEmployee(
    val id: Long,
    val employee: Employee?,
)
