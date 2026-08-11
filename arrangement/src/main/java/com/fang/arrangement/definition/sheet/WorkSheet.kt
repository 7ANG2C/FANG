package com.fang.arrangement.definition.sheet

internal data class WorkSheet(
    val values: List<Any>,
    val clazz: Class<out Any>,
)
