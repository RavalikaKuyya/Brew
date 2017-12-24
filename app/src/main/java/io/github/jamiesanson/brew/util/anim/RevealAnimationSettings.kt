package io.github.jamiesanson.brew.util.anim

import android.annotation.SuppressLint
import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@SuppressLint("ParcelCreator")
@Parcelize
data class RevealAnimationSettings(
        val centerX: Int,
        val centerY: Int,
        val width: Int,
        val height: Int,
        val startColor: Int
): Parcelable