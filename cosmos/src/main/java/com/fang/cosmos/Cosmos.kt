package com.fang.cosmos

import androidx.datastore.core.DataStore

/**
 * Cosmos Helper
 */
internal object Cosmos {

    /**
     * Module qualifier.
     * For the purpose of prefix for [DataStore] ..., or other else.
     */
    const val QUALIFIER = "f_cosmos"

    fun err(msg: String): Nothing = error("Cosmos module error: $msg")
}