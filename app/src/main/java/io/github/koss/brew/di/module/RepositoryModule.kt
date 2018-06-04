package io.github.koss.brew.di.module

import android.content.Context
import dagger.Module
import dagger.Provides
import io.github.koss.brew.data.local.dao.DrinkDao
import io.github.koss.brew.data.remote.DrinkService
import io.github.koss.brew.di.scope.ApplicationScope
import io.github.koss.brew.repository.activity.ActivityRepository
import io.github.koss.brew.repository.config.PreferencesManager
import io.github.koss.brew.repository.drinks.DrinkRepository

@Module(
        includes = [
            DatabaseModule::class
        ]
)
class RepositoryModule {

    @Provides
    @ApplicationScope
    fun provideDrinkRepository(drinkDao: DrinkDao, drinkService: DrinkService, preferencesManager: PreferencesManager): DrinkRepository =
            DrinkRepository(drinkDao, drinkService, preferencesManager)

    @Provides
    @ApplicationScope
    fun providePreferencesManager(context: Context): PreferencesManager =
            PreferencesManager(context)

    @Provides
    @ApplicationScope
    fun provideActivityRepository(drinkDao: DrinkDao): ActivityRepository =
            ActivityRepository(drinkDao)
}