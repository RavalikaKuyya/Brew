package io.github.koss.brew.repository.drinks

import android.arch.lifecycle.LiveData
import io.github.koss.brew.data.local.dao.DrinkDao
import io.github.koss.brew.data.model.Drink
import io.github.koss.brew.data.remote.DrinkService
import io.github.koss.brew.repository.config.ConfigurationWrapper
import io.github.koss.brew.util.Session
import io.reactivex.Maybe
import kotlinx.coroutines.experimental.launch

class DrinkRepository(
        private val drinkDao: DrinkDao,
        private val drinkService: DrinkService
) {

    fun getDrinks(): LiveData<List<Drink>> = drinkDao.loadAllDrinks()

    fun addNewDrink(drink: Drink) {
        launch {
            val id = drinkDao.insertDrink(drink)

            if (Session.isLoggedIn) {
                val shouldSync = ConfigurationWrapper.blockingFetch().shouldSyncImmediately

                if (shouldSync) {
                    // Get the new drink from the DB such that it has a valid ID
                    drinkService.enqueueDrink(drinkDao.getDrinkById(id.toInt()).blockingGet())
                }
            }
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