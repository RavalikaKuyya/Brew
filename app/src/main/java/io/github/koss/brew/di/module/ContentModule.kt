package io.github.koss.brew.di.module

import dagger.Module
import dagger.Provides
import io.github.koss.brew.di.scope.ApplicationScope
import io.github.koss.brew.ui.home.content.HomeContent
import io.github.koss.brew.ui.you.settings.content.SettingsContent
import io.github.koss.brew.util.arch.BrewViewModelFactory

@Module
class ContentModule {

    @Provides
    @ApplicationScope
    fun provideHomeContent(viewModelFactory: BrewViewModelFactory): HomeContent =
            HomeContent(viewModelFactory)

    @Provides
    @ApplicationScope
    fun provideSettingsContent(viewModelFactory: BrewViewModelFactory): SettingsContent =
            SettingsContent(viewModelFactory)
}