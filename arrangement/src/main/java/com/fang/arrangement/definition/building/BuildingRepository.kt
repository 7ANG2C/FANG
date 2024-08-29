package com.fang.arrangement.definition.building

import com.fang.arrangement.definition.DataStoreKey
import com.fang.cosmos.definition.datastore.QualifierAwareDataStore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

internal class BuildingRepository(
    private val dataStore: QualifierAwareDataStore,
    private val scope: CoroutineScope
) {
    private val key = DataStoreKey.buildings

    fun invoke() = dataStore.getDataFlow<List<Building>>(key)
    fun add(name: String) {
        scope.launch {
            dataStore.update<List<Building>>(key = key) { old ->
                old.orEmpty() + Building(id = System.currentTimeMillis(), name = name)
            }
        }
    }

    fun edit(id: Long, name: String) {
        scope.launch {
            dataStore.update<List<Building>>(key = key) { old ->
                old?.map {
                    if (it.id == id) it.copy(name = name) else it
                }
            }
        }
    }

    fun delete(id: Long) {
        scope.launch {
            dataStore.update<List<Building>>(key = key) { old ->
                old?.mapNotNull {
                    if (it.id == id) null else it
                }
            }
        }
    }

}