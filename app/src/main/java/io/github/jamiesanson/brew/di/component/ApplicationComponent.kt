package io.github.jamiesanson.brew.di.component

import dagger.Component
import io.github.jamiesanson.brew.BrewActivity
import io.github.jamiesanson.brew.BrewApp
import io.github.jamiesanson.brew.di.module.ApplicationModule
import io.github.jamiesanson.brew.di.module.RepositoryModule
import io.github.jamiesanson.brew.di.scope.ApplicationScope
import io.github.jamiesanson.brew.ui.home.HomeFragment
import io.github.jamiesanson.brew.ui.main.MainActivityComponent
import io.github.jamiesanson.brew.ui.main.MainActivityModule

@ApplicationScope
@Component(modules = [
    ApplicationModule::class,
    RepositoryModule::class
])
interface ApplicationComponent {

    fun inject(app: BrewApp)

    fun inject(activity: BrewActivity)

    fun inject(fragment: HomeFragment)

    fun plus(module: MainActivityModule): MainActivityComponent
}