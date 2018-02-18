package io.github.koss.brew.ui.main

import dagger.Subcomponent
import io.github.koss.brew.di.scope.ActivityScope

@ActivityScope
@Subcomponent(
        modules = [
            MainActivityModule::class
        ]
)
interface MainActivityComponent {

    fun inject(activity: MainActivity)
}