package com.fang.cosmos.definition.datastore.noscoped

import android.content.Context
import com.fang.cosmos.definition.CosmosDef
import com.fang.cosmos.definition.datastore.QualifierAwareDataStore
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.flowOf

abstract class NonQualifierDataStore(
    gson: Gson = CosmosDef.Gson,
    context: Context = CosmosDef.Context,
    coroutineScope: CoroutineScope = CosmosDef.CoroutineScope,
) : QualifierAwareDataStore(gson, context, coroutineScope) {

    override val qualifierKeyFlow = flowOf("")
}

