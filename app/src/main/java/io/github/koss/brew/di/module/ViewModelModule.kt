package io.github.koss.brew.di.module

import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider
import dagger.Binds
import dagger.MapKey
import dagger.Module
import dagger.multibindings.IntoMap
import io.github.koss.brew.ui.camera.CameraViewModel
import io.github.koss.brew.ui.create.drink.AddDrinkViewModel
import io.github.koss.brew.ui.drink.DrinkViewModel
import io.github.koss.brew.ui.home.HomeViewModel
import io.github.koss.brew.ui.home.content.recent.RecentDrinksViewModel
import io.github.koss.brew.ui.main.MainViewModel
import io.github.koss.brew.ui.main.fragment.MainFragmentViewModel
import io.github.koss.brew.ui.you.profile.ProfileViewModel
import io.github.koss.brew.util.arch.BrewViewModelFactory
import kotlin.reflect.KClass

@Module
abstract class ViewModelModule {

    @Binds
    @IntoMap
    @ViewModelKey(HomeViewModel::class)
    abstract fun bindHomeViewModel(cameraViewModel: HomeViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(MainViewModel::class)
    abstract fun bindMainViewModel(mainViewModel: MainViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(MainFragmentViewModel::class)
    abstract fun bindMainFragmentViewModel(mainFragmentViewModel: MainFragmentViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(AddDrinkViewModel::class)
    abstract fun bindAddDrinkViewModel(drinkViewModel: AddDrinkViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(DrinkViewModel::class)
    abstract fun bindDrinkViewModel(drinkViewModel: DrinkViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(CameraViewModel::class)
    abstract fun bindCameraViewModel(cameraViewModel: CameraViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(RecentDrinksViewModel::class)
    abstract fun bindRecentDrinksViewModel(viewModel: RecentDrinksViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(ProfileViewModel::class)
    abstract fun bindProfileViewModel(viewModel: ProfileViewModel): ViewModel

    @Binds
    abstract fun bindViewModelFactory(factory: BrewViewModelFactory): ViewModelProvider.Factory
}

@Target(AnnotationTarget.FUNCTION, AnnotationTarget.PROPERTY_GETTER, AnnotationTarget.PROPERTY_SETTER)
@Retention(AnnotationRetention.RUNTIME)
@MapKey
internal annotation class ViewModelKey(val value: KClass<out ViewModel>)