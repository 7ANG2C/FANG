package com.fang.arrangement.definition

import com.fang.arrangement.foundation.Bool

internal data class Employee(
    val id: Long,
    val name: String,
    val salaries: List<Salary>,
    val expiredMillis: Long?,
    val delete: Int,
    val order: Int,
) {
    val isExpire get() = expiredMillis != null
    val notExpire get() = !isExpire
    val isDelete get() = Bool(delete)
    val notDelete get() = !isDelete
}

internal data class Salary(
    val millis: Long,
    val salary: Int,
)
