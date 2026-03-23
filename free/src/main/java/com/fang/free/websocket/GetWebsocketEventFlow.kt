package com.fang.free.websocket

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okhttp3.WebSocket

class GetWebsocketEventFlow {
    fun invoke(
        url: String,
        tag: String,
    ) = callbackFlow {
        val client = OkHttpClient()
        val ws =
            client.newWebSocket(
                Request.Builder().url(url).build(),
                object : CustomWebSocketListener(tag) {
                    override fun onOpen(
                        webSocket: WebSocket,
                        response: Response,
                    ) {
                        super.onOpen(webSocket, response)
                        launch {
                            trySend(WsEvent.Open(webSocket, response))
                        }
                    }

                    override fun onMessage(
                        webSocket: WebSocket,
                        text: String,
                    ) {
                        super.onMessage(webSocket, text)
                        // exception 沒 launch throw：走 callback 的 override fun onFailure(...)，不會 awaitClose
                        // exception 有 launch throw：走下游 .catch { }，會 awaitClose
                        launch {
                            trySend(WsEvent.Message(text))
                        }
                    }

                    override fun onFailure(
                        webSocket: WebSocket,
                        t: Throwable,
                        response: Response?,
                    ) {
                        super.onFailure(webSocket, t, response)
                        // exception 沒 launch throw：就算下游有 .catch { }，還是會閃退
                        // exception 有 launch throw：走下游 .catch { }，會 awaitClose
                        launch {
                            trySend(WsEvent.Failure(t, response))
                        }
                    }

                    override fun onClosing(
                        webSocket: WebSocket,
                        code: Int,
                        reason: String,
                    ) {
                        super.onClosing(webSocket, code, reason)
                        launch {
                            trySend(WsEvent.Closing(code, reason))
                        }
                    }

                    override fun onClosed(
                        webSocket: WebSocket,
                        code: Int,
                        reason: String,
                    ) {
                        super.onClosed(webSocket, code, reason)
                        launch {
                            trySend(WsEvent.Closed(code, reason))
                        }
                    }
                },
            )
        awaitClose {
            ws.close(1000, "($tag) WS awaitClose: $url")
            client.dispatcher.executorService.shutdown()
            launch {
                trySend(WsEvent.AwaitClose(ws))
            }
        }
    }.flowOn(Dispatchers.IO)
        .catch {
            emit(WsEvent.CatchFailure(it))
        }
}
