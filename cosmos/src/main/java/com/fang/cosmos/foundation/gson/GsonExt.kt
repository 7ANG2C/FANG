package com.fang.cosmos.foundation.gson

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

inline fun <reified T> Gson.fromJson(json: String): Result<T?> = runCatching { fromJson(json, T::class.java) }

inline fun <reified T> Gson.fromJsonTypeToken(json: String): Result<T?> = runCatching { fromJson(json, object : TypeToken<T>() {}.type) }
