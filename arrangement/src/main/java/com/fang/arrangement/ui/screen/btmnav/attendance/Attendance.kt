package com.fang.arrangement.ui.screen.btmnav.attendance

import com.fang.arrangement.definition.Employee
import com.fang.arrangement.definition.Site

internal data class MAttendanceAll(
    val id: Long,
    val attendances: List<MAttendance>,
    val remark: String?,
)

internal data class MAttendance(
    val siteId: Long,
    val site: Site?,
    val fulls: List<MEmployee>,
    val halfs: List<MEmployee>,
) {
    companion object {
        val empty by lazy { MAttendance(-1L, null, emptyList(), emptyList()) }
    }
}

internal data class MEmployee(
    val id: Long,
    val employee: Employee?,
)
