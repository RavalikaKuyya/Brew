package io.github.koss.brew.ui.drink

import android.arch.lifecycle.ViewModel
import android.net.Uri
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import io.github.koss.brew.data.model.Drink
import io.github.koss.brew.data.remote.worker.util.KEY_DESCRIPTION
import io.github.koss.brew.data.remote.worker.util.KEY_IMAGE_LINK
import io.github.koss.brew.data.remote.worker.util.KEY_NAME
import io.github.koss.brew.data.remote.worker.util.KEY_TAGS
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

    var drink = Drink()

    fun initialise(drinkId: String, isReference: Boolean) {
        if (isReference) {
            val uid = FirebaseAuth.getInstance().uid
            FirebaseFirestore.getInstance()
                    .document("users/$uid/drinks/$drinkId")
                    .get()
                    .addOnSuccessListener {
                        @Suppress("UNCHECKED_CAST")
                        drink = drink.copy(
                                name = it[KEY_NAME] as String,
                                description = it[KEY_DESCRIPTION] as String,
                                tags = it[KEY_TAGS] as List<String>
                        )
                        state.send(DrinkEvent.DrinkLoaded(drink))
                    }
                    .addOnFailureListener {
                        state.send(DrinkEvent.DrinkNonExistent)
                    }

            FirebaseFirestore.getInstance()
                    .collection("users/$uid/drinks/$drinkId/images")
                    .get()
                    .addOnSuccessListener {
                        it.documents.firstOrNull()?.let {
                            drink = drink.copy(photoUri = Uri.parse(it[KEY_IMAGE_LINK] as String))
                            state.send(DrinkEvent.DrinkLoaded(drink))
                        }
                    }
        } else {
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