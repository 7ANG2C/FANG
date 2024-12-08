package com.fang.arrangement.definition

import com.fang.arrangement.definition.foundation.KeyValue
import com.fang.arrangement.foundation.Bool
import com.google.gson.annotations.SerializedName

internal data class Boss(
    @SerializedName(BossKey.ID)
    val id: Long,
    @SerializedName(BossKey.NAME)
    val name: String,
    @SerializedName(BossKey.DELETE)
    val delete: Int,
) {
    val isDelete get() = Bool(delete)
    val notDelete get() = !isDelete
}

internal interface BossKey {
    companion object {
        const val ID = "id"
        const val NAME = "name"
        const val DELETE = "delete"

        fun fold(
            id: String,
            name: String,
            delete: String,
        ) = listOf(
            KeyValue(ID, id),
            KeyValue(NAME, name),
            KeyValue(DELETE, delete),
        )
    }
}
