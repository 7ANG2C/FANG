package com.fang.free.tick

sealed class TickEvent {
    object Open : TickEvent()

    data class Message(
        val tick: MediatorTick,
    ) : TickEvent()

    data class WsFailure(
        val description: String,
        val t: Throwable,
    ) : TickEvent() // from GetWsEventFlow

    data class CatchFailure(
        val t: Throwable,
    ) : TickEvent()

    data class Close(
        val description: String,
    ) : TickEvent() // from GetWsEventFlow
}
