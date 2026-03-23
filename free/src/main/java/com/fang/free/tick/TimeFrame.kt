package com.fang.free.tick

object TimeFrame {
    // m -> minutes; h -> hours; d -> days; w -> weeks; M -> months
    fun isValid(tf: String): Boolean =
        tf in
            listOf(
                "1m",
                "3m",
                "5m",
                "15m",
                "30m",
                "1h",
                "2h",
                "4h",
                "6h",
                "8h",
                "12h",
                "1d",
                "3d",
                "1w",
                "1M",
            )
}
