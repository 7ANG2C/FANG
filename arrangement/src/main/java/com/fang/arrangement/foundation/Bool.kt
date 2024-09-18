package com.fang.arrangement.foundation

internal interface Bool {
    companion object {
        const val TRUE = 1
        const val FALSE = 0

        operator fun invoke(value: Int) = value == TRUE

        operator fun invoke(value: String) = invoke(value.toIntOrNull() ?: FALSE)

        operator fun invoke(value: Boolean) = if (value) TRUE else FALSE
    }
}
