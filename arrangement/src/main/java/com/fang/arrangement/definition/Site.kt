package com.fang.arrangement.definition

import com.fang.arrangement.definition.foundation.KeyValue
import com.fang.arrangement.foundation.Bool
import com.google.gson.annotations.SerializedName

internal data class Site(
    @SerializedName(SiteKey.ID)
    val id: Long,
    @SerializedName(SiteKey.NAME)
    val name: String,
    @SerializedName(SiteKey.INCOME)
    val income: Int?,
    @SerializedName(SiteKey.ADDRESS)
    val address: String?,
    @SerializedName(SiteKey.START_MILLIS)
    val startMillis: Long?,
    @SerializedName(SiteKey.END_MILLIS)
    val endMillis: Long?,
    @SerializedName(SiteKey.ARCHIVE)
    val archive: Int,
    @SerializedName(SiteKey.DELETE)
    val delete: Int,
) {
    val isArchive get() = Bool(archive)
    val isDelete get() = Bool(delete)
}

internal interface SiteKey {
    companion object {
        const val ID = "id"
        const val NAME = "name"
        const val INCOME = "income"
        const val ADDRESS = "address"
        const val START_MILLIS = "start"
        const val END_MILLIS = "end"
        const val ARCHIVE = "archive"
        const val DELETE = "delete"

        fun fold(
            id: String,
            name: String,
            income: String,
            address: String,
            startMillis: String,
            endMillis: String,
            archive: String,
            delete: String,
        ) = listOf(
            KeyValue(ID, id),
            KeyValue(NAME, name),
            KeyValue(INCOME, income),
            KeyValue(ADDRESS, address),
            KeyValue(START_MILLIS, startMillis),
            KeyValue(END_MILLIS, endMillis),
            KeyValue(ARCHIVE, archive),
            KeyValue(DELETE, delete),
        )
    }
}
