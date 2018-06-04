package io.github.koss.brew.data.remote.worker

import android.util.Log
import androidx.work.Worker
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import io.github.koss.brew.data.model.DrinkAdded
import io.github.koss.brew.data.model.Drink
import io.github.koss.brew.data.remote.error.NotLoggedInException
import io.github.koss.brew.data.remote.worker.util.*

/**
 * Worker for uploading a single Drink to the Firebase Cloud Firestore. Takes image parameters and the drink ID
 * as input, and returns the drink path
 */
class DrinkUploadWorker : Worker() {

    override fun doWork(): WorkerResult {
        "Starting Drink Upload".d()
        val drinkRepository = applicationContext.getDrinkRepository()
        val firestore = FirebaseFirestore.getInstance()
        val user = FirebaseAuth.getInstance().currentUser ?: throw NotLoggedInException()

        "User's logged in".d()

        val drinkId = inputData.keyValueMap[KEY_DRINK_ID]?.toString()
            ?: throw IllegalArgumentException("Missing Drink ID as input to drink upload")

        "Got a drink ID: $drinkId".d()

        val imageData = inputData.keyValueMap.filter {
            it.key == KEY_IMAGE_ID || it.key == KEY_IMAGE_DELETE_HASH || it.key == KEY_IMAGE_LINK
        }.mapValues { it.value.toString() }

        "Got image data: $imageData".d()

        // NOTE - No default value for blockingGet will cause this to throw an exception
        val drink = drinkRepository.getDrinkById(drinkId).doOnError { "Room errored".e(Exception(it)) }.blockingGet()
        val drinkData = drink.serialize()

        "Got all drink data".d()

        // Add the drink to the user
        val drinkDocument = firestore
                .document("users/${user.uid}")
                .collection("drinks")
                .add(drinkData)
                .addOnFailureListener {
                    "Adding the drink failed".e(it)
                }
                .await()

        // Update the drink database
        drinkRepository.onDrinkUploaded(drinkId.toInt(), drinkDocument.id)

        "Drink uploaded".d()

        if (imageData.isNotEmpty()) {
            drinkDocument
                    .collection("images")
                    .add(imageData)
                    .addOnFailureListener {
                        "Adding the image data to the drink failed".e(it)
                    }
                    .await()
        }

        // Add an add drink activity to the users activity
        firestore
                .document("users/${user.uid}")
                .collection("activity")
                .add(DrinkAdded(
                        imageLink = imageData[KEY_IMAGE_LINK]!!,
                        drinkName = drinkData[KEY_NAME] as String,
                        drinkDescription = drinkData[KEY_DESCRIPTION] as String,
                        drinkId = drinkDocument.id,
                        timestamp = drink.timestamp
                ))
                .addOnFailureListener {
                    "Inserting new activity failed".e(it)
                }.await()

        return WorkerResult.SUCCESS
    }

    private fun Drink.serialize(): Map<String, Any> = mapOf(
            KEY_NAME to name,
            KEY_DESCRIPTION to description!!,
            KEY_TAGS to tags
    )

    private fun String.d() {
        Log.d("DrinkUploadWorker", this)
    }

    private fun String.e(exception: Exception) {
        Log.e("DrinkUploadWorker", this, exception)
    }
}
