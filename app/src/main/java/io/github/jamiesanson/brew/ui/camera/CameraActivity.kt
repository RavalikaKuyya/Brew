package io.github.jamiesanson.brew.ui.camera

import android.Manifest
import android.app.Activity
import android.os.Bundle
import android.os.Environment
import android.support.v4.content.ContextCompat
import android.view.View
import com.bumptech.glide.Glide
import io.fotoapparat.Fotoapparat
import io.fotoapparat.parameter.ScaleType
import io.fotoapparat.result.WhenDoneListener
import io.github.jamiesanson.brew.R
import io.github.jamiesanson.brew.util.PermissionDelegate
import kotlinx.android.synthetic.main.activity_camera.*
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.async
import org.jetbrains.anko.sdk25.coroutines.onClick
import java.io.File
import java.net.URI

/**
 * Activity for taking a single photo. Returns a URI
 * of the photo taken with RESULT_PHOTO_URI as the key
 */
class CameraActivity: Activity() {

    private lateinit var fotoapparat: Fotoapparat

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
                        scaleType = ScaleType.CenterCrop
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
                    .into(imagePreviewView)
        }

    }

    private fun takePicture() {
        val photoResult = fotoapparat.takePicture()
        val file = getIncrementedFile()

        photoResult
                .saveToFile(file)
                .whenDone( object: WhenDoneListener<Unit> {
                    override fun whenDone(it: Unit?) {
                        onPhotoTaken(file.toURI())
                    }
                })
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

    companion object {
        const val RESULT_PHOTO_URI = "result_photo_uri"
        const val RESULT_PERMISSION_DENIED = 112
    }
}