package com.fang.arrangement.foundation

internal interface Bool {
    companion object {
        operator fun invoke(value: Int) = value == 1
    }
}
