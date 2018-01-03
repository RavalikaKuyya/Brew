package io.github.jamiesanson.brew.util

import android.content.Context
import android.graphics.drawable.Drawable
import android.net.Uri
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.Priority
import com.bumptech.glide.request.RequestOptions
import com.zhihu.matisse.engine.ImageEngine

/**
 * [ImageEngine] implementation using Glide.
 */
class GlideImageEngine : ImageEngine {

    override fun loadThumbnail(context: Context, resize: Int, placeholder: Drawable, imageView: ImageView, uri: Uri) {
        Glide.with(context)
                .asBitmap()
                .load(uri)
                .apply(RequestOptions.placeholderOf(placeholder))
                .apply(RequestOptions.centerCropTransform())
                .apply(RequestOptions.overrideOf(resize))
                .into(imageView)
    }

    override fun loadAnimatedGifThumbnail(context: Context, resize: Int, placeholder: Drawable, imageView: ImageView,
                                          uri: Uri) {
        Glide.with(context)
                .asBitmap()
                .load(uri)
                .apply(RequestOptions.placeholderOf(placeholder))
                .apply(RequestOptions.centerCropTransform())
                .apply(RequestOptions.overrideOf(resize))
                .into(imageView)
    }

    override fun loadImage(context: Context, resizeX: Int, resizeY: Int, imageView: ImageView, uri: Uri) {
        Glide.with(context)
                .load(uri)
                .apply(RequestOptions.overrideOf(resizeX, resizeY))
                .apply(RequestOptions.priorityOf(Priority.HIGH))
                .into(imageView)
    }

    override fun loadAnimatedGifImage(context: Context, resizeX: Int, resizeY: Int, imageView: ImageView, uri: Uri) {
        Glide.with(context)
                .load(uri)
                .apply(RequestOptions.overrideOf(resizeX, resizeY))
                .apply(RequestOptions.priorityOf(Priority.HIGH))
                .into(imageView)
    }

    override fun supportAnimatedGif(): Boolean {
        return true
    }

}
