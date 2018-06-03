package io.github.koss.brew.data.remote.worker

import android.util.Log
import androidx.work.Worker
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import io.github.koss.brew.data.model.Drink
import io.github.koss.brew.data.remote.error.NotLoggedInException
import io.github.koss.brew.data.remote.worker.util.*

/**
 * Worker for uploading a single Drink to the Firebase Cloud Firestore. Takes image parameters and the drink ID
 * as input
 */
class DrinkUploadWorker : Worker() {

    override fun doWork(): WorkerResult {

        Log.d("DrinkUpload", "Starting")
        val drinkRepository = applicationContext.getDrinkRepository()
        val firestore = FirebaseFirestore.getInstance()
        val user = FirebaseAuth.getInstance().currentUser ?: throw NotLoggedInException()

        Log.d("DrinkUpload", "Got user: ${user.uid}")

        val drinkId = inputData.keyValueMap[KEY_DRINK_ID]?.toString()
            ?: throw IllegalArgumentException("Missing Drink ID as input to drink upload")

        Log.d("DrinkUpload", "Got drink ID: $drinkId")

        val imageData = inputData.keyValueMap.filter {
            it.key == KEY_IMAGE_ID || it.key == KEY_IMAGE_DELETE_HASH || it.key == KEY_IMAGE_LINK
        }.mapValues { it.value.toString() }

        Log.d("DrinkUpload", "Got image data: $imageData")

        Log.d("DrinkUpload", "Getting drink")

        // NOTE - No default value for blockingGet will cause this to throw an exception
        val drinkData = drinkRepository.getDrinkById(drinkId).blockingGet().serialize()

        Log.d("DrinkUpload", "Adding drink to user: $drinkData")

        // Add the drink to the user
        val drinkDocument = firestore
                .document("users/${user.uid}")
                .collection("drinks")
                .add(drinkData)
                .addOnFailureListener { Log.e("DrinkUpload", "RIP", it) }
                .await()

        // Update the drink database
        drinkRepository.onDrinkUploaded(drinkId.toInt(), drinkDocument.id)

        if (imageData.isNotEmpty()) {
            Log.d("DrinkUpload", "Adding drink image data to ${drinkDocument.path}")

            drinkDocument
                    .collection("images")
                    .add(imageData)
                    .await()
        }

        return WorkerResult.SUCCESS
    }

    private fun Drink.serialize(): Map<String, Any> = mapOf(
            KEY_NAME to name,
            KEY_DESCRIPTION to description!!,
            KEY_TAGS to tags
    )
}
