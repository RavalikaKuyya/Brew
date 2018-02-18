package io.github.koss.brew.ui.create.drink.photo

import android.support.annotation.DrawableRes
import android.support.annotation.StringRes
import io.github.koss.brew.R

/**
 * Class encapsulating photo sources
 */
sealed class PhotoSource(@StringRes val titleRes: Int, @DrawableRes val iconRes: Int = 0)

class Camera: PhotoSource(R.string.camera, R.drawable.ic_camera_black_24dp)

class Gallery: PhotoSource(R.string.gallery, R.drawable.ic_collections_black_24dp)