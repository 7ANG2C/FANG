package com.fang.cosmos.foundation.log

import android.util.Log
import com.fang.cosmos.BuildConfig

/**
 * quick usage for [Log.d]
 */
fun logD(tag: String, vararg any: Any?) {
    if (BuildConfig.DEBUG) {
        Log.d(tag, any.joinToString(", "))
    }
}
