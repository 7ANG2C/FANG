package com.fang.arrangement.definition.loan

import com.fang.arrangement.definition.DataStoreKey
import com.fang.cosmos.definition.datastore.QualifierAwareDataStore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

internal class LoanRepository(
    private val dataStore: QualifierAwareDataStore,
    private val scope: CoroutineScope
) {
    private val key = DataStoreKey.loans

    fun invoke() = dataStore.getDataFlow<List<Loan>>(key)
    fun add(
        employeeAId: Long,
        employeeBId: Long,
        loanMillis: Long,
        remark: String,
        money: Long,
    ) {
        scope.launch(Dispatchers.Default) {
            dataStore.update<List<Loan>>(key = key) { old ->
                (old.orEmpty() + Loan(
                    id = System.currentTimeMillis(),
                    employeeAId = employeeAId,
                    employeeBId = employeeBId,
                    loanMillis = loanMillis,
                    money = money,
                    remark = remark,
                    records = emptyList()
                )).sortedByDescending { it.id }
            }
        }
    }

    fun edit(id: Long, money: Long) {
        scope.launch(Dispatchers.Default) {
            dataStore.update<List<Loan>>(key = key) { old ->
                old?.map {
                    if (it.id == id) it.copy(
                        records = (it.records + Record(
                            System.currentTimeMillis(),
                            money
                        )).sortedByDescending { it.id }
                    ) else it
                }
            }
        }
    }

    fun delete(id: Long) {
        scope.launch(Dispatchers.Default) {
            dataStore.update<List<Loan>>(key = key) { old ->
                old?.mapNotNull {
                    if (it.id == id) null else it
                }
            }
        }
    }

    fun removeInvalidIds(ids: List<Long>) {
        scope.launch {
            dataStore.update<List<Loan>>(key = key) { old ->
                old?.mapNotNull { summary ->
                    if (summary.employeeAId in ids || summary.employeeBId in ids) {
                        null
                    } else summary
                }
            }
        }
    }


}