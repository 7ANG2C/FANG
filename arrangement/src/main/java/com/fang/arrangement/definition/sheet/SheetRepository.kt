package com.fang.arrangement.definition.sheet

import android.content.Context
import com.fang.arrangement.definition.foundation.KeyValue
import com.fang.cosmos.foundation.fromJsonTypeList
import com.fang.cosmos.foundation.indexOfFirstOrNull
import com.fang.cosmos.foundation.retry
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
import kotlinx.coroutines.flow.mapLatest
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

    private companion object {
        const val MAIN = true
        val SPREAD_SHEET_ID =
            if (MAIN) {
                "1hYhuc7IYnVkjx6qK7WePQiTF7Jw9ZUwC-pU8DMVcNdI"
            } else {
//                "1Z7uSrOTASCKYvEydJ_QTClgwaOPvq_xuRqxKGKzrc34"
                "1jj5ejgD-FtGH6c2tXNtrAEPDmGsAR_n2yDWRIXRBQac"
            }
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

    private val _workSheets = MutableStateFlow<List<WorkSheet>?>(null)
    val workSheets = _workSheets.asStateFlow()

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
                .mapLatest { sheets ->
                    sheets?.takeIf {
                        val sheet = it.firstOrNull()
                        sheet?.id != invalidSheetId && !sheet?.keys.isNullOrEmpty()
                    }
                }
                .filterNotNull()
                .flowOn(Dispatchers.Default)
                .collectLatest {
                    _workSheets.value = it
                }
        }
    }

    suspend inline fun <reified T> insert(keyValues: List<KeyValue>) =
        editSheet<T>(
            keyValue = null,
            { sheet, _ ->
                setInsertDimension(InsertDimensionRequest().setRange(range(sheet, 1)))
            },
            { sheet, _ -> paste(keyValues, sheet, 1) },
        )

    suspend inline fun <reified T> update(
        key: String,
        value: String,
        keyValues: List<KeyValue>,
    ) = editSheet<T>(
        keyValue = KeyValue(key, value),
        { sheet, index -> paste(keyValues, sheet, index) },
    )

    suspend inline fun <reified T> delete(
        key: String,
        value: String,
    ) = editSheet<T>(
        keyValue = KeyValue(key, value),
        { sheet, index ->
            setDeleteDimension(DeleteDimensionRequest().setRange(range(sheet, index)))
        },
    )

    suspend inline fun <reified T> deletes(
        key: String,
        values: List<String>,
    ) = workSheets.value.orEmpty().sheet<T>()?.let { sheet ->
        sheet.getSpreedSheetValues().getOrNull()?.let { data ->
            withDefaultCoroutine {
                data.firstOrNull()?.indexOfFirstOrNull {
                    it.toString() == key
                }?.let { keyIndex ->
                    values.mapNotNull { value ->
                        data.indexOfFirstOrNull { row ->
                            row.getOrNull(keyIndex) == value
                        }
                    }
                }?.takeIf { it.isNotEmpty() }?.sortedDescending()?.map {
                    Request().setDeleteDimension(
                        DeleteDimensionRequest()
                            .setRange(range(sheet, it)),
                    )
                }?.let { requests ->
                    sheet.batchUpdate(requests)
                }
            }
        } ?: Result.failure(NoRowIdException(sheet.name, key, values.toString()))
    } ?: Result.failure(NoSheetException(T::class.java))

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
                                                        val t =
                                                            if (validValue.startsWith("[")) {
                                                                validValue
                                                            } else {
                                                                "\"$validValue\""
                                                            }
                                                        "\"$key\":$t,"
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
    ) = workSheets.value.orEmpty().sheet<T>()?.let { sheet ->
        keyValue?.let { kv ->
            sheet.getSpreedSheetValues().getOrNull()?.let { data ->
                withDefaultCoroutine {
                    data.firstOrNull()?.indexOfFirstOrNull {
                        it.toString() == kv.key
                    }?.let { keyIndex ->
                        data.indexOfFirstOrNull { row ->
                            row.getOrNull(keyIndex) == kv.value
                        }?.let { i -> sheet.batchUpdate(requests.map { it(Request(), sheet, i) }) }
                    }
                }
            } ?: Result.failure(NoRowIdException(sheet.name, kv.key, kv.value))
        } ?: sheet.batchUpdate(requests.map { it(Request(), sheet, -1) })
    } ?: Result.failure(NoSheetException(T::class.java))

    private suspend inline fun <reified T> Sheet<T>.getSpreedSheetValues() =
        retry {
            ioCatching {
                service.values().get(SPREAD_SHEET_ID, name)
                    .execute()?.getValues()
            }
        }

    private suspend inline fun <reified T> Sheet<T>.batchUpdate(requests: List<Request>) =
        ioCatching {
            service.batchUpdate(
                SPREAD_SHEET_ID,
                BatchUpdateSpreadsheetRequest().setRequests(requests),
            ).execute()
        }.onSuccess {
            refreshSheet(SpreadSheet(name))
        }

    private fun <T> range(
        sheet: Sheet<T>,
        index: Int,
    ) = DimensionRange()
        .setDimension("ROWS")
        .setSheetId(sheet.id)
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
