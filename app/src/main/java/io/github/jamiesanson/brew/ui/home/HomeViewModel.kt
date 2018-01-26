package io.github.jamiesanson.brew.ui.home

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.Transformations
import android.arch.lifecycle.ViewModel
import io.github.jamiesanson.brew.data.model.Drink
import io.github.jamiesanson.brew.repository.drinks.DrinkRepository
import javax.inject.Inject

class HomeViewModel @Inject constructor(
        drinksRepository: DrinkRepository
): ViewModel() {

    val drinkList: LiveData<List<Drink>> = drinksRepository.getDrinks()

    // Take the last RECENT_COUNT recent drinks and reverse for display
    val recentDrinks: LiveData<List<Drink>> = Transformations.map(drinkList) {
        it.takeLast(RECENT_COUNT).reversed()
    }

    companion object {
        const val RECENT_COUNT = 5
    }
}