package io.github.koss.brew.ui.create.drink.simple

import android.arch.lifecycle.ViewModel
import android.net.Uri
import com.google.firebase.Timestamp
import io.github.koss.brew.data.model.Drink
import io.github.koss.brew.repository.drinks.DrinkRepository
import javax.inject.Inject

class SimpleAddDrinkViewModel @Inject constructor(
        private val drinkRepository: DrinkRepository
): ViewModel() {

    private val tags: MutableList<String> = mutableListOf()

    private var photoUri: Uri? = null

    fun addDrink(title: String, description: String) {
        drinkRepository.addNewDrink(Drink(
                name = title,
                description = description,
                tags = tags,
                photoUri = photoUri,
                timestamp = Timestamp.now()
        ))
    }

    fun tagAdded(tag: String) {
        tags.add(tag)
    }

    fun tagRemoved(tag: String) {
        tags.remove(tag)
    }

    fun onPhotoTaken(photoUri: Uri) {
        this.photoUri = photoUri
    }
}