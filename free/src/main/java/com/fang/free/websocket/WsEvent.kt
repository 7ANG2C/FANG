package com.fang.free.websocket

import okhttp3.Response
import okhttp3.WebSocket

sealed class WsEvent {
    data class Open(
        val ws: WebSocket,
        val response: Response,
    ) : WsEvent()

    data class Message(
        val text: String,
    ) : WsEvent()

    data class Failure(
        val t: Throwable,
        val response: Response?,
    ) : WsEvent()

    data class CatchFailure(
        val t: Throwable,
    ) : WsEvent()

    data class Closing(
        val code: Int,
        val reason: String,
    ) : WsEvent()

    data class Closed(
        val code: Int,
        val reason: String,
    ) : WsEvent()

    data class AwaitClose(
        val ws: WebSocket,
    ) : WsEvent()
}
