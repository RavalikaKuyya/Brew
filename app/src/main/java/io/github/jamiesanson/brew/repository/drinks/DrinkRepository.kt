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

    fun insertRandomDrink() {
        launch {
            drinkDao.insertDrink(Drink(
                    name = "test ${Random().nextInt()}",
                    rating = Rating(
                            score = 5.0,
                            review = "Was alright thanks"
                    )
            ))
        }
    }
}