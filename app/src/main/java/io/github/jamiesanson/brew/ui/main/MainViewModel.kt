package io.github.jamiesanson.brew.ui.main

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.Transformations
import android.arch.lifecycle.ViewModel
import io.github.jamiesanson.brew.repository.drinks.DrinkRepository
import javax.inject.Inject

class MainViewModel @Inject constructor(
        private val drinkRepository: DrinkRepository
): ViewModel() {

    val drinkCount: LiveData<Int> = Transformations.map(
            drinkRepository.getDrinks(),
            { it.size })

    fun addDrink() {
        drinkRepository.insertRandomDrink()
    }
}