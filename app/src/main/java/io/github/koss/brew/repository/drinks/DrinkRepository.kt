package io.github.koss.brew.repository.drinks

import android.arch.lifecycle.LiveData
import io.github.koss.brew.data.local.dao.DrinkDao
import io.github.koss.brew.data.model.Drink
import io.github.koss.brew.data.remote.DrinkService
import io.reactivex.Maybe
import kotlinx.coroutines.experimental.launch

class DrinkRepository(
        private val drinkDao: DrinkDao,
        private val drinkService: DrinkService
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

    fun getDrinkById(drinkId: String): Maybe<Drink> {
        return drinkDao.getDrinkById(drinkId.toInt())
    }

    fun clearLocalDrinks() {
        launch {
            drinkDao.deleteAllDrinks(drinkDao.getAllDrinks())
        }
    }

    fun syncLocalDrinks() {
        launch {
            drinkDao.getAllDrinks()
                    .filter { it.remoteId == null }
                    .forEach(drinkService::enqueueDrinkUpload)
        }
    }

    fun onDrinkUploaded(drinkId: Int, firebaseDrinkId: String) {
        launch {
            drinkDao.updateDrinkUploadStatus(drinkId, firebaseDrinkId)
        }
    }
}