package com.fang.arrangement.definition.sheet

internal data class WorkSheet(
    val id: Int,
    val name: String,
    val keys: List<String>,
    val values: List<Any>,
    val clazz: Class<out Any>,
)
