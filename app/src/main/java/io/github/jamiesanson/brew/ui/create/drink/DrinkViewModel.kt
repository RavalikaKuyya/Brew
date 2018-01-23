package io.github.jamiesanson.brew.ui.create.drink

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import android.net.Uri
import io.github.jamiesanson.brew.data.model.Drink
import io.github.jamiesanson.brew.repository.drinks.DrinkRepository
import javax.inject.Inject

class DrinkViewModel @Inject constructor(
        private val drinkRepository: DrinkRepository
): ViewModel() {

    var isViewRevealed = false

    private var model = Drink()

    val hasPhoto: Boolean
        get() = model.photoUri != null

    val photo: Uri?
        get() = model.photoUri

    val title: String
        get() = model.name

    val tags: List<String>
        get() = model.tags

    private val actions: LiveData<DrinkAction> = MutableLiveData()
    val state: LiveData<DrinkState> = MutableLiveData()

    init {
        (state as MutableLiveData).value = Empty()
        actions.observeForever(this::onAction)
    }

    private fun onAction(action: DrinkAction?) {
        model = when (action) {
            is DrinkSubmitted -> {
                drinkRepository.addNewDrink(model)
                model
            }
            is PhotoChosen -> {
                model.copy(photoUri = action.photo).apply {
                    (state as MutableLiveData).postValue(PhotoPresent(this))
                }
            }
            is TagAdded -> model.copy(tags = ArrayList(model.tags).apply { add(action.newTag) })
            is TagRemoved -> model.copy(tags = ArrayList(model.tags).apply { remove(action.toRemove) })
            is TitleChanged -> model.copy(name = action.title)
            null -> model
        }
    }

    private fun onStateChange(state: DrinkState) {

    }

    fun postAction(action: DrinkAction) {
        (actions as MutableLiveData).postValue(action)
    }
}