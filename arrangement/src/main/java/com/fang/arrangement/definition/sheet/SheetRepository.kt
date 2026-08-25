package com.fang.arrangement.definition.sheet

import com.fang.arrangement.Arrangement
import com.fang.arrangement.definition.AttendanceAll
import com.fang.arrangement.definition.Boss
import com.fang.arrangement.definition.Employee
import com.fang.arrangement.definition.Fund
import com.fang.arrangement.definition.Loan
import com.fang.arrangement.definition.Payback
import com.fang.arrangement.definition.Site
import com.fang.arrangement.definition.firestore.FsAttendanceAll
import com.fang.arrangement.definition.firestore.FsBoss
import com.fang.arrangement.definition.firestore.FsEmployee
import com.fang.arrangement.definition.firestore.FsFund
import com.fang.arrangement.definition.firestore.FsLoan
import com.fang.arrangement.definition.firestore.FsPayback
import com.fang.arrangement.definition.firestore.FsSite
import com.fang.arrangement.definition.firestore.domain
import com.fang.arrangement.definition.firestore.firestore
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.MetadataChanges
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

/** Firestore repository with DTOs that keep Firebase mapping out of the UI domain. */
internal class SheetRepository(
    coroutineScope: CoroutineScope,
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance(),
) {
    @PublishedApi
    internal interface Collection {
        val name: String
        val domainClass: Class<out Any>

        fun read(document: DocumentSnapshot): Any?

        fun document(value: Any): Any

        fun id(value: Any): String
    }

    @PublishedApi
    internal data class TypedCollection<T : Any, D : Any>(
        override val name: String,
        override val domainClass: Class<T>,
        private val documentClass: Class<D>,
        private val toDomain: (D) -> T,
        private val toFirestore: (T) -> D,
        private val documentId: (T) -> Long,
    ) : Collection {
        override fun read(document: DocumentSnapshot): T? = document.toObject(documentClass)?.let(toDomain)

        override fun document(value: Any) = toFirestore(requireNotNull(domainClass.cast(value)))

        override fun id(value: Any) = documentId(requireNotNull(domainClass.cast(value))).toString()
    }

    @PublishedApi
    internal val collections: List<Collection> =
        listOf(
            TypedCollection(
                "attendance",
                AttendanceAll::class.java,
                FsAttendanceAll::class.java,
                FsAttendanceAll::domain,
                AttendanceAll::firestore,
                AttendanceAll::id,
            ),
            TypedCollection("loans", Loan::class.java, FsLoan::class.java, FsLoan::domain, Loan::firestore, Loan::id),
            TypedCollection("funds", Fund::class.java, FsFund::class.java, FsFund::domain, Fund::firestore, Fund::id),
            TypedCollection(
                "paybacks",
                Payback::class.java,
                FsPayback::class.java,
                FsPayback::domain,
                Payback::firestore,
                Payback::id,
            ),
            TypedCollection("bosses", Boss::class.java, FsBoss::class.java, FsBoss::domain, Boss::firestore, Boss::id),
            TypedCollection(
                "employees",
                Employee::class.java,
                FsEmployee::class.java,
                FsEmployee::domain,
                Employee::firestore,
                Employee::id,
            ),
            TypedCollection("sites", Site::class.java, FsSite::class.java, FsSite::domain, Site::firestore, Site::id),
        )

    @PublishedApi
    internal val rootDoc = firestore.collection("environments").document(Arrangement.current.id)

    private val _workSheets = MutableStateFlow<List<WorkSheet>?>(null)
    val workSheets = _workSheets.asStateFlow()

    init {
        coroutineScope.launch {
            combine(collections.map(::observeCollection)) { it.toList() }
                .collectLatest { _workSheets.value = it }
        }
    }

    private fun observeCollection(collection: Collection) =
        callbackFlow {
            val registration =
                rootDoc.collection(collection.name).addSnapshotListener(MetadataChanges.INCLUDE) { snapshot, error ->
                    if (error != null) {
                        close(error)
                    } else {
                        if (snapshot == null || snapshot.metadata.isFromCache) {
                            return@addSnapshotListener
                        } else {
                            val values =
                                snapshot.documents.mapNotNull { doc ->
                                    runCatching {
                                        collection.read(doc)
                                    }.getOrNull()
                                }
                            trySend(
                                WorkSheet(
                                    values = values,
                                    clazz = collection.domainClass,
                                ),
                            )
                        }
                    }
                }
            awaitClose(registration::remove)
        }

    suspend inline fun <reified T : Any> insert(value: T): Result<Unit> = write(value)

    suspend inline fun <reified T : Any> update(value: T): Result<Unit> = write(value)

    suspend inline fun <reified T : Any> delete(id: String): Result<Unit> = runCatching { collection<T>().document(id).delete().await() }

    suspend inline fun <reified T : Any> deletes(ids: List<String>): Result<Unit> =
        runCatching {
            firestore
                .runBatch { batch ->
                    ids.forEach { batch.delete(collection<T>().document(it)) }
                }.await()
        }

    private suspend inline fun <reified T : Any> write(value: T): Result<Unit> =
        runCatching {
            val mapper = mapper<T>()
            rootDoc
                .collection(mapper.name)
                .document(mapper.id(value))
                .set(mapper.document(value))
                .await()
        }

    @PublishedApi
    internal inline fun <reified T : Any> collection() = rootDoc.collection(mapper<T>().name)

    @PublishedApi
    internal inline fun <reified T : Any> mapper() = collections.first { it.domainClass == T::class.java }
}
