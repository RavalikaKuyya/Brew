package io.github.koss.brew.data.remote.worker

import androidx.work.*
import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.Tasks
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Source
import io.github.koss.brew.data.remote.error.NotLoggedInException
import io.github.koss.brew.data.remote.image.imgur.model.response.CreateAlbumResponseData
import io.github.koss.brew.data.remote.worker.util.*

/**
 * Worker for creating an Imgur Album. Takes nothing as an input and outputs album delete hash
 */
class GetOrCreateAlbumWorker : Worker() {

    override fun doWork(): WorkerResult {
        val api = applicationContext.getImgurApi()
        val currentUserUid = FirebaseAuth.getInstance().currentUser?.uid
                ?: throw NotLoggedInException()

        val currentUser = FirebaseAuth.getInstance().currentUser

        val userReference = FirebaseFirestore.getInstance().document("/users/$currentUserUid")

        if (!userReference.get(Source.SERVER).await().exists()) {
            // If it doesn't exist, make the new user object
            FirebaseFirestore.getInstance().collection("users")
                    .document(currentUserUid)
                    .set(mapOf(
                          "name" to currentUser?.displayName
                    )).await()
        }

        val albumCreationTaggedWork = emptyList<WorkStatus>()

        val firebaseAlbumDeleteHash = userReference.get().await().get(KEY_ALBUM_DELETE_HASH)

        // If the firebase album hash exists, short-circuit the job here
        firebaseAlbumDeleteHash?.run {
            outputData = Data.Builder().putAll(mapOf(
                    KEY_ALBUM_DELETE_HASH to (this as String)
            )).build()

            return WorkerResult.SUCCESS
        }

        // The following statement indicates something is already trying to create an album. Check if any of the tagged workers
        // are enqueued or running and return a retry signal, otherwise continue
        if (albumCreationTaggedWork.isNotEmpty() && firebaseAlbumDeleteHash == null) {
            if (albumCreationTaggedWork.any { it.state == State.ENQUEUED || it.state == State.RUNNING }) {
                return WorkerResult.RETRY
            }
        }

        // Create an album, set the fields in the user object and return
        val result = api.createAlbum().blockingGet()

        return if (result.success) {
            outputData = result.data.toData()

            // Run user update synchronously
            userReference.set(mapOf(
                    KEY_ALBUM_ID to result.data.id,
                    KEY_ALBUM_DELETE_HASH to result.data.deleteHash
            )).await()

            WorkerResult.SUCCESS
        } else {
            WorkerResult.FAILURE
        }
    }

    private fun CreateAlbumResponseData.toData(): Data =
            Data.Builder().putAll(mapOf(
                    KEY_ALBUM_DELETE_HASH to deleteHash
            )).build()


}