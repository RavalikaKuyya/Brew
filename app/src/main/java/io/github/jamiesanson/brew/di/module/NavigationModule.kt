package io.github.jamiesanson.brew.di.module

import dagger.Module
import dagger.Provides
import io.github.jamiesanson.brew.di.scope.ActivityScope
import ru.terrakok.cicerone.Cicerone
import ru.terrakok.cicerone.NavigatorHolder
import ru.terrakok.cicerone.Router

@Module
class NavigationModule {

    @Provides
    @ActivityScope
    fun provideCicerone(): Cicerone<Router> = Cicerone.create()

    @Provides
    @ActivityScope
    fun provideRouter(cicerone: Cicerone<Router>): Router = cicerone.router

    @Provides
    @ActivityScope
    fun provideNavigatorHolder(cicerone: Cicerone<Router>): NavigatorHolder = cicerone.navigatorHolder
}