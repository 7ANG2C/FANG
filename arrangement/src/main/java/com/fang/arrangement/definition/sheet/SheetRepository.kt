package com.fang.arrangement.definition.sheet

import android.content.Context
import com.fang.arrangement.definition.foundation.KeyValue
import com.fang.arrangement.ui.shared.retry
import com.fang.cosmos.foundation.fromJsonTypeList
import com.fang.cosmos.foundation.retryExponentialWhen
import com.fang.cosmos.foundation.takeIfNotBlank
import com.fang.cosmos.foundation.throttleLatest
import com.fang.cosmos.foundation.withcontextcatching.ioCatching
import com.fang.cosmos.foundation.withcontextcatching.withDefaultCoroutine
import com.google.api.client.http.javanet.NetHttpTransport
import com.google.api.client.json.gson.GsonFactory
import com.google.api.services.sheets.v4.Sheets
import com.google.api.services.sheets.v4.SheetsScopes
import com.google.api.services.sheets.v4.model.BatchUpdateSpreadsheetRequest
import com.google.api.services.sheets.v4.model.BatchUpdateSpreadsheetResponse
import com.google.api.services.sheets.v4.model.DeleteDimensionRequest
import com.google.api.services.sheets.v4.model.DimensionRange
import com.google.api.services.sheets.v4.model.GridCoordinate
import com.google.api.services.sheets.v4.model.InsertDimensionRequest
import com.google.api.services.sheets.v4.model.PasteDataRequest
import com.google.api.services.sheets.v4.model.Request
import com.google.auth.http.HttpCredentialsAdapter
import com.google.auth.oauth2.ServiceAccountCredentials
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.merge
import kotlinx.coroutines.flow.scan
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import kotlin.time.Duration.Companion.seconds

@OptIn(ExperimentalCoroutinesApi::class)
internal class SheetRepository(
    context: Context,
    coroutineScope: CoroutineScope,
    private val gson: Gson,
) {
    private data class Mediator(
        val name: String,
        val keys: List<String>,
        val values: List<Any>,
        val clazz: Class<out Any>,
    )

    companion object {
        const val SPREAD_SHEET_ID = "1hYhuc7IYnVkjx6qK7WePQiTF7Jw9ZUwC-pU8DMVcNdI"
    }

    private val service by lazy {
        val credentials =
            runBlocking {
                withContext(Dispatchers.IO) {
                    ServiceAccountCredentials.fromStream(
                        context.assets.open("service-account.json"),
                    ).createScoped(SheetsScopes.SPREADSHEETS)
                }
            }
        Sheets.Builder(
            NetHttpTransport(),
            GsonFactory.getDefaultInstance(),
            HttpCredentialsAdapter(credentials),
        )
            .setApplicationName("Arrangement")
            .build()
            .spreadsheets()
    }

    private sealed interface Type {
        data class Property(val properties: List<SpreadSheet.Property>) : Type

        data class Sheet(val sheets: List<Mediator>) : Type
    }

    private val refreshProperty = MutableStateFlow<Long?>(null)
    private val refreshSheet = MutableStateFlow<Type.Sheet?>(null)

    private val _workSheet = MutableStateFlow<List<WorkSheet>?>(null)
    val workSheet = _workSheet.asStateFlow()

    init {
        refreshProperty()
        coroutineScope.launch {
            refreshSheet(SpreadSheet.all)
            val propertyFlow =
                refreshProperty.filterNotNull()
                    .throttleLatest(1.seconds)
                    .flowOn(Dispatchers.Default)
                    .flatMapLatest {
                        flow {
                            val properties =
                                service.get(SPREAD_SHEET_ID).execute()
                                    .sheets.map {
                                        SpreadSheet.Property(
                                            id = it.properties.sheetId,
                                            name = it.properties.title,
                                        )
                                    }
                            emit(Type.Property(properties))
                        }
                            .flowOn(Dispatchers.IO)
                            .retryExponentialWhen { _, attempt ->
                                attempt < 3
                            }
                            .flowOn(Dispatchers.Default)
                    }
            val invalidSheetId = -1
            merge(propertyFlow, refreshSheet.filterNotNull())
                .scan(null) { acc: List<WorkSheet>?, new ->
                    when (new) {
                        is Type.Property ->
                            acc?.map { sheet ->
                                new.properties.find { it.name == sheet.name }?.let {
                                    sheet.copy(id = it.id)
                                } ?: sheet
                            } ?: new.properties.map {
                                WorkSheet(
                                    id = it.id,
                                    name = it.name,
                                    keys = emptyList(),
                                    values = emptyList(),
                                    clazz = Any::class.java,
                                )
                            }
                        is Type.Sheet ->
                            acc?.map { sheet ->
                                new.sheets.find { it.name == sheet.name }?.let {
                                    sheet.copy(
                                        keys = it.keys,
                                        values = it.values,
                                        clazz = it.clazz,
                                    )
                                } ?: sheet
                            } ?: new.sheets.map {
                                WorkSheet(
                                    id = invalidSheetId,
                                    name = it.name,
                                    keys = it.keys,
                                    values = it.values,
                                    clazz = it.clazz,
                                )
                            }
                    }
                }
                .filterNotNull()
                .flowOn(Dispatchers.Default)
                .collectLatest { sheets ->
                    sheets.takeIf {
                        val sheet = it.firstOrNull()
                        sheet?.id != invalidSheetId && !sheet?.keys.isNullOrEmpty()
                    }?.let { _workSheet.value = it }
                }
        }
    }

    suspend inline fun <reified T> insert(keyValues: List<KeyValue>) =
        editSheet<T>(
            null,
            { sheet, _ ->
                setInsertDimension(InsertDimensionRequest().setRange(range(sheet, 1)))
            },
            { sheet, _ ->
                paste(keyValues, sheet, 1)
            },
        )

    suspend inline fun <reified T> update(
        key: String,
        value: String,
        keyValues: List<KeyValue>,
    ) = editSheet<T>(
        KeyValue(key, value),
        { sheet, index ->
            paste(keyValues, sheet, index)
        },
    )

    suspend inline fun <reified T> delete(
        key: String,
        value: String,
    ) = editSheet<T>(
        KeyValue(key, value),
        { sheet, index ->
            setDeleteDimension(DeleteDimensionRequest().setRange(range(sheet, index)))
        },
    )

    private fun refreshProperty() {
        refreshProperty.value = System.currentTimeMillis()
    }

    private suspend fun refreshSheet(requests: List<SpreadSheet.Request>) {
        retry {
            ioCatching {
                service.values()
                    .batchGet(SPREAD_SHEET_ID)
                    .setRanges(requests.map { it.name }).execute()
            }
        }.mapCatching { res ->
            withDefaultCoroutine {
                res.valueRanges.mapNotNull { valueRange ->
                    async(Dispatchers.Default) {
                        val valueRanges = valueRange.getValues()
                        val request = requests.find { it.name in valueRange.range }
                        val keys =
                            valueRanges.firstOrNull()?.mapNotNull { it.toString().takeIfNotBlank }
                        val valueList = valueRanges.drop(1)
                        if (request != null && !keys.isNullOrEmpty()) {
                            val jsonString =
                                "[" +
                                    valueList.fold("") { acc, values ->
                                        val pairs =
                                            values.foldIndexed("") { i, old, value ->
                                                val key = keys.getOrNull(i).takeIfNotBlank
                                                val validValue =
                                                    value?.toString().takeIfNotBlank
                                                val pair =
                                                    if (key != null && validValue != null) {
                                                        "$key:$validValue,"
                                                    } else {
                                                        ""
                                                    }
                                                old + pair
                                            }.dropLastWhile { it == ',' }
                                        val type =
                                            if (pairs.isNotBlank()) {
                                                "{$pairs},"
                                            } else {
                                                ""
                                            }
                                        acc + type
                                    }.dropLastWhile { it == ',' } + "]"
                            gson.fromJsonTypeList(jsonString, request.clazz).getOrNull()?.let {
                                Mediator(
                                    name = request.name,
                                    keys = keys,
                                    values = it,
                                    clazz = request.clazz,
                                )
                            }
                        } else {
                            null
                        }
                    }
                }.awaitAll()
            }
        }.onSuccess { mediators ->
            mediators.filterNotNull().takeIf { it.isNotEmpty() }?.let {
                refreshSheet.value = Type.Sheet(it)
            }
        }
    }

    private suspend inline fun <reified T> editSheet(
        keyValue: KeyValue?,
        vararg requests: Request.(Sheet<T>, Int) -> Request,
    ) = workSheet.value.orEmpty().sheet<T>()?.let { sheet ->
        val response: suspend (Int) -> Result<BatchUpdateSpreadsheetResponse> = { rowIndex: Int ->
            ioCatching {
                service.batchUpdate(
                    SPREAD_SHEET_ID,
                    BatchUpdateSpreadsheetRequest().setRequests(
                        requests.map {
                            it.invoke(Request(), sheet, rowIndex)
                        },
                    ),
                ).execute()
            }.onSuccess {
                refreshSheet(SpreadSheet(sheet.name))
            }
        }

        keyValue?.let { kv ->
            retry {
                ioCatching {
                    service.values().get(SPREAD_SHEET_ID, sheet.name)
                        .execute()?.getValues()
                }
            }.getOrNull()?.let { data ->
                withDefaultCoroutine {
                    data.firstOrNull()?.indexOfFirst {
                        it.toString() == kv.key
                    }?.let { keyIndex ->
                        val index =
                            data.indexOfFirst { row ->
                                row.getOrNull(keyIndex) == kv.value
                            }
                        if (index >= 0) {
                            response(index)
                        } else {
                            null
                        }
                    }
                }
            } ?: Result.failure(NoRowIdException(sheet.name, kv.key, kv.value))
        } ?: response(-1)
    } ?: Result.failure(NoSheetException(T::class.java))

    private fun <T> range(
        s: Sheet<T>,
        index: Int,
    ) = DimensionRange()
        .setDimension("ROWS")
        .setSheetId(s.id)
        .setStartIndex(index)
        .setEndIndex(index + 1)

    private fun <T> Request.paste(
        keyValues: List<KeyValue>,
        sheet: Sheet<T>,
        index: Int,
    ) = setPasteData(
        PasteDataRequest().setDelimiter("\t")
            .setData(
                sheet.keys.joinToString("\t") { key ->
                    keyValues.find { it.key == key }?.value.orEmpty()
                },
            )
            .setCoordinate(
                GridCoordinate().setSheetId(sheet.id)
                    .setColumnIndex(0)
                    .setRowIndex(index),
            ),
    )
}