package com.fang.free.ta

data class Macd(
    val macd: Double,
    val signal: Double,
) {
    val diff = macd - signal
}

fun <T> List<T>.macd(
    fastLength: Int = 12,
    slowLength: Int = 26,
    signalSmooth: Int = 9,
    source: (T) -> Double,
): List<Macd> {
    check(fastLength > 1)
    check(slowLength > 1)
    check(fastLength < slowLength)
    val alpha = { len: Int -> 2.0 / (len + 1) }
    val fastAlpha = alpha(fastLength)
    val slowAlpha = alpha(slowLength)
    val signalLength = slowLength + signalSmooth - 1
    val signalAlpha = alpha(signalSmooth)
    var fastSumForSma = 0.0
    var fast = Double.NaN
    var slowSumForSma = 0.0
    var slow = Double.NaN
    var macd = Double.NaN
    var signalSumForSma = 0.0
    var signal = Double.NaN
    return mapIndexed { i, t ->
        val src = source(t)
        // fast
        if (i < fastLength) fastSumForSma += src
        if (i == fastLength - 1) {
            fast = fastSumForSma / fastLength
        } else if (i >= fastLength) {
            val new = (src * fastAlpha) + fast * (1 - fastAlpha)
            fast = new
        }
        // slow
        if (i < slowLength) slowSumForSma += src
        if (i == slowLength - 1) {
            slow = slowSumForSma / slowLength
            macd = fast - slow
        } else if (i >= slowLength) {
            val new =
                (src * slowAlpha) + slow * (1 - slowAlpha)
            slow = new
            macd = fast - slow
        }
        // signal
        if (i < signalLength) {
            signalSumForSma += if (macd.isNaN()) 0.0 else macd
        }
        if (i == signalLength - 1) {
            signal = signalSumForSma / signalSmooth
        } else if (i >= signalLength) {
            val new = (macd * signalAlpha) + signal * (1 - signalAlpha)
            signal = new
        }
        Macd(macd, signal)
    }
}
