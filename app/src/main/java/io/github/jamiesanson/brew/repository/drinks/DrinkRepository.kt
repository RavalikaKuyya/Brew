package io.github.jamiesanson.brew.repository.drinks

import android.arch.lifecycle.LiveData
import io.github.jamiesanson.brew.data.local.dao.DrinkDao
import io.github.jamiesanson.brew.data.model.Drink
import io.github.jamiesanson.brew.data.model.Rating
import kotlinx.coroutines.experimental.launch
import java.util.*

class DrinkRepository(
        private val drinkDao: DrinkDao
) {

    fun getDrinks(): LiveData<List<Drink>> = drinkDao.loadAllDrinks()

    fun addNewDrink(drink: Drink) {
        launch {
            drinkDao.insertDrink(drink)
        }
    }

    fun removeDrink(drink: Drink) {
        launch {
            drinkDao.deleteDrink(drink)
        }
    }
}