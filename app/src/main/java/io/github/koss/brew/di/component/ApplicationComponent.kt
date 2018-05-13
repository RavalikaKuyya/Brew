package io.github.koss.brew.di.component

import dagger.Component
import io.github.koss.brew.BrewActivity
import io.github.koss.brew.BrewApp
import io.github.koss.brew.di.module.ApplicationModule
import io.github.koss.brew.di.module.NavigationModule
import io.github.koss.brew.di.module.RepositoryModule
import io.github.koss.brew.di.scope.ApplicationScope
import io.github.koss.brew.ui.camera.CameraActivity
import io.github.koss.brew.ui.create.drink.AddDrinkFragment
import io.github.koss.brew.ui.create.drink.simple.SimpleAddDrinkBottomSheetDialogFragment
import io.github.koss.brew.ui.drink.DrinkActivity
import io.github.koss.brew.ui.home.HomeFragment
import io.github.koss.brew.ui.main.MainActivityComponent
import io.github.koss.brew.ui.main.MainActivityModule
import io.github.koss.brew.ui.main.fragment.MainFragment
import io.github.koss.brew.ui.you.YouFragment
import io.github.koss.brew.ui.you.loggedout.LoggedOutFragment
import io.github.koss.brew.ui.you.profile.ProfileFragment
import io.github.koss.brew.ui.you.settings.SettingsFragment

@ApplicationScope
@Component(modules = [
    ApplicationModule::class,
    RepositoryModule::class,
    NavigationModule::class
])
interface ApplicationComponent {

    fun inject(app: BrewApp)

    fun inject(activity: BrewActivity)

    fun inject(cameraActivity: CameraActivity)

    fun inject(drinkActivity: DrinkActivity)

    fun inject(fragment: HomeFragment)

    fun inject(fragment: MainFragment)

    fun inject(fragment: AddDrinkFragment)

    fun inject(fragment: ProfileFragment)

    fun inject(fragment: LoggedOutFragment)

    fun inject(fragment: SettingsFragment)

    fun inject(fragment: YouFragment)

    fun inject(fragment: SimpleAddDrinkBottomSheetDialogFragment)

    fun plus(module: MainActivityModule): MainActivityComponent
}