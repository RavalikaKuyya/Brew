package io.github.jamiesanson.brew.data.local

import android.arch.persistence.room.Database
import android.arch.persistence.room.RoomDatabase
import io.github.jamiesanson.brew.data.local.dao.DrinkDao
import io.github.jamiesanson.brew.model.Drink

@Database(
        entities = [
            Drink::class
        ],
        version = 1)
abstract class BrewDatabase: RoomDatabase() {

    abstract fun drinkDao(): DrinkDao
}