package io.github.koss.brew.ui.drink

import android.annotation.SuppressLint
import android.os.Parcelable
import io.github.koss.brew.data.model.Drink
import kotlinx.android.parcel.Parcelize

@SuppressLint("ParcelCreator")
@Parcelize
data class DrinkRevealSettings(
        val drink: Drink,
        val width: Int,
        val height: Int,
        val x: Int,
        val y: Int
): Parcelable