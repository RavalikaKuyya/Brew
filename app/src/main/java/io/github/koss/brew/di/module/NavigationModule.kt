package io.github.koss.brew.di.module

import dagger.Module
import dagger.Provides
import io.github.koss.brew.di.scope.ApplicationScope
import io.github.koss.brew.ui.main.navigator.BrewRouter
import ru.terrakok.cicerone.Cicerone
import ru.terrakok.cicerone.NavigatorHolder

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