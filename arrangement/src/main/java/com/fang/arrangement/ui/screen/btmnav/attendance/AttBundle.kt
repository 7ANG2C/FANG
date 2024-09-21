package com.fang.arrangement.ui.screen.btmnav.attendance

import com.fang.arrangement.definition.Employee
import com.fang.arrangement.definition.Site

internal data class AttBundle(
    val sites: List<Site>,
    val employees: List<Employee>,
    val attAlls: List<MAttendanceAll>,
)
