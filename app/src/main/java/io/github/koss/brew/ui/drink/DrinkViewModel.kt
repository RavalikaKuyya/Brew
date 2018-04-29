package io.github.koss.brew.ui.drink

import android.arch.lifecycle.ViewModel
import io.github.koss.brew.repository.drinks.DrinkRepository
import io.github.koss.brew.util.arch.ReducibleLiveData
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

class DrinkViewModel @Inject constructor(
        private val drinkRepository: DrinkRepository
): ViewModel() {

    val state = ReducibleLiveData(
            initialState = DrinkState.Loading,
            reducer = ::reduce)

    fun initialise(drinkId: String) {
        drinkRepository.getDrinkById(drinkId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeBy(
                    onSuccess = {
                        state.send(DrinkEvent.DrinkLoaded(it))
                    },
                    onComplete = {
                        state.send(DrinkEvent.DrinkNonExistent)
                    }
                )
    }

    private fun reduce(currentState: DrinkState, event: DrinkEvent): DrinkState {
        return when (currentState) {
            DrinkState.Loading -> {
                when (event) {
                    is DrinkEvent.DrinkLoaded -> DrinkState.DrinkRetrieved(
                            drink = event.drink,
                            relatedDrinks = null
                    )
                    DrinkEvent.DrinkNonExistent -> DrinkState.UnrecoverableError(
                            title = "Drink not found",
                            message = "These aren't the droids you're looking for."
                    )
                    DrinkEvent.DrinkRetrievalFailed -> DrinkState.UnrecoverableError(
                            title = "Oh dear",
                            message = "Something bad's happened. Try again soon."
                    )
                    is DrinkEvent.RelatedDrinksLoaded -> DrinkState.DrinkRetrieved(
                            drink = null,
                            relatedDrinks = event.relatedDrinks
                    )
                    DrinkEvent.NoRelatedDrinks -> DrinkState.DrinkRetrieved(
                            drink = null,
                            relatedDrinks = emptyList()
                    )
                }
            }
            is DrinkState.DrinkRetrieved -> {
                when (event) {
                    is DrinkEvent.DrinkLoaded -> currentState.copy(
                            drink = event.drink
                    )
                    is DrinkEvent.RelatedDrinksLoaded -> currentState.copy(
                            relatedDrinks = event.relatedDrinks
                    )
                    is DrinkEvent.NoRelatedDrinks -> currentState.copy(
                            relatedDrinks = emptyList()
                    )
                    else -> currentState
                }
            }
            is DrinkState.UnrecoverableError -> currentState
        }
    }
}