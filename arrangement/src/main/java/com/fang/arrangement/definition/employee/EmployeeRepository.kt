package com.fang.arrangement.definition.employee

import com.fang.arrangement.definition.DataStoreKey
import com.fang.cosmos.definition.datastore.QualifierAwareDataStore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

internal class EmployeeRepository(
    private val dataStore: QualifierAwareDataStore,
    private val scope: CoroutineScope
) {
    private val key = DataStoreKey.employees

    fun invoke() = dataStore.getDataFlow<List<Employee>>(key)
    fun add(name: String) {
        scope.launch {
            dataStore.update<List<Employee>>(key = key) { old ->
                old.orEmpty() + Employee(id = System.currentTimeMillis(), name = name, emptyList())
            }
        }
    }

    fun edit(id: Long, name: String) {
        scope.launch {
            dataStore.update<List<Employee>>(key = key) { old ->
                old?.map {
                    if (it.id == id) it.copy(name = name) else it
                }
            }
        }
    }

    fun delete(id: Long) {
        scope.launch {
            dataStore.update<List<Employee>>(key = key) { old ->
                old?.mapNotNull {
                    if (it.id == id) null else it
                }
            }
        }
    }

}