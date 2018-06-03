package io.github.koss.brew.data.remote

import android.net.Uri
import androidx.work.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import io.github.koss.brew.data.model.Drink
import io.github.koss.brew.data.remote.worker.DrinkUploadWorker
import io.github.koss.brew.data.remote.worker.GetOrCreateAlbumWorker
import io.github.koss.brew.data.remote.worker.ImageUploadWorker
import io.github.koss.brew.data.remote.worker.util.*

/**
 * Class for handling network-related drink things
 */
class DrinkService {

    /**
     * Saves a drink to the firestore under the current users drink doc collection
     */
    fun enqueueDrinkUpload(drink: Drink) {
        val workConstraints = getUploadConstraints()

        // Start by getting the users album delete hash
        var continuation = WorkManager.getInstance().beginWith(
                OneTimeWorkRequestBuilder<GetOrCreateAlbumWorker>()
                        .setConstraints(workConstraints)
                        .addTag(TAG_ALBUM_CREATION)
                        .build())

        // Upload the image if it exists
        drink.photoUri?.let {
            continuation = continuation.then(it.toUploadRequest())
        }

        // Upload the Drink
        continuation.then(drink.toUploadRequest())
                .enqueue()
    }

    private fun Uri.toUploadRequest(): OneTimeWorkRequest {
        return OneTimeWorkRequestBuilder<ImageUploadWorker>().setInputData(
                Data.Builder().putAll(mapOf(
                        KEY_IMAGE_URI to path
                )).build()
        ).setInputMerger(OverwritingInputMerger::class).build()
    }

    private fun Drink.toUploadRequest(): OneTimeWorkRequest {
        return OneTimeWorkRequestBuilder<DrinkUploadWorker>().setInputData(
                Data.Builder().putAll(mapOf(
                        KEY_DRINK_ID to id
                )).build()
        ).setInputMerger(OverwritingInputMerger::class).build()
    }

    private fun getUploadConstraints(): Constraints {
        return Constraints.Builder()
                .setRequiredNetworkType(NetworkType.UNMETERED)
                .setRequiresBatteryNotLow(true)
                .build()
    }

}