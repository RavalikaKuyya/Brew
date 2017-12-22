package io.github.jamiesanson.brew.ui.main

import dagger.Subcomponent
import io.github.jamiesanson.brew.di.module.NavigationModule
import io.github.jamiesanson.brew.di.scope.ActivityScope

@ActivityScope
@Subcomponent(
        modules = [
            MainActivityModule::class
        ]
)
interface MainActivityComponent {

    fun inject(activity: MainActivity)
}