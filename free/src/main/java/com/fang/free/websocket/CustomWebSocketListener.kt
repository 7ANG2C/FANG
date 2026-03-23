package com.fang.free.websocket

import com.fang.cosmos.foundation.logD
import okhttp3.Response
import okhttp3.WebSocket
import okhttp3.WebSocketListener
import okio.ByteString

abstract class CustomWebSocketListener(
    private val tag: String,
) : WebSocketListener() {
    override fun onOpen(
        webSocket: WebSocket,
        response: Response,
    ) {
        super.onOpen(webSocket, response)
        log("onOpen | WebSocket: $webSocket, Response: $response")
    }

    override fun onMessage(
        webSocket: WebSocket,
        bytes: ByteString,
    ) {
        super.onMessage(webSocket, bytes)
        log("onMessage | WebSocket: $webSocket, ByteString: $bytes")
    }

    override fun onMessage(
        webSocket: WebSocket,
        text: String,
    ) {
        super.onMessage(webSocket, text)
        log("onMessage | WebSocket: $webSocket, Text: $text")
    }

    override fun onFailure(
        webSocket: WebSocket,
        t: Throwable,
        response: Response?,
    ) {
        super.onFailure(webSocket, t, response)
        log("onFailure | Throwable: $t, WebSocket: $webSocket, Response?: $response")
    }

    override fun onClosing(
        webSocket: WebSocket,
        code: Int,
        reason: String,
    ) {
        super.onClosing(webSocket, code, reason)
        log("onClosing | Code: $code, WebSocket: $webSocket, Reason: $reason")
    }

    override fun onClosed(
        webSocket: WebSocket,
        code: Int,
        reason: String,
    ) {
        super.onClosed(webSocket, code, reason)
        log("onClosed | Code: $code, WebSocket: $webSocket, Reason: $reason")
    }

    private fun log(msg: String) {
        tag.takeIf { it.isNotBlank() }?.let { logD(it, msg) }
    }
}
