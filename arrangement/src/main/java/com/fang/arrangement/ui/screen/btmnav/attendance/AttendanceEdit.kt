package com.fang.arrangement.ui.screen.btmnav.attendance

internal data class AttEditBundle(
    val current: MAttendanceAll?,
    val edit: AttAllEdit,
) {
    val isInsert get() = current == null
    val anyDiff
        get() =
            current?.id != edit.id ||
                current
                    ?.attendances
                    .orEmpty()
                    .filter { it.fulls.isNotEmpty() || it.halfs.isNotEmpty() } !=
                edit.attSiteEdits.filter { it.fulls.isNotEmpty() || it.halfs.isNotEmpty() }
}

internal data class AttAllEdit(
    val id: Long?,
    val attSiteEdits: List<MAttendance>,
) {
    val savable
        get() =
            id != null && attSiteEdits.sumOf { it.fulls.size + it.halfs.size } > 0
}
