package com.fang.free

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody

suspend fun reportToLINE(text: String) {
    withContext(Dispatchers.IO) {
        kotlin.runCatching {
            OkHttpClient()
                .newBuilder()
                .build()
                .newCall(
                    Request
                        .Builder()
                        .url("https://api.line.me/v2/bot/message/broadcast")
                        .method(
                            "POST",
                            "{\"messages\":[{\"type\":\"text\",\"text\":\"${text}\"}]}"
                                .toRequestBody("application/json".toMediaTypeOrNull()),
                        ).addHeader("Content-Type", "application/json")
                        .addHeader(
                            "Authorization",
                            "Bearer ${Config.LINE_TOKEN}",
                        ).build(),
                ).execute()
        }
    }
}
