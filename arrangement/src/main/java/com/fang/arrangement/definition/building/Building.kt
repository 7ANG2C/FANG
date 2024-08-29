package com.fang.arrangement.definition.building

import com.google.gson.annotations.SerializedName

internal data class Building(
    @SerializedName("buildingId")
    val id: Long,
    @SerializedName("buildingName")
    val name: String,
    @SerializedName("income")
    val income: Long = 0,
)
