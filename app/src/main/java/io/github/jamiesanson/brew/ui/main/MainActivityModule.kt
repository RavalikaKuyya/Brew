package io.github.jamiesanson.brew.ui.main

import dagger.Module
import dagger.Provides
import io.github.jamiesanson.brew.R
import io.github.jamiesanson.brew.di.module.NavigationModule
import io.github.jamiesanson.brew.di.scope.ActivityScope
import io.github.jamiesanson.brew.ui.main.navigator.BrewNavigator
import ru.terrakok.cicerone.Navigator

@Module
class MainActivityModule(private val activity: MainActivity) {

    @Provides
    @ActivityScope
    fun provideActivity(): MainActivity = activity

    @Provides
    @ActivityScope
    fun provideNavigator(activity: MainActivity): Navigator =
            BrewNavigator(activity.supportFragmentManager, R.id.container)
}