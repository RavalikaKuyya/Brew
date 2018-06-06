package io.github.koss.brew.ui.create.drink

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import android.net.Uri
import com.google.firebase.Timestamp
import io.github.koss.brew.data.model.Drink
import io.github.koss.brew.repository.drinks.DrinkRepository
import javax.inject.Inject

class AddDrinkViewModel @Inject constructor(
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

    val modelNotEmpty: Boolean
        get() = photo != null || title.isNotEmpty() || tags.isNotEmpty()

    private val actions: LiveData<DrinkAction> = MutableLiveData()
    val state: LiveData<DrinkState> = MutableLiveData()

    init {
        (state as MutableLiveData).value = Empty()
        actions.observeForever(this::onAction)
    }

    private fun onAction(action: DrinkAction?) {
        model = when (action) {
            is DrinkSubmitted -> {
                if (modelNotEmpty) {
                    drinkRepository.addNewDrink(model.copy(timestamp = Timestamp.now()))
                }
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

    fun postAction(action: DrinkAction) {
        (actions as MutableLiveData).postValue(action)
    }

    override fun onCleared() {
        super.onCleared()
        clear()
    }

    fun clear() {
        model = Drink()
        isViewRevealed = false
    }
}