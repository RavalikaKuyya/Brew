package io.github.koss.brew.data.remote

import android.net.Uri
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import io.github.koss.brew.data.remote.image.imgur.ImgurImageService
import io.reactivex.Completable
import io.reactivex.Single
import io.reactivex.SingleEmitter

// Number of Imgur credits to not exceed in a cycle with image uploads
private val CREDIT_BUFFER = 50

/**
 * Class for handling network-related drink things
 */
class DrinkService(
        private val imageService: ImgurImageService,
        private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
) {

    /**
     * Saves a drink to the firestore under the current users drink doc collection
     */
    fun saveDrink(name: String, description: String, tags: Array<String>, image: Uri): Completable = Completable.create { emitter ->
        // Fail early if the user isn't logged in
        val user = FirebaseAuth.getInstance().currentUser ?: throw IllegalStateException("User must be logged in to save a drink remotely")

        val drink = mapOf(
                "name" to name,
                "description" to description,
                "tags" to tags
        )

        // Upload image
        val imageResult = uploadImage(image).blockingGet()

        if (emitter.isDisposed) return@create

        // The following creates a drink in the users drinks collection, and adds images to it
        firestore.collection("users/${user.uid}/drinks").add(drink).addOnSuccessListener {
            it.collection("images").add(imageResult).addOnSuccessListener {
                emitter.onComplete()
            }
        }
    }

    /**
     * Function for uploading an image and formatting the response into a Firebase Cloud Firestore map
     */
    private fun uploadImage(imageUri: Uri): Single<Map<String, Any>> =
        imageService.uploadImage(imageUri)
                .map { mapOf(
                        "image_id" to it.id,
                        "image_delete_hash" to it.deleteHash,
                        "image_link" to it.link
                )}

}