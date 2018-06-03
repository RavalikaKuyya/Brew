package io.github.koss.brew.repository.config

import com.crashlytics.android.Crashlytics
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import io.github.koss.brew.data.remote.error.NotLoggedInException
import kotlinx.coroutines.experimental.suspendCancellableCoroutine
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

class ConfigurationWrapper(private val onLoaded: () -> Unit = {}) {

    var shouldSyncDrinks by config(
            configurationName = "should_sync",
            default = true)

    var syncOver4G by config(
            configurationName = "sync_over_4g",
            default = false
    )

    var shouldSyncImmediately by config(
            configurationName = "should_sync_immediately",
            default = true
    )

    var publicByDefault by config(
            configurationName = "public_by_default",
            default = false
    )

    private var loadedCount = 0
    private val totalCount = 4

    private var loadCallback = {}

    private fun loaded() {
        onLoaded()
        loadedCount += 1

        if (loadedCount == totalCount) {
            loadCallback()
        }
    }

    suspend fun waitUntilLoaded() = suspendCancellableCoroutine<Unit?> {
        if (loadedCount == totalCount) {
            it.resume(null)
            return@suspendCancellableCoroutine
        }

        loadCallback = {
            it.resume(null)
        }
    }

    private fun <T> config(configurationName: String, default: T) = FirebaseConfigDelegate(configurationName, default)

    /**
     * Class which handles user configuration which should be persisted to Firebase.
     */
    inner class FirebaseConfigDelegate<T>(
            private val configurationName: String,
            private val defaultValue: T
    ) : ReadWriteProperty<Any, T> {

        private val configRef by lazy {
            val userUid = FirebaseAuth.getInstance().currentUser?.uid ?: throw NotLoggedInException()

            FirebaseFirestore.getInstance()
                    .document("users/$userUid/misc/config")
        }

        private var cachedValue: T? = null

        private var notified = false

        init {
            configRef.addSnapshotListener { documentSnapshot, firebaseFirestoreException ->
                // Short-circuit if an exception occurs
                firebaseFirestoreException?.run {
                    Crashlytics.logException(this)
                    return@addSnapshotListener
                }

                @Suppress("UNCHECKED_CAST")
                val newValue = documentSnapshot?.get(configurationName) as T?

                // Notify loaded
                if (cachedValue == null && newValue != null && !notified) {
                    loaded()
                    notified = true
                }

                cachedValue = newValue
            }
        }

        override fun getValue(thisRef: Any, property: KProperty<*>): T {
            return cachedValue ?: defaultValue
        }

        override fun setValue(thisRef: Any, property: KProperty<*>, value: T) {
            configRef.update(mapOf(configurationName to value))
                    .addOnFailureListener {
                        Crashlytics.logException(it)
                    }
        }
    }
}