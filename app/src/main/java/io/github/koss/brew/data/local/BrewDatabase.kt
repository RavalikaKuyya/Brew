package io.github.koss.brew.data.local

import android.arch.persistence.room.Database
import android.arch.persistence.room.RoomDatabase
import android.arch.persistence.room.TypeConverters
import io.github.koss.brew.data.local.dao.DrinkDao
import io.github.koss.brew.data.model.Drink
import io.github.koss.brew.util.arch.RoomTypeConverters

@Database(
        entities = [
            Drink::class
        ],
        version = 1)
@TypeConverters(value = [RoomTypeConverters::class])
abstract class BrewDatabase: RoomDatabase() {

    abstract fun drinkDao(): DrinkDao
}