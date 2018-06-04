package io.github.koss.brew.ui.home.content.recent

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.Transformations
import android.arch.lifecycle.ViewModel
import io.github.koss.brew.data.model.Drink
import io.github.koss.brew.repository.config.PreferencesManager
import io.github.koss.brew.repository.drinks.DrinkRepository
import io.github.koss.brew.util.event.RebuildHomescreen
import io.github.koss.brew.util.event.UiEvent
import io.github.koss.brew.util.event.UiEventBus
import javax.inject.Inject

class RecentDrinksViewModel @Inject constructor(
        private val drinksRepository: DrinkRepository,
        private val eventBus: UiEventBus,
        private val preferencesManager: PreferencesManager
): ViewModel() {

    val recentDrinks: LiveData<List<Drink>> = Transformations.map(drinksRepository.getDrinks()) {
        it.reversed()
    }

    init {
        recentDrinks.observeForever { eventBus.postEvent(RebuildHomescreen()) }
    }

    fun removeDrink(drink: Drink) {
        drinksRepository.removeDrink(drink)
    }

    fun shouldShowEmptyState(): Boolean {
        return !preferencesManager.hasAddedFirstDrink
    }

    fun postEvent(event: UiEvent) {
        eventBus.postEvent(event)
    }

    companion object {
        const val RECENT_COUNT = 5
    }
}