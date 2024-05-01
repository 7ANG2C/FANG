package com.fang.arrangement.definition.protocol

internal interface Target : Id {
    override val id: Long
    val name: String
}
