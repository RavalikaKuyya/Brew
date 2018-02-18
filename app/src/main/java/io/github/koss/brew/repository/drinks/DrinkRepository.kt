package io.github.koss.brew.repository.drinks

import android.arch.lifecycle.LiveData
import io.github.koss.brew.data.local.dao.DrinkDao
import io.github.koss.brew.data.model.Drink
import kotlinx.coroutines.experimental.launch

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