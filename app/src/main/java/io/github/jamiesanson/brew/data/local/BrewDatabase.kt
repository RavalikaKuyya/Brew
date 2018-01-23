package io.github.jamiesanson.brew.data.local

import android.arch.persistence.room.Database
import android.arch.persistence.room.RoomDatabase
import android.arch.persistence.room.TypeConverters
import io.github.jamiesanson.brew.data.local.dao.DrinkDao
import io.github.jamiesanson.brew.data.model.Drink
import io.github.jamiesanson.brew.util.arch.RoomTypeConverters

@Database(
        entities = [
            Drink::class
        ],
        version = 1)
@TypeConverters(value = [RoomTypeConverters::class])
abstract class BrewDatabase: RoomDatabase() {

    abstract fun drinkDao(): DrinkDao
}