package io.github.jamiesanson.brew.ui.home.content.recent

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.Transformations
import android.arch.lifecycle.ViewModel
import io.github.jamiesanson.brew.data.model.Drink
import io.github.jamiesanson.brew.repository.drinks.DrinkRepository
import io.github.jamiesanson.brew.util.event.RebuildHomescreen
import io.github.jamiesanson.brew.util.event.UiEvent
import io.github.jamiesanson.brew.util.event.UiEventBus
import javax.inject.Inject

class RecentDrinksViewModel @Inject constructor(
        private val drinksRepository: DrinkRepository,
        private val eventBus: UiEventBus
): ViewModel() {

    // Take the last RECENT_COUNT recent drinks and reverse for display
    val recentDrinks: LiveData<List<Drink>> = Transformations.map(drinksRepository.getDrinks()) {
        it.takeLast(RECENT_COUNT).reversed()
    }

    init {
        recentDrinks.observeForever { eventBus.postEvent(RebuildHomescreen()) }
    }

    fun removeDrink(drink: Drink) {
        drinksRepository.removeDrink(drink)
    }

    fun postEvent(event: UiEvent) {
        eventBus.postEvent(event)
    }

    companion object {
        const val RECENT_COUNT = 5
    }
}