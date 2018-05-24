package io.github.koss.brew.data.remote.image.imgur

import android.net.Uri
import io.github.koss.brew.data.remote.error.NotLoggedInException
import io.github.koss.brew.data.remote.image.imgur.model.request.CreateAlbumRequest
import io.github.koss.brew.data.remote.image.imgur.model.response.UploadImageResponseData
import io.github.koss.brew.util.Session
import io.github.koss.brew.util.extension.toMultipartImage
import io.github.koss.brew.util.getAlbumDeleteHash
import io.github.koss.brew.util.setAlbumDetails
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers

/**
 * Imgur-backed image service. Uses the Imgur public API by creating an album for the user
 * and then uploading images to that album. The user owns the album hash, and is allowed to delete
 * the album when they either delete their account, or remove remote storage.
 *
 * Note: When deleting images from Imgur, they have to be deleted one by one. This service doesn't
 * handle scheduling, but provides a [getRemainingQuota] endpoint to retrieve how many more "credits"
 * are allocated to this client. In future, there could potentially be client coordination implemented
 * and some form of remote queuing to fully utilise the quota
 */
class ImgurImageService(
        private val api: ImgurApi,
        private val session: Session
) {

    /**
     * Function for uploading drink - By default all Images are tied to the user ID and not the
     * drink, allowing possible extension to multiple drinks using the same image at a later date.
     *
     * Returns a single which emits the images deletehash
     */
    fun uploadImage(imageUri: Uri): Single<UploadImageResponseData> {
        val user = session.user ?: return Single.error(NotLoggedInException())


        // The following:
        // - Gets users album hash
        // - If the delete hash doesn't exist, create an album
        // - Upload image and discard redundant information
        return user
                .getAlbumDeleteHash()
                .switchIfEmpty(
                        api.createAlbum(createAlbumRequest = CreateAlbumRequest())
                                .map {
                                    user.setAlbumDetails(it.data.id, it.data.deleteHash)
                                            .subscribeOn(Schedulers.io())
                                            .subscribe()
                                    it.data.deleteHash
                                }
                )
                .flatMap {
                    api.uploadImage(
                            image = imageUri.toMultipartImage(),
                            albumDeleteHash = it)
                }.map {
                    // Remove BasicResponse as any errors will bubble an exception up to subscribers
                    it.data
                }
    }

    /**
     * Function for deleting image - By default all Images are tied to the user ID and not the
     * drink, allowing possible extension to multiple drinks using the same image at a later date
     */
    fun deleteImage(imageDeleteHash: String): Single<Boolean> {
        return api.deleteImage(imageDeleteHash)
                .map {
                    // Remove BasicResponse as any errors will bubble an exception up to subscribers
                    it.data
                }
    }

    /**
     * Function for getting the remaining image quota
     */
    fun getRemainingQuota(): Single<Int> {
        return api.getRemainingCredits().map { it.data.userRemaining }
    }
}


