package io.github.koss.brew.data.remote

import android.net.Uri
import androidx.work.*
import io.github.koss.brew.data.model.Drink
import io.github.koss.brew.data.remote.worker.DrinkUploadWorker
import io.github.koss.brew.data.remote.worker.GetOrCreateAlbumWorker
import io.github.koss.brew.data.remote.worker.ImageUploadWorker
import io.github.koss.brew.data.remote.worker.util.*
import io.github.koss.brew.repository.config.ConfigurationWrapper
import kotlinx.coroutines.experimental.launch

/**
 * Class for handling network-related drink things
 */
class DrinkService {

    /**
     * Saves a drink to the firestore under the current users drink doc collection
     */
    fun enqueueDrinkUpload(drink: Drink) {
        launch {
            val config = ConfigurationWrapper.blockingFetch()

            // Get constraints
            val workConstraints = getUploadConstraints(config)

            // TODO - Make this periodic based off user preference

            // Start by getting the users album delete hash
            var continuation = WorkManager.getInstance().beginUniqueWork(
                    drink.name + drink.id,
                    ExistingWorkPolicy.KEEP,
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

    private fun getUploadConstraints(configurationWrapper: ConfigurationWrapper): Constraints {
        return Constraints.Builder()
                .setRequiredNetworkType(if (configurationWrapper.syncOver4G) NetworkType.CONNECTED else NetworkType.UNMETERED)
                .setRequiresBatteryNotLow(true)
                .build()
    }

}