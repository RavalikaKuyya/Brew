package io.github.koss.brew.data.remote.worker

import android.net.Uri
import android.util.Log
import androidx.work.Data
import androidx.work.Worker
import io.github.koss.brew.data.remote.image.imgur.model.response.UploadImageResponseData
import io.github.koss.brew.data.remote.worker.util.*

/**
 * Worker for uploading a single image to Imgur. Takes an album ID and image URI and returns
 * upload information, i.e the photo delete hash, photo ID and photo link
 */
class ImageUploadWorker : Worker() {

    override fun doWork(): WorkerResult {
        "Starting upload".d()

        val api = applicationContext.getImgurApi()

        val albumDeleteHash = inputData.keyValueMap[KEY_ALBUM_DELETE_HASH] as? String
                ?: throw IllegalArgumentException("Missing Album Delete Hash as input to image upload")

        "Retrieved delete hash".d()

        val imagePath = inputData.keyValueMap[KEY_IMAGE_URI] as? String
                ?: throw IllegalArgumentException("Missing Image URI as input to image upload")

        "Got Image path".d()

        val imageUri = Uri.parse(imagePath)

        val response = api.uploadImage(albumDeleteHash, imageUri).blockingGet()

        return when {
            response.success -> {
                outputData = response.data.toData()
                WorkerResult.SUCCESS
            }
            else -> WorkerResult.FAILURE
        }
    }

    private fun UploadImageResponseData.toData(): Data =
            Data.Builder().putAll(mapOf(
                    KEY_IMAGE_ID to id,
                    KEY_IMAGE_DELETE_HASH to deleteHash,
                    KEY_IMAGE_LINK to link
            )).build()

    private fun String.d() {
        Log.d("ImageUploadWorker", this)
    }

    private fun String.e(exception: Exception) {
        Log.e("ImageUploadWorker", this, exception)
    }
}