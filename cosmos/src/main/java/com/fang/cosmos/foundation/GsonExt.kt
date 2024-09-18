package com.fang.cosmos.foundation

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

fun Gson.json(any: Any?): Result<String?> = runCatching { toJson(any).replace("\\s+".toRegex(), "") }

inline fun <reified T> Gson.fromJson(json: String): Result<T?> = runCatching { fromJson(json, T::class.java) }

inline fun <reified T> Gson.fromJsonTypeToken(json: String): Result<T?> = runCatching { fromJson(json, object : TypeToken<T>() {}.type) }

fun <T> Gson.fromJsonTypeList(
    json: String,
    clazz: Class<T>,
): Result<List<T>?> =
    kotlin.runCatching {
        fromJson(json, TypeToken.getParameterized(List::class.java, clazz).type)
    }

inline fun <reified T> Gson.fromJsonTypeList(json: String): Result<List<T>?> = fromJsonTypeList(json, T::class.java)
