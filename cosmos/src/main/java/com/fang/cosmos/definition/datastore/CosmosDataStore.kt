package com.fang.cosmos.definition.datastore

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.byteArrayPreferencesKey
import androidx.datastore.preferences.core.doublePreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.fang.cosmos.Cosmos
import com.fang.cosmos.definition.CosmosDef
import com.fang.cosmos.foundation.fromJsonTypeToken
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.withContext

val Context.cosmosDataStore by preferencesDataStore(
    name = "${Cosmos.QUALIFIER}default_data_store",
)

@OptIn(ExperimentalCoroutinesApi::class)
class CosmosDataStore(
    val gson: Gson = CosmosDef.Gson,
    val context: Context = CosmosDef.Context,
) {
    inline fun <reified T> dataStoreFlow(
        feature: String,
        vararg addition: String,
    ) = context.cosmosDataStore.data
        .mapLatest { pref ->
            val key = key(feature, *addition)
            when (T::class) {
                Int::class -> pref[intPreferencesKey(key)] as? T
                Double::class -> pref[doublePreferencesKey(key)] as? T
                String::class -> pref[stringPreferencesKey(key)] as? T
                Boolean::class -> pref[booleanPreferencesKey(key)] as? T
                Float::class -> pref[floatPreferencesKey(key)] as? T
                Long::class -> pref[longPreferencesKey(key)] as? T
                ByteArray::class -> pref[byteArrayPreferencesKey(key)] as? T
                else ->
                    pref[stringPreferencesKey(key)]?.let {
                        gson.fromJsonTypeToken<T>(it).getOrThrow()
                    }
            }
        }.flowOn(Dispatchers.IO)

    suspend inline fun <reified T> update(
        feature: String,
        vararg addition: String,
        crossinline transform: (T?) -> T?,
    ) {
        withContext(Dispatchers.IO) {
            context.cosmosDataStore.edit { pref ->
                val key = key(feature, *addition)
                when (T::class) {
                    Int::class -> {
                        val prefKey = intPreferencesKey(key)
                        (transform(pref[prefKey] as? T) as? Int)?.let {
                            pref[prefKey] = it
                        } ?: pref.remove(prefKey)
                    }
                    Double::class -> {
                        val prefKey = doublePreferencesKey(key)
                        (transform(pref[prefKey] as? T) as? Double)?.let {
                            pref[prefKey] = it
                        } ?: pref.remove(prefKey)
                    }
                    String::class -> {
                        val prefKey = stringPreferencesKey(key)
                        (transform(pref[prefKey] as? T) as? String)?.let {
                            pref[prefKey] = it
                        } ?: pref.remove(prefKey)
                    }
                    Boolean::class -> {
                        val prefKey = booleanPreferencesKey(key)
                        (transform(pref[prefKey] as? T) as? Boolean)?.let {
                            pref[prefKey] = it
                        } ?: pref.remove(prefKey)
                    }
                    Float::class -> {
                        val prefKey = floatPreferencesKey(key)
                        (transform(pref[prefKey] as? T) as? Float)?.let {
                            pref[prefKey] = it
                        } ?: pref.remove(prefKey)
                    }
                    Long::class -> {
                        val prefKey = longPreferencesKey(key)
                        (transform(pref[prefKey] as? T) as? Long)?.let {
                            pref[prefKey] = it
                        } ?: pref.remove(prefKey)
                    }
                    ByteArray::class -> {
                        val prefKey = byteArrayPreferencesKey(key)
                        (transform(pref[prefKey] as? T) as? ByteArray)?.let {
                            pref[prefKey] = it
                        } ?: pref.remove(prefKey)
                    }
                    else -> {
                        val prefKey = stringPreferencesKey(key)
                        val old =
                            pref[prefKey]?.let {
                                gson.fromJsonTypeToken<T>(it).getOrThrow()
                            }
                        transform(old)?.let {
                            pref[prefKey] = gson.toJson(it)
                        } ?: pref.remove(prefKey)
                    }
                }
            }
        }
    }

    fun key(
        main: String,
        vararg otherKeys: String,
    ) = (listOf(main) + otherKeys.toList()).joinToString("_")
}
