package com.fang.free.ta

import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min

fun <T> Iterable<T>.rsi(
    length: Int = 14,
    source: (T) -> Double,
): List<Double?> {
    check(length > 0)
    var preClose = 0.0
    var minSumForSma = 0.0
    var maxSumForSma = 0.0
    var min = Double.NaN
    var max = Double.NaN

    return mapIndexed { i, t ->
        val src = source(t)
        val priceChanged =
            if (i == 0) {
                preClose = src
                0.0
            } else {
                val newPriceChanged = src - preClose
                preClose = src
                newPriceChanged
            }
        if (i <= length) {
            if (priceChanged < 0.0) minSumForSma += -priceChanged
            if (priceChanged > 0.0) maxSumForSma += priceChanged
        }
        if (i == length) {
            min = minSumForSma / length
            max = maxSumForSma / length
        } else if (i > length) {
            val newMin = abs(min(0.0, priceChanged)) / length + (length - 1) * min / length
            val newMax = max(0.0, priceChanged) / length + (length - 1) * max / length
            min = newMin
            max = newMax
        }
        output(min, max)
    }
}

private fun output(
    _min: Double,
    _max: Double,
): Double? =
    if (_min.isNaN() || _max.isNaN()) {
        null
    } else if (_min == 0.0) {
        100.0
    } else if (_max == 0.0) {
        0.0
    } else {
        100 - (100 / (1 + _max / _min))
    }
