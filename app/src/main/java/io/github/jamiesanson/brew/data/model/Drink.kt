package io.github.jamiesanson.brew.data.model

import android.arch.persistence.room.Embedded
import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey

@Entity
data class Drink(
        @PrimaryKey(autoGenerate = true)
        var id: Int = 0,

        var name: String,

        @Embedded
        var rating: Rating
)