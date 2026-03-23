package com.fang.free.tick

import com.fang.cosmos.foundation.throttleLatest
import com.fang.free.ta.macd
import com.fang.free.ta.rsi
import com.fang.free.ta.td
import com.fang.free.websocket.GetWebsocketEventFlow
import com.fang.free.websocket.WsEvent
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.coroutines.flow.merge
import kotlinx.coroutines.flow.scan
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONArray
import java.util.Calendar
import java.util.Locale
import java.util.TimeZone
import kotlin.time.Duration.Companion.hours
import kotlin.time.Duration.Companion.seconds

object Url {
    const val BINANCE = "https://fapi.binance.com"
    const val WS = "wss://fstream.binance.com"
}

enum class Switch {
    FIRST,
    SECOND,
    ;

    companion object {
        val all = entries
    }
}

/**
 * @see <a href="https://binance-docs.github.io/apidocs/futures/cn/#k-6">Websocket_K線</a>
 * @see <a href="https://github.com/binance/binance-futures-connector-java">Github</a>
 */
@Suppress("OPT_IN_IS_NOT_ENABLED")
@OptIn(ExperimentalCoroutinesApi::class)
class GetTickFlowUseCase(
    coroutineScope: CoroutineScope,
    private val gson: Gson = Gson(),
    private val getWsEventFlow: GetWebsocketEventFlow = GetWebsocketEventFlow(),
) {
    companion object {
        private const val COUNT = 1440
    }

    private val reconnectState = MutableStateFlow(taipeiTime)
    private val refreshState = MutableStateFlow(taipeiTime)

    private val switch1State = MutableStateFlow(true)
    private val switch2State = MutableStateFlow(false)

    private val symbolState = MutableStateFlow("ADAUSDT")
    private val timeFrameState = MutableStateFlow("1m")

    private val _logcatState = MutableStateFlow(emptyList<String>())
    val logcatState = _logcatState.asStateFlow()

    init {
        coroutineScope.launch {
            reconnectState
                .flatMapLatest {
                    flow {
                        while (true) {
                            delay(23.hours)
                            emit(taipeiTime)
                        }
                    }
                }.flowOn(Dispatchers.Default)
                .collectLatest {
                    switch1State.value = true
                    switch2State.value = true
                }
        }
    }

    operator fun invoke() =
        refreshState
            .throttleLatest(2.seconds)
            .flowOn(Dispatchers.Default)
            .flatMapLatest {
                combine(symbolState, timeFrameState, ::Pair)
                    .mapNotNull { (symbol, timeFrame) ->
                        if (symbol.isNotBlank() && TimeFrame.isValid(timeFrame)) {
                            // 避免時間太短，還沒訂閱成功就進入下一分鐘，拿不到當前那一分鐘的收盤
                            if (it.get(Calendar.SECOND) > 55) {
                                delay(15.seconds)
                            }
                            val apiList =
                                getApiList(symbol, timeFrame)
                                    .getOrNull()
                                    .orEmpty()
                                    .dropLast(1)
                            Triple(symbol, timeFrame, apiList)
                        } else {
                            null
                        }
                    }.flowOn(Dispatchers.IO)
            }.flatMapLatest { (symbol, timeFrame, apiList) ->
                Switch.all
                    .map { invoke(it, symbol, timeFrame) }
                    .merge()
                    .scan(emptyList()) { acc: List<MediatorTick>, new ->
                        val wsList =
                            when {
                                acc.isEmpty() -> listOf(new)
                                (new.closeMillis - acc.last().closeMillis) == 60000L -> acc + new
                                (new.closeMillis - acc.last().closeMillis) > 60000L -> {
                                    setLogcatState("Missing Tick!")
                                    acc
                                }
                                else -> {
                                    //
                                    acc
                                }
                            }
                        val totalList =
                            if (wsList.size < COUNT) {
                                apiList + wsList
                            } else {
                                wsList
                            }
                        totalList.drop((totalList.size - COUNT).coerceAtLeast(0))
                    }
            }.distinctUntilChanged()
            .mapNotNull { ticks ->
                if (ticks.size >= COUNT) {
                    val closes = ticks.map { it.close }
                    val tds = closes.td { it }
                    val rsiList = closes.rsi { it }
                    val macdList = closes.macd { it }
                    ticks.mapIndexed { i, tick ->
                        Tick(
                            close = tick.close,
                            closeMillis = tick.closeMillis,
                            td = tds[i],
                            macd = macdList[i],
                            rsi = rsiList[i],
                        )
                    }
                    // 計算指標
                } else {
                    null
                }
            }.flowOn(Dispatchers.Default)

    private operator fun invoke(
        switch: Switch,
        symbol: String,
        timeFrame: String,
    ): Flow<MediatorTick> {
        val switch1State: MutableStateFlow<Boolean>
        val switch2State: MutableStateFlow<Boolean>
        when (switch) {
            Switch.FIRST -> {
                switch1State = this.switch1State
                switch2State = this.switch2State
            }
            Switch.SECOND -> {
                switch1State = this.switch2State
                switch2State = this.switch1State
            }
        }
        return switch1State.flatMapLatest { isEnable ->
            if (isEnable) {
                getWsEventFlow
                    .invoke("${Url.WS}/stream?streams=${symbol.lowercase()}@kline_$timeFrame", "TICK_")
                    .flowOn(Dispatchers.IO)
                    .catch {
                        refresh()
                        setLogcatState("$switch catch {${it::class.java.simpleName} - ${it.message} }")
                    }.mapLatest { ev ->
                        when (ev) {
                            is WsEvent.Open -> {
                                switch2State.value = false
                                reconnectState.value = taipeiTime
                                setLogcatState("$switch Open(Tick)")
                                null
                            }
                            is WsEvent.Message -> {
                                kotlin
                                    .runCatching {
                                        val data = gson.fromJson(ev.text, WsTick::class.java).data
                                        val tick = data.tick
                                        if (tick.isClose) {
                                            MediatorTick(
                                                close = tick.close,
                                                tickMillis = data.tickMillis,
                                                closeMillis = tick.closeMillis,
                                            )
                                        } else {
                                            null
                                        }
                                    }.getOrNull()
                            }
                            is WsEvent.Failure -> {
                                refresh()
                                setLogcatState("$switch WsEvent.Failure(${ev.t::class.java.simpleName} - ${ev.t.message})")
                                null
                            }
                            is WsEvent.CatchFailure -> {
                                refresh()
                                setLogcatState("$switch WsEvent.CatchFailure(${ev.t::class.java.simpleName} - ${ev.t.message})")
                                null
                            }
                            is WsEvent.Closing -> {
                                setLogcatState("$switch Closing")
                                null
                            }
                            is WsEvent.Closed -> {
                                null
                            }
                            is WsEvent.AwaitClose -> {
                                null
                            }
                        }
                    }.flowOn(Dispatchers.Default)
            } else {
                flow { emit(null) }
            }.filterNotNull()
        }
    }

    private suspend fun getApiList(
        symbol: String,
        tf: String,
        count: Int = COUNT,
    ) = withContext(Dispatchers.IO) {
//        [
//            [
//                1499040000000,      // 0: 开盘时间
//                "0.01634790",       // 1: 开盘价
//                "0.80000000",       // 2: 最高价
//                "0.01575800",       // 3: 最低价
//                "0.01577100",       // 4: 收盘价(当前K线未结束的即为最新价)
//                "148976.11427815",  // 5: 成交量
//                1499644799999,      // 6: 收盘时间
//                "2434.19055334",    // 7: 成交额
//                308,                // 8: 成交笔数
//                "1756.87402397",    // 9: 主动买入成交量
//                "28.46694368",      // 10: 主动买入成交额
//                "17928899.62484339" // 11: 请忽略该参数
//            ]
//        ]
        kotlin.runCatching {
            val request =
                Request
                    .Builder()
                    .url("${Url.BINANCE}/fapi/v1/klines?symbol=$symbol&interval=$tf&limit=$count")
                    .addHeader("Content-Type", "application/json")
                    .build()
            OkHttpClient()
                .newBuilder()
                .build()
                .newCall(request)
                .execute()
                .body
                ?.string()
                ?.let { string ->
                    val dataList = JSONArray(string)
                    (0 until dataList.length()).map { index ->
                        val jsonArray = dataList.getJSONArray(index)
                        MediatorTick(
                            close = jsonArray.getDouble(4),
                            tickMillis = 0L,
                            closeMillis = jsonArray.getLong(6),
                        )
                    }
                }
        }
    }

    private fun setLogcatState(string: String) {
        _logcatState.update {
            (it + string).takeLast(100)
        }
    }

    fun refresh() {
        refreshState.value = taipeiTime
    }

    fun setSymbol(symbol: String) {
        symbolState.value = symbol
    }

    fun setTimeFrame(tf: String) {
        timeFrameState.value = tf
    }
}

val taipeiTime: Calendar get() = Calendar.getInstance(TimeZone.getTimeZone("GMT+8"), Locale.TAIWAN)
val taipeiMillis: Long get() = taipeiTime.timeInMillis
