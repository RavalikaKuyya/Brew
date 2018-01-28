package io.github.jamiesanson.brew.data.model

import android.annotation.SuppressLint
import android.arch.persistence.room.Embedded
import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey
import android.net.Uri
import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import kotlinx.android.parcel.RawValue

@SuppressLint("ParcelCreator")
@Entity
@Parcelize
data class Drink(
        @PrimaryKey(autoGenerate = true)
        var id: Int = 0,

        var name: String = "",

        var photoUri: Uri? = null,

        var tags: List<String> = emptyList(),

        @Embedded
        var rating: @RawValue Rating? = null
) : Parcelable