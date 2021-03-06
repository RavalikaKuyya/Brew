package io.github.koss.brew.data.remote

import android.net.Uri
import android.support.annotation.WorkerThread
import androidx.work.*
import io.github.koss.brew.data.model.Drink
import io.github.koss.brew.data.remote.worker.DrinkUploadWorker
import io.github.koss.brew.data.remote.worker.GetOrCreateAlbumWorker
import io.github.koss.brew.data.remote.worker.ImageUploadWorker
import io.github.koss.brew.data.remote.worker.util.*
import io.github.koss.brew.repository.config.ConfigurationWrapper
import kotlinx.android.synthetic.main.activity_camera.view.*
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
            enqueueDrink(drink)
        }
    }

    @WorkerThread
    suspend fun enqueueDrink(drink: Drink) {
        val config = ConfigurationWrapper.blockingFetch()

        // Get constraints
        val workConstraints = getUploadConstraints(config)

        // TODO - Make this periodic based off user preference

        // Start by getting the users album delete hash
        var continuation = WorkManager.getInstance().beginUniqueWork(
                drink.name + drink.id,
                ExistingWorkPolicy.REPLACE,
                OneTimeWorkRequestBuilder<GetOrCreateAlbumWorker>()
                        .setConstraints(workConstraints)
                        .build())

        // Upload the image if it exists
        drink.photoUri?.let {
            continuation = continuation.then(it.toUploadRequest(workConstraints))
        }

        // Upload the Drink
        continuation.then(drink.toUploadRequest(workConstraints))
                .enqueue()
    }

    private fun Uri.toUploadRequest(constraints: Constraints): OneTimeWorkRequest {
        return OneTimeWorkRequestBuilder<ImageUploadWorker>()
                .setInputData(
                        Data.Builder().putAll(mapOf(
                                KEY_IMAGE_URI to path
                        )).build()
                )
                .setConstraints(constraints)
                .setInputMerger(OverwritingInputMerger::class).build()
    }

    private fun Drink.toUploadRequest(constraints: Constraints): OneTimeWorkRequest {
        return OneTimeWorkRequestBuilder<DrinkUploadWorker>().setInputData(
                Data.Builder().putAll(mapOf(
                        KEY_DRINK_ID to id
                )).build()
                )
                .setConstraints(constraints)
                .setInputMerger(OverwritingInputMerger::class).build()
    }

    private fun getUploadConstraints(configurationWrapper: ConfigurationWrapper): Constraints {
        return Constraints.Builder()
                .setRequiredNetworkType(if (configurationWrapper.syncOver4G) NetworkType.CONNECTED else NetworkType.UNMETERED)
                .setRequiresBatteryNotLow(true)
                .build()
    }

}