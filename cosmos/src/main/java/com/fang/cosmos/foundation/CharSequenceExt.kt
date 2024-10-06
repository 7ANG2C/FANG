package com.fang.cosmos.foundation

inline fun CharSequence.indexOfFirstOrNull(predicate: (Char) -> Boolean): Int? = indexOfFirst(predicate).takeIf { it >= 0 }

inline fun CharSequence.indexOfLastOrNull(predicate: (Char) -> Boolean): Int? = indexOfLast(predicate).takeIf { it >= 0 }
