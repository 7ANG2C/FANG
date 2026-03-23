package com.fang.free.tick

import com.google.gson.annotations.SerializedName

/**
 * @see <a href="https://binance-docs.github.io/apidocs/futures/cn/#k-6">Websocket_K線</a>
 */
data class WsTick(
    @SerializedName("data")
    val data: TickData,
) {
    data class TickData(
        @SerializedName("k")
        val tick: Tick,
        @SerializedName("E")
        val tickMillis: Long,
    ) {
        data class Tick(
            @SerializedName("c")
            val close: Double,
            @SerializedName("t")
            val openMillis: Long,
            @SerializedName("T")
            val closeMillis: Long,
            @SerializedName("x")
            val isClose: Boolean,
//            @SerializedName("o")
//            val open: Double,
//            @SerializedName("h")
//            val high: Double,
//            @SerializedName("l")
//            val low: Double,
        )
    }
}
