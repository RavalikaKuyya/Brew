package io.github.koss.brew.repository.drinks

import android.arch.lifecycle.LiveData
import android.net.Uri
import androidx.work.Data
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import io.github.koss.brew.data.local.dao.DrinkDao
import io.github.koss.brew.data.model.Drink
import io.github.koss.brew.data.remote.DrinkService
import io.github.koss.brew.data.remote.image.imgur.ImgurUploadWorker
import io.github.koss.brew.data.remote.image.imgur.ImgurUploadWorker.Companion.KEY_IMAGE_URIS
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
            // TODO - Move this to drink service perhaps?
            // TODO - Check which drinks haven't been uploaded

            val drinks = drinkDao.getAllDrinks()

            val inputData = drinks
                    .filter { it.photoId == null }
                    .mapNotNull { drink -> drink.photoUri?.path }
                    .toTypedArray()

            val work = OneTimeWorkRequestBuilder<ImgurUploadWorker>()
                    .setInputData(Data.Builder().apply { putStringArray(KEY_IMAGE_URIS, inputData) }.build())
                    .build()

            // Launch image upload job
            WorkManager.getInstance().enqueue(work)
        }
    }

    fun updateDrinkUploadStatus(drinkUri: Uri, imageId: String, imageDeleteHash: String, imageLink: String) {
        launch {
            drinkDao.updateDrinkUploadStatus(drinkUri, imageId, imageDeleteHash, imageLink)
        }
    }
}