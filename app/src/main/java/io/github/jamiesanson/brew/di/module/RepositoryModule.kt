package io.github.jamiesanson.brew.di.module

import android.content.Context
import dagger.Module
import dagger.Provides
import io.github.jamiesanson.brew.data.local.dao.DrinkDao
import io.github.jamiesanson.brew.di.scope.ApplicationScope
import io.github.jamiesanson.brew.repository.config.ConfigurationRepository
import io.github.jamiesanson.brew.repository.config.PreferencesManager
import io.github.jamiesanson.brew.repository.drinks.DrinkRepository

@Module(
        includes = [
            DatabaseModule::class
        ]
)
class RepositoryModule {

    @Provides
    @ApplicationScope
    fun provideConfigurationRepository(preferencesManager: PreferencesManager): ConfigurationRepository =
            ConfigurationRepository(preferencesManager)

    @Provides
    @ApplicationScope
    fun provideDrinkRepository(drinkDao: DrinkDao): DrinkRepository =
            DrinkRepository(drinkDao)

    @Provides
    @ApplicationScope
    fun providePreferencesManager(context: Context): PreferencesManager =
            PreferencesManager(context)
}