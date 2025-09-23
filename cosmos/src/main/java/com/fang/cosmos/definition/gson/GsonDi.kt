package com.fang.cosmos.definition.gson

import com.fang.cosmos.definition.singleCosmos
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.Strictness
import org.koin.core.definition.Definition
import org.koin.core.module.Module

/**
 * Koin [Module] extension for [Gson] D.I. .
 */
fun Module.cosmosModuleGson(
    createdAtStart: Boolean = false,
    override: Definition<Gson>? = null,
) = singleCosmos<Gson>(createdAtStart) { override?.invoke(this, it) ?: createGson() }

private fun createGson() =
    GsonBuilder()
        .serializeNulls()
        .setStrictness(Strictness.LENIENT)
        .setPrettyPrinting()
        .serializeSpecialFloatingPointValues()
        .enableComplexMapKeySerialization()
        .create()
