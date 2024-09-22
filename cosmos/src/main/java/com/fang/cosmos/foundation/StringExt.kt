package com.fang.cosmos.foundation

val String?.takeIfNotBlank get() = this?.takeIf { it.isNotBlank() }
