package com.fang.arrangement.definition

import com.fang.arrangement.foundation.Bool

internal data class Boss(
    val id: Long,
    val name: String,
    val delete: Int,
) {
    val isDelete get() = Bool(delete)
    val notDelete get() = !isDelete
}
