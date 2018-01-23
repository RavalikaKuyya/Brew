package io.github.jamiesanson.brew.util.anim

import android.annotation.SuppressLint
import android.os.Parcelable
import android.support.annotation.ColorInt
import android.view.View
import android.view.Window
import kotlinx.android.parcel.Parcelize
import kotlinx.android.parcel.RawValue

@SuppressLint("ParcelCreator")
@Parcelize
data class RevealAnimationSettings(
        val centerX: Int,
        val centerY: Int,
        val width: Int,
        val height: Int,
        @ColorInt val startColor: Int,
        @ColorInt val endColor: Int = 0,
        val duration: Long = 500,
        var targetView: @RawValue View? = null,
        var statusBarAnimationSettings: @RawValue StatusBarAnimationSettings? = null
): Parcelable

data class StatusBarAnimationSettings(
        @ColorInt val startColor: Int,
        @ColorInt val endColor: Int,
        val window: Window?
)