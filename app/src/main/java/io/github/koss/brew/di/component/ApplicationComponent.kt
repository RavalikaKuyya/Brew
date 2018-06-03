package io.github.koss.brew.di.component

import dagger.Component
import io.github.koss.brew.BrewActivity
import io.github.koss.brew.BrewApp
import io.github.koss.brew.data.remote.image.imgur.ImgurImageService
import io.github.koss.brew.di.module.*
import io.github.koss.brew.di.scope.ApplicationScope
import io.github.koss.brew.repository.drinks.DrinkRepository
import io.github.koss.brew.ui.camera.CameraActivity
import io.github.koss.brew.ui.create.drink.AddDrinkFragment
import io.github.koss.brew.ui.create.drink.simple.SimpleAddDrinkBottomSheetDialogFragment
import io.github.koss.brew.ui.drink.DrinkActivity
import io.github.koss.brew.ui.home.HomeFragment
import io.github.koss.brew.ui.main.MainActivityComponent
import io.github.koss.brew.ui.main.MainActivityModule
import io.github.koss.brew.ui.main.fragment.MainFragment
import io.github.koss.brew.ui.syncsettings.SyncSettingsDialogFragment
import io.github.koss.brew.ui.you.YouFragment
import io.github.koss.brew.ui.you.loggedout.LoggedOutFragment
import io.github.koss.brew.ui.you.profile.ProfileFragment
import io.github.koss.brew.ui.you.settings.SettingsFragment

@ApplicationScope
@Component(modules = [
    ApplicationModule::class,
    RepositoryModule::class,
    NavigationModule::class,
    NetworkModule::class
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

    fun inject(fragment: SyncSettingsDialogFragment)

    fun plus(module: MainActivityModule): MainActivityComponent

    fun imageService(): ImgurImageService

    fun drinkRepository(): DrinkRepository
}