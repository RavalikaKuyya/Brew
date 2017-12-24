package io.github.jamiesanson.brew.di.module

import dagger.Module
import dagger.Provides
import io.github.jamiesanson.brew.di.scope.ApplicationScope
import io.github.jamiesanson.brew.ui.main.navigator.BrewRouter
import ru.terrakok.cicerone.Cicerone
import ru.terrakok.cicerone.NavigatorHolder
import ru.terrakok.cicerone.Router

@Module
class NavigationModule {

    @Provides
    @ApplicationScope
    fun provideCicerone(): Cicerone<BrewRouter> = Cicerone.create(BrewRouter())

    @Provides
    @ApplicationScope
    fun provideRouter(cicerone: Cicerone<BrewRouter>): BrewRouter = cicerone.router

    @Provides
    @ApplicationScope
    fun provideNavigatorHolder(cicerone: Cicerone<BrewRouter>): NavigatorHolder = cicerone.navigatorHolder
}