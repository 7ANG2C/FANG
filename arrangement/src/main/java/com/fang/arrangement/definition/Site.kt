package com.fang.arrangement.definition

import com.fang.arrangement.foundation.Bool

internal data class Site(
    val id: Long,
    val name: String,
    val address: String?,
    val income: Int?,
    val startMillis: Long?,
    val endMillis: Long?,
    val archive: Int,
    val delete: Int,
) {
    val isArchive get() = Bool(archive)
    val notArchive get() = !isArchive
    val isDelete get() = Bool(delete)
    val notDelete get() = !isDelete
}
