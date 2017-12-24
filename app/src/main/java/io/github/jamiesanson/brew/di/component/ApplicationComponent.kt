package io.github.jamiesanson.brew.di.component

import dagger.Component
import io.github.jamiesanson.brew.BrewActivity
import io.github.jamiesanson.brew.BrewApp
import io.github.jamiesanson.brew.di.module.ApplicationModule
import io.github.jamiesanson.brew.di.module.NavigationModule
import io.github.jamiesanson.brew.di.module.RepositoryModule
import io.github.jamiesanson.brew.di.scope.ApplicationScope
import io.github.jamiesanson.brew.ui.create.drink.CreateDrinkFragment
import io.github.jamiesanson.brew.ui.home.HomeFragment
import io.github.jamiesanson.brew.ui.main.MainActivityComponent
import io.github.jamiesanson.brew.ui.main.MainActivityModule
import io.github.jamiesanson.brew.ui.main.fragment.MainFragment

@ApplicationScope
@Component(modules = [
    ApplicationModule::class,
    RepositoryModule::class,
    NavigationModule::class
])
interface ApplicationComponent {

    fun inject(app: BrewApp)

    fun inject(activity: BrewActivity)

    fun inject(fragment: HomeFragment)

    fun inject(fragment: MainFragment)

    fun inject(fragment: CreateDrinkFragment)

    fun plus(module: MainActivityModule): MainActivityComponent
}