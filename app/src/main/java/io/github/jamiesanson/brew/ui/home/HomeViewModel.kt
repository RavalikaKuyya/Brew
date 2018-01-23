package io.github.jamiesanson.brew.ui.home

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.ViewModel
import io.github.jamiesanson.brew.data.model.Drink
import io.github.jamiesanson.brew.repository.drinks.DrinkRepository
import javax.inject.Inject

class HomeViewModel @Inject constructor(
        drinksRepository: DrinkRepository
): ViewModel() {

    val drinkList: LiveData<List<Drink>> = drinksRepository.getDrinks()
}