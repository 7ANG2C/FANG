package com.fang.arrangement.definition

import com.fang.arrangement.definition.foundation.KeyValue
import com.fang.arrangement.foundation.Bool
import com.google.gson.annotations.SerializedName

internal data class Employee(
    @SerializedName(EmployeeKey.ID)
    val id: Long,
    @SerializedName(EmployeeKey.NAME)
    val name: String,
    @SerializedName(EmployeeKey.SALARIES)
    val salaries: List<Salary>,
    @SerializedName(EmployeeKey.EXPIRED)
    val expiredMillis: Long?,
    @SerializedName(EmployeeKey.DELETE)
    val delete: Int,
) {
    val isExpire get() = expiredMillis != null
    val notExpire get() = !isExpire
    val isDelete get() = Bool(delete)
    val notDelete get() = !isDelete
}

internal data class Salary(
    @SerializedName(EmployeeKey.S_MILLIS)
    val millis: Long,
    @SerializedName(EmployeeKey.S_SALARY)
    val salary: Int,
)

internal interface EmployeeKey {
    companion object {
        const val ID = "id"
        const val NAME = "name"
        const val SALARIES = "salaries"
        const val EXPIRED = "expired"
        const val DELETE = "delete"
        const val S_MILLIS = "millis"
        const val S_SALARY = "salary"

        fun fold(
            id: String,
            name: String,
            salaries: String,
            expire: String,
            delete: String,
        ) = listOf(
            KeyValue(ID, id),
            KeyValue(NAME, name),
            KeyValue(SALARIES, salaries),
            KeyValue(EXPIRED, expire),
            KeyValue(DELETE, delete),
        )
    }
}
