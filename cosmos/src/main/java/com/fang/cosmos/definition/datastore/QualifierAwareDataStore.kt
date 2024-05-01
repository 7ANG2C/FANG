package com.fang.cosmos.definition.datastore

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.doublePreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.fang.cosmos.Cosmos
import com.fang.cosmos.definition.CosmosDef
import com.fang.cosmos.foundation.gson.fromJsonTypeToken
import com.fang.cosmos.foundation.withcontextcatching.withDefaultCoroutine
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.launch

val Context.psgpDataStore by preferencesDataStore(
    name = "${Cosmos.QUALIFIER}default_scoped_d_s"
)

@OptIn(ExperimentalCoroutinesApi::class)
abstract class QualifierAwareDataStore(
    val gson: Gson = CosmosDef.Gson,
    val context: Context = CosmosDef.Context,
    coroutineScope: CoroutineScope = CosmosDef.CoroutineScope,
) {

    abstract val qualifierKeyFlow: Flow<String?>
    private val _qualifierState = MutableStateFlow<String?>(null)
    val qualifierState by lazy { _qualifierState.asStateFlow() }

    init {
        coroutineScope.launch {
            qualifierKeyFlow.collectLatest {
                _qualifierState.value = it
            }
        }
    }

    inline fun <reified T> getDataFlow(key: String) =
        context.psgpDataStore.data.flatMapLatest { pref ->
            qualifierState.filterNotNull().mapLatest { qualifier ->
                val realKey = getKey(key = key, qualifier)
                when (T::class) {
                    Int::class -> pref[intPreferencesKey(realKey)] as? T
                    Double::class -> pref[doublePreferencesKey(realKey)] as? T
                    String::class -> pref[stringPreferencesKey(realKey)] as? T
                    Boolean::class -> pref[booleanPreferencesKey(realKey)] as? T
                    Float::class -> pref[floatPreferencesKey(realKey)] as? T
                    Long::class -> pref[longPreferencesKey(realKey)] as? T
                    else -> pref[stringPreferencesKey(realKey)]?.let {
                        gson.fromJsonTypeToken<T>(it).getOrThrow()
                    }
                }
            }
        }.flowOn(Dispatchers.Default)

    inline fun <reified T> getDataFlowOrDefault(key: String, default: T) =
        getDataFlow<T>(key).mapLatest { it ?: default }

    suspend inline fun <reified T> update(key: String, crossinline transform: (T?) -> T?) {
        qualifierState.value?.let { q ->
            val realKey = getKey(key = key, q)
            withDefaultCoroutine {
                context.psgpDataStore.edit { pref ->
                    when (T::class) {
                        Int::class -> {
                            val prefKey = intPreferencesKey(realKey)
                            (transform(pref[prefKey] as? T) as? Int)?.let {
                                pref[prefKey] = it
                            } ?: pref.remove(prefKey)
                        }
                        Double::class -> {
                            val prefKey = doublePreferencesKey(realKey)
                            (transform(pref[prefKey] as? T) as? Double)?.let {
                                pref[prefKey] = it
                            } ?: pref.remove(prefKey)
                        }
                        String::class -> {
                            val prefKey = stringPreferencesKey(realKey)
                            (transform(pref[prefKey] as? T) as? String)?.let {
                                pref[prefKey] = it
                            } ?: pref.remove(prefKey)
                        }
                        Boolean::class -> {
                            val prefKey = booleanPreferencesKey(realKey)
                            (transform(pref[prefKey] as? T) as? Boolean)?.let {
                                pref[prefKey] = it
                            } ?: pref.remove(prefKey)
                        }
                        Float::class -> {
                            val prefKey = floatPreferencesKey(realKey)
                            (transform(pref[prefKey] as? T) as? Float)?.let {
                                pref[prefKey] = it
                            } ?: pref.remove(prefKey)
                        }
                        Long::class -> {
                            val prefKey = longPreferencesKey(realKey)
                            (transform(pref[prefKey] as? T) as? Long)?.let {
                                pref[prefKey] = it
                            } ?: pref.remove(prefKey)
                        }
                        else -> {
                            val prefKey = stringPreferencesKey(realKey)
                            val old =
                                pref[prefKey]?.let { gson.fromJsonTypeToken<T>(it).getOrThrow() }
                            transform(old)?.let {
                                pref[prefKey] = gson.toJson(it)
                            } ?: pref.remove(prefKey)
                        }
                    }
                }
            }
        }
    }

    fun getKey(key: String, prefix: String) = "$prefix$key"
}
