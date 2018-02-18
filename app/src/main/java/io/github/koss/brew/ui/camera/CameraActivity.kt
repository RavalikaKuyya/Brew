package io.github.koss.brew.ui.camera

import android.Manifest
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.support.annotation.LayoutRes
import android.support.constraint.ConstraintSet
import android.support.v4.content.ContextCompat
import android.support.v4.view.animation.FastOutSlowInInterpolator
import android.support.v7.app.AppCompatActivity
import android.transition.AutoTransition
import android.transition.TransitionManager
import android.util.Log
import android.view.View
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import io.fotoapparat.Fotoapparat
import io.fotoapparat.configuration.CameraConfiguration
import io.fotoapparat.parameter.ScaleType
import io.fotoapparat.result.WhenDoneListener
import io.fotoapparat.selector.*
import io.github.koss.brew.R
import io.github.koss.brew.util.PermissionDelegate
import io.github.koss.brew.util.arch.BrewViewModelFactory
import io.github.koss.brew.util.extension.component
import kotlinx.android.synthetic.main.activity_camera.*
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.async
import kotlinx.coroutines.experimental.delay
import kotlinx.coroutines.experimental.launch
import org.jetbrains.anko.sdk25.coroutines.onClick
import java.io.File
import java.io.IOException
import javax.inject.Inject

/**
 * Activity for taking a single photo. Returns a URI
 * of the photo taken with RESULT_PHOTO_URI as the key
 */
class CameraActivity: AppCompatActivity() {

    private lateinit var fotoapparat: Fotoapparat
    private lateinit var viewModel: CameraViewModel
    @Inject lateinit var viewModelFactory: BrewViewModelFactory

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_camera)
        component.inject(this)
        viewModel = ViewModelProviders
                .of(this, viewModelFactory)
                .get(CameraViewModel::class.java)

        window.statusBarColor = ContextCompat.getColor(this, R.color.material_black)

        // Check permissions
        launch(UI) {
            val permissionAccepted = PermissionDelegate()
                    .checkPermissions(
                            activity = this@CameraActivity,
                            justification = "Some permissions are needed to use the camera",
                            permission = Manifest.permission.CAMERA,
                            permissions = *arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE))

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

        viewModel.state.observe(this, Observer {
            when (it) {
                is CameraViewModel.State.PreviewShowing -> {
                    imagePreviewView.visibility = View.GONE
                }
                is CameraViewModel.State.PhotoTaken -> {
                    onPhotoTaken(it.photoUri)
                }
                is CameraViewModel.State.PhotoAccepted -> {
                    setResult(RESULT_OK, Intent().putExtra(RESULT_PHOTO_URI, it.photoUri))
                    finish()
                }
                is CameraViewModel.State.PhotoDeclined -> {
                    try {
                        File(it.declinedUri.path).delete()
                    } catch (e: IOException) {
                        Log.e("CameraActivity", "Failed to delete previous image", e)
                    }
                    animateButtonChange(true)
                    imagePreviewView.setImageBitmap(null)
                    takePictureButton.isEnabled = true
                }
                null -> {}
            }
        })
    }

    override fun onPostCreate(savedInstanceState: Bundle?) {
        super.onPostCreate(savedInstanceState)
        takePictureButton.onClick {
            takePicture()
        }

        acceptButton.onClick {
            viewModel.photoAccepted()
        }

        declineButton.onClick {
            viewModel.photoDeclined()
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

    private fun onPhotoTaken(uri: Uri) {
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
                    .setDuration(150L)
                    .start()
        }

        takePictureButton.isEnabled = false

        photoResult
                .saveToFile(file)
                .whenDone( object: WhenDoneListener<Unit> {
                    override fun whenDone(it: Unit?) {
                        viewModel.photoTaken(Uri.fromFile(file))
                        animateButtonChange()
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
                        takePictureButton.visibility = View.INVISIBLE
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
                            acceptButton.visibility = View.INVISIBLE
                            declineButton.visibility = View.INVISIBLE
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
                jpegQuality = highestQuality(),
                sensorSensitivity = lowestSensorSensitivity()
        )
    }

    companion object {
        const val RESULT_PHOTO_URI = "result_photo_uri"
        const val RESULT_PERMISSION_DENIED = 112
    }
}