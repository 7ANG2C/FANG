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
