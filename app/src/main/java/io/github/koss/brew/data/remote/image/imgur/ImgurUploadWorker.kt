package io.github.koss.brew.data.remote.image.imgur

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.net.Uri
import android.os.Build
import android.support.v4.app.NotificationCompat
import android.support.v4.app.NotificationManagerCompat
import androidx.work.Data
import androidx.work.Worker
import io.github.koss.brew.R
import io.github.koss.brew.di.component.ApplicationComponent
import io.github.koss.brew.di.component.DaggerApplicationComponent
import io.github.koss.brew.di.module.ApplicationModule

/**
 * Upload worker which uploads one or more images via input URIs, displaying a subtle progress
 * notification
 */
class ImgurUploadWorker : Worker() {

    private var uploadCount = 0
    private var maxProgress = 0

    private var applicationComponent: ApplicationComponent? = null

    override fun doWork(): WorkerResult {
        val context = applicationContext
        val imageService = context.initialiseImageService()
        val imageStrings = inputData.getStringArray(KEY_IMAGE_URIS)
        val imageUris = imageStrings.map { Uri.parse(it) }
        maxProgress = imageUris.size

        context.updateNotification()

        val results = imageUris.map {
            context.incrementProgressCount()

            try {
                val result = imageService.uploadImage(it).blockingGet()
                return@map UploadResult.Success(imageId = result.id, imageDeleteHash = result.deleteHash, link = result.link)
            } catch (e: Exception) {
                e.printStackTrace()
                return@map UploadResult.Failure(e)
            }
        }

        context.cancelNotification()

        val failureMap = imageUris.withIndex()
                .associate { (index, value) ->
                    value to results[index]
                }.mapNotNull { (uri, result) ->
                    return@mapNotNull when (result) {
                        is UploadResult.Success -> {
                            context.persistDrinkResult(uri, result)
                            null
                        }
                        else -> uri
                    }
                }

        val returnData = failureMap.map(Uri::getPath)

        outputData = Data.Builder().apply { putStringArray(KEY_FAILED_PATHS, returnData.toTypedArray()) }.build()

        // Always succeed. The results will be checked from outputData later
        return WorkerResult.SUCCESS
    }

    private fun Context.incrementProgressCount() {
        uploadCount = uploadCount++
        updateNotification()
    }

    private fun Context.cancelNotification() {
        NotificationManagerCompat.from(this).cancel(NOTIFICATION_ID)
    }

    private fun Context.updateNotification() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = VERBOSE_NOTIFICATION_CHANNEL_NAME
            val description = VERBOSE_NOTIFICATION_CHANNEL_DESCRIPTION
            val importance = NotificationManager.IMPORTANCE_MIN
            val channel = NotificationChannel(CHANNEL_ID, name, importance)
            channel.description = description

            val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

            notificationManager.createNotificationChannel(channel)
        }

        // Create the notification
        val builder = NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(NOTIFICATION_TITLE)
                .setContentText("Uploading $uploadCount of $maxProgress images")
                .setPriority(NotificationCompat.PRIORITY_MIN)
                .setProgress(maxProgress, uploadCount, false)
                .setVibrate(LongArray(0))

        // Show the notification
        NotificationManagerCompat.from(this).notify(NOTIFICATION_ID, builder.build())
    }

    private fun Context.initialiseImageService(): ImgurImageService {
        // Create Imgur API instance
        return getApplicationComponent(this).imageService()
    }

    private fun Context.persistDrinkResult(drinkImageUri: Uri, result: UploadResult.Success) {
        val repo = getApplicationComponent(this).drinkRepository()
        repo.updateDrinkUploadStatus(drinkImageUri,
                imageId = result.imageId,
                imageDeleteHash = result.imageDeleteHash,
                imageLink = result.link)
    }


    private fun getApplicationComponent(context: Context): ApplicationComponent {
        if (applicationComponent == null) {
            applicationComponent =  DaggerApplicationComponent.builder()
                    .applicationModule(ApplicationModule(context as Application))
                    .build()
        }

        return applicationComponent!!
    }

    /**
     * Upload result for image uploads
     */
    sealed class UploadResult {

        data class Success(
                val imageId: String,
                val imageDeleteHash: String,
                val link: String) : UploadResult()

        data class Failure(val exception: Exception) : UploadResult()
    }

    companion object {
        const val KEY_IMAGE_URIS = "image_uris"
        const val KEY_FAILED_PATHS = "failed_image_paths"
    }
}

// Notification constants
private val VERBOSE_NOTIFICATION_CHANNEL_NAME: CharSequence = "Brew Background Sync"
private var VERBOSE_NOTIFICATION_CHANNEL_DESCRIPTION = "Shows notifications whenever syncing starts"
private val NOTIFICATION_TITLE: CharSequence = "Brew is syncing your drinks"
private val CHANNEL_ID = "BACKGROUND_SYNC_NOTIFICATION"
private val NOTIFICATION_ID = 1