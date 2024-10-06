package com.fang.cosmos.foundation

val <T> Collection<T?>.isMulti get() = size > 1

inline fun <T, R : Any> Iterable<T>.mapNoNull(
    predicate: (T) -> Boolean,
    transform: (T) -> R,
) = mapNotNull {
    if (predicate(it)) {
        transform(it)
    } else {
        null
    }
}

inline fun <T> Iterable<T>.replace(
    predicate: (T) -> Boolean,
    transform: (T) -> T,
) = map { if (predicate(it)) transform(it) else it }

inline fun <T> Iterable<T>.indexOfFirstOrNull(predicate: (T) -> Boolean) = indexOfFirst(predicate).takeIf { it >= 0 }

inline fun <T> Iterable<T>.indexOfLastOrNull(predicate: (T) -> Boolean) = indexOfLast(predicate).takeIf { it >= 0 }
