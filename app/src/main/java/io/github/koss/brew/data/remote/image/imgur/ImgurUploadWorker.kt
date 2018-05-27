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
import io.github.koss.brew.data.remote.image.imgur.model.response.UploadImageResponseData
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

    private var notificationBuilder: NotificationCompat.Builder? = null

    override fun doWork(): WorkerResult {
        val context = applicationContext
        val imageService = context.initialiseImageService()
        val imageStrings = inputData.getStringArray(KEY_IMAGE_URIS)
        val imageUris = imageStrings.map { Uri.parse(it) }
        maxProgress = imageUris.size

        context.createNotificationChannel()
        context.updateNotification()

        val failedUploads = imageUris.mapNotNull {
            return@mapNotNull try {
                val result = imageService.uploadImage(it).blockingGet()
                uploadCount += 1
                context.updateNotification()
                context.persistDrinkResult(it, result)
                null
            } catch (e: Exception) {
                uploadCount += 1
                context.updateNotification()
                it
            }
        }

        context.cancelNotification()

        val returnData = failedUploads.map(Uri::getPath)

        outputData = Data.Builder().apply { putStringArray(KEY_FAILED_PATHS, returnData.toTypedArray()) }.build()

        // Always succeed. The results will be checked from outputData later
        return WorkerResult.SUCCESS
    }

    private fun Context.createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = VERBOSE_NOTIFICATION_CHANNEL_NAME
            val description = VERBOSE_NOTIFICATION_CHANNEL_DESCRIPTION
            val importance = NotificationManager.IMPORTANCE_MIN
            val channel = NotificationChannel(CHANNEL_ID, name, importance)
            channel.description = description

            val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun Context.cancelNotification() {
        NotificationManagerCompat.from(this).cancel(NOTIFICATION_ID)
    }

    private fun Context.updateNotification() {
        // Create the notification
        val builder = getNotificationBuilder()
                .setContentText("Uploading ${uploadCount + 1} of $maxProgress images")
                .setProgress(maxProgress, uploadCount, false)

        // Show the notification
        NotificationManagerCompat.from(this).notify(NOTIFICATION_ID, builder.build())
    }

    private fun Context.initialiseImageService(): ImgurImageService {
        // Create Imgur API instance
        return getApplicationComponent(this).imageService()
    }

    private fun Context.persistDrinkResult(drinkImageUri: Uri, result: UploadImageResponseData) {
        val repo = getApplicationComponent(this).drinkRepository()
        repo.updateDrinkUploadStatus(drinkImageUri,
                imageId = result.id,
                imageDeleteHash = result.deleteHash,
                imageLink = result.link)
    }

    private fun Context.getNotificationBuilder(): NotificationCompat.Builder {
        if (notificationBuilder == null) {
            notificationBuilder = NotificationCompat.Builder(this, CHANNEL_ID)
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setOngoing(true)
                    .setContentTitle(NOTIFICATION_TITLE)
                    .setPriority(NotificationCompat.PRIORITY_LOW)
                    .setVibrate(LongArray(0))
        }

        return notificationBuilder!!
    }

    private fun getApplicationComponent(context: Context): ApplicationComponent {
        if (applicationComponent == null) {
            applicationComponent =  DaggerApplicationComponent.builder()
                    .applicationModule(ApplicationModule(context as Application))
                    .build()
        }

        return applicationComponent!!
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