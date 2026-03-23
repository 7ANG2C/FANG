package com.fang.free.ta

inline fun <T> Iterable<T>.kd(
    fastLength: Int = 12,
    slowLength: Int = 26,
    signalSmooth: Int = 9,
    source: (T) -> Double,
): List<Macd> {
    check(fastLength > 0) { "length <= 0" }
    check(slowLength > 0) { "length <= 0" }
    var fastTotal = 0.0
    var fast = Double.NaN
    var slowTotal = 0.0
    var slow = Double.NaN
    var macd = Double.NaN
    var signal = Double.NaN
    return mapIndexed { i, t ->
        if (i < fastLength) {
            fastTotal += source(t)
        } else if (i == fastLength) {
            val new = fastTotal / fastLength
            fast = new
        } else {
            val new =
                (source(t) * 2 / (fastLength + 1)) + fast * (fastLength - 1) / (fastLength + 1)
            fast = new
        }
        if (i < slowLength) {
            slowTotal += source(t)
        } else if (i == slowLength) {
            val new = slowTotal / slowLength
            slow = new
            macd = fast - slow
        } else {
            val new =
                (source(t) * 2 / (slowLength + 1)) + slow * (slowLength - 1) / (slowLength + 1)
            slow = new
            macd = fast - slow
        }

        Macd(macd, signal)
    }
}
