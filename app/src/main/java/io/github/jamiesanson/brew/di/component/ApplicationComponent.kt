package io.github.jamiesanson.brew.di.component

import dagger.Component
import io.github.jamiesanson.brew.BrewApp
import io.github.jamiesanson.brew.di.module.ApplicationModule
import io.github.jamiesanson.brew.di.scope.ApplicationScope
import io.github.jamiesanson.brew.ui.home.HomeFragment

@ApplicationScope
@Component(modules = [
    ApplicationModule::class
])
interface ApplicationComponent {

    fun inject(app: BrewApp)

    fun inject(fragment: HomeFragment)
}