package io.github.jamiesanson.brew.ui.camera

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.os.Environment
import android.support.annotation.LayoutRes
import android.support.constraint.ConstraintSet
import android.support.v4.content.ContextCompat
import android.support.v4.view.animation.FastOutSlowInInterpolator
import android.transition.AutoTransition
import android.transition.TransitionManager
import android.view.View
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import io.fotoapparat.Fotoapparat
import io.fotoapparat.configuration.CameraConfiguration
import io.fotoapparat.configuration.Configuration
import io.fotoapparat.parameter.ScaleType
import io.fotoapparat.result.Photo
import io.fotoapparat.result.WhenDoneListener
import io.fotoapparat.selector.*
import io.github.jamiesanson.brew.R
import io.github.jamiesanson.brew.util.PermissionDelegate
import kotlinx.android.synthetic.main.activity_camera.*
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.async
import kotlinx.coroutines.experimental.delay
import kotlinx.coroutines.experimental.launch
import org.jetbrains.anko.sdk25.coroutines.onClick
import java.io.File
import java.net.URI

/**
 * Activity for taking a single photo. Returns a URI
 * of the photo taken with RESULT_PHOTO_URI as the key
 */
class CameraActivity: Activity() {

    private lateinit var fotoapparat: Fotoapparat

    private var fileUri: URI? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_camera)
        window.statusBarColor = ContextCompat.getColor(this, R.color.material_black)

        async(UI) {
            val permissionAccepted = PermissionDelegate()
                    .checkPermissions(
                            activity = this@CameraActivity,
                            justification = "Some permissions are needed to use the camera",
                            permission = Manifest.permission.CAMERA)

            if (permissionAccepted) {
                fotoapparat = Fotoapparat(
                        context = this@CameraActivity,
                        view = cameraView,
                        scaleType = ScaleType.CenterCrop,
                        cameraConfiguration = getConfiguration()
                )

                fotoapparat.start()
            } else {
                setResult(RESULT_PERMISSION_DENIED)
                finish()
            }
        }
    }

    override fun onPostCreate(savedInstanceState: Bundle?) {
        super.onPostCreate(savedInstanceState)
        takePictureButton.onClick {
            takePicture()
        }

        acceptButton.onClick {
            setResult(RESULT_OK, Intent().putExtra(RESULT_PHOTO_URI, fileUri))
            finish()
        }

        declineButton.onClick {
            imagePreviewView.visibility = View.GONE
            animateButtonChange(true)
            fileUri = null
        }
    }

    override fun onStart() {
        super.onStart()
        if (::fotoapparat.isInitialized) {
            fotoapparat.start()
        }
    }

    override fun onStop() {
        super.onStop()
        if (::fotoapparat.isInitialized) {
            fotoapparat.stop()
        }
    }

    private fun onPhotoTaken(uri: URI) {
        async(UI) {
            imagePreviewView.visibility = View.VISIBLE
            Glide.with(this@CameraActivity)
                    .load(uri.path)
                    .listener(object: RequestListener<Drawable> {
                        override fun onResourceReady(resource: Drawable?, model: Any?, target: Target<Drawable>?, dataSource: DataSource?, isFirstResource: Boolean): Boolean {
                            // Fade out flash layout
                            with (flashLayout) {
                                animate()
                                        .alpha(0f)
                                        .setDuration(300L)
                                        .setInterpolator(FastOutSlowInInterpolator())
                                        .withEndAction { flashLayout.visibility = View.GONE }
                                        .start()
                            }
                            return false
                        }

                        override fun onLoadFailed(e: GlideException?, model: Any?, target: Target<Drawable>?, isFirstResource: Boolean): Boolean {
                            return false
                        }
                    })
                    .into(imagePreviewView)
        }
    }

    private fun takePicture() {
        val photoResult = fotoapparat.takePicture()
        val file = getIncrementedFile()

        // Show flash layout
        with (flashLayout) {
            alpha = 0f
            visibility = View.VISIBLE
            animate()
                    .alpha(1f)
                    .setDuration(30L)
                    .start()
        }

        animateButtonChange()

        photoResult
                .saveToFile(file)
                .whenDone( object: WhenDoneListener<Unit> {
                    override fun whenDone(it: Unit?) {
                        onPhotoTaken(file.toURI())
                        fileUri = file.toURI()
                    }
                })
    }

    private fun animateButtonChange(photoDeclined: Boolean = false) {
        val animationDuration = if (photoDeclined) 200 else 500

        fun switchConstraints(@LayoutRes layoutRes: Int) {
            val transition = AutoTransition()
            transition.duration = animationDuration.toLong()

            TransitionManager.beginDelayedTransition(constraintLayout, transition)

            val constraintSet = ConstraintSet()
            constraintSet.clone(this, layoutRes)

            constraintSet.applyTo(constraintLayout)
        }

        if (!photoDeclined) {
            acceptButton.visibility = View.VISIBLE
            declineButton.visibility = View.VISIBLE
            takePictureButton
                    .animate()
                    .alpha(0f)
                    .setDuration(200L)
                    .withEndAction {
                        takePictureButton.visibility = View.GONE
                        switchConstraints(R.layout.activity_camera_pic_taken)
                    }.start()
        } else {
            switchConstraints(R.layout.activity_camera)
            launch(UI) {
                delay(animationDuration)
                takePictureButton
                        .animate()
                        .alpha(1f)
                        .setDuration(200L)
                        .withStartAction {
                            takePictureButton.visibility = View.VISIBLE
                            takePictureButton.alpha = 0f
                        }
                        .withEndAction {
                            acceptButton.visibility = View.GONE
                            declineButton.visibility = View.GONE
                        }
                        .start()
            }
        }
    }

    private fun getIncrementedFile(): File {
        // Get public external storage dir
        val dir = File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES), "Brew")

        if (!dir.exists()) {
            dir.mkdirs()
        }

        val count = dir
                .listFiles { file -> !file.isDirectory && file.name.startsWith("brew_photo") }
                .count()

        return File(dir, "brew_photo_$count.jpg")
    }

    private fun getConfiguration(): CameraConfiguration {
        return CameraConfiguration(
                pictureResolution = highestResolution(),
                previewResolution = highestResolution(),
                previewFpsRange = highestFps(),
                focusMode = continuousFocusPicture(),
                flashMode = off(),
                antiBandingMode = auto(),
                jpegQuality = manualJpegQuality(70),
                sensorSensitivity = lowestSensorSensitivity()
        )
    }

    companion object {
        const val RESULT_PHOTO_URI = "result_photo_uri"
        const val RESULT_PERMISSION_DENIED = 112
    }
}