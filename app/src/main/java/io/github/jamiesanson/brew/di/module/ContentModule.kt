package io.github.jamiesanson.brew.di.module

import dagger.Module
import dagger.Provides
import io.github.jamiesanson.brew.di.scope.ApplicationScope
import io.github.jamiesanson.brew.ui.home.content.HomeContent
import io.github.jamiesanson.brew.util.arch.BrewViewModelFactory

@Module
class ContentModule {

    @Provides
    @ApplicationScope
    fun provideHomeContent(viewModelFactory: BrewViewModelFactory): HomeContent =
            HomeContent(viewModelFactory)
}