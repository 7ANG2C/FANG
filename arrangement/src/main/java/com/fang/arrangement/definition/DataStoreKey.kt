package com.fang.arrangement.definition

internal interface DataStoreKey {
    companion object {
        val attendanceSummary = getKey("attendanceSummary")
        val buildings = getKey("buildings")
        val employees = getKey("employees")
        val loans = getKey("loans")
        private fun getKey(key: String) = "ARR-$key"
    }
}