package io.github.jamiesanson.brew.di.module

import android.arch.persistence.room.Room
import android.content.Context
import dagger.Module
import dagger.Provides
import io.github.jamiesanson.brew.data.local.BrewDatabase
import io.github.jamiesanson.brew.data.local.dao.DrinkDao
import io.github.jamiesanson.brew.di.scope.ApplicationScope

@Module
class DatabaseModule {

    @Provides
    @ApplicationScope
    fun provideDatabase(context: Context): BrewDatabase {
        return Room
                .databaseBuilder(context, BrewDatabase::class.java, DB_NAME)
                .build()
    }

    @Provides
    @ApplicationScope
    fun provideDrinkDao(database: BrewDatabase): DrinkDao = database.drinkDao()

    companion object {
        const val DB_NAME = "brew-database"
    }
}