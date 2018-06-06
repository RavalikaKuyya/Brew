package io.github.koss.brew.ui.drink

import io.github.koss.brew.data.model.Drink

/**
 * State definition for the View
 */
sealed class DrinkState {
    // The content on the page is loading
    object Loading: DrinkState()

    // State for the drink being retrieved
    data class DrinkRetrieved(
            val drink: Drink?,
            val relatedDrinks: List<Drink>?
    ): DrinkState()

    // State for unrecoverable Drink retrieval error
    data class UnrecoverableError(
            val title: String,
            val message: String
    ): DrinkState()
}

sealed class DrinkEvent {

    data class DrinkLoaded(val drink: Drink): DrinkEvent()

    object DrinkNonExistent: DrinkEvent()

    object DrinkRetrievalFailed: DrinkEvent()

    data class RelatedDrinksLoaded(val relatedDrinks: List<Drink>): DrinkEvent()

    object NoRelatedDrinks: DrinkEvent()

}