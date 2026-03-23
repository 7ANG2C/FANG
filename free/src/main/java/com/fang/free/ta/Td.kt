package com.fang.free.ta

fun <T> Iterable<T>.td(source: (T) -> Double): List<Int?> {
    val valid = filter { !source(it).isNaN() }
    if (valid.size < 100) {
        return emptyList()
    } else {
        val tdMList = mutableListOf<Int?>()
        valid.forEachIndexed { i, close ->
            if (i < 4) {
                tdMList.add(null)
            } else {
                val pre = valid[i - 4]
                // buy
                if (source(close) < source(pre)) {
                    // 判斷 td
                    val preTd = (tdMList[i - 1] ?: 0)
                    if (preTd == 13) {
                        tdMList.add(1)
                    } else {
                        tdMList.add(preTd + 1)
                    }
                } else {
                    tdMList.add(null)
                }
            }
        }
        return tdMList
    }
}
