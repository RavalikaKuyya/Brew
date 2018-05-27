package io.github.koss.brew.ui.you.settings.content.appsettings

import android.arch.lifecycle.ViewModel
import io.github.koss.brew.repository.drinks.DrinkRepository
import javax.inject.Inject

class AppSettingsViewModel @Inject constructor(
        private val drinkRepository: DrinkRepository
): ViewModel() {

    fun onAllLocalDrinksCleared() {
        drinkRepository.clearLocalDrinks()
    }

    fun onSyncRequested() {
        drinkRepository.syncLocalDrinks()
    }
}