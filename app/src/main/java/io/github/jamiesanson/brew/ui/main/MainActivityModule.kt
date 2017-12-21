package io.github.jamiesanson.brew.ui.main

import android.arch.lifecycle.ViewModelProviders
import dagger.Module
import dagger.Provides
import io.github.jamiesanson.brew.R
import io.github.jamiesanson.brew.di.scope.ActivityScope
import io.github.jamiesanson.brew.util.arch.BrewViewModelFactory
import ru.terrakok.cicerone.Navigator

@Module
class MainActivityModule(private val activity: MainActivity) {

    @Provides
    @ActivityScope
    fun provideActivity(): MainActivity = activity

    @Provides
    @ActivityScope
    fun provideViewModel(activity: MainActivity, viewModelFactory: BrewViewModelFactory): MainViewModel {
        return ViewModelProviders
                .of(activity, viewModelFactory)
                .get(MainViewModel::class.java)
    }

    @Provides
    @ActivityScope
    fun provideNavigator(activity: MainActivity): Navigator =
            BrewNavigator(activity.supportFragmentManager, R.id.container)
}