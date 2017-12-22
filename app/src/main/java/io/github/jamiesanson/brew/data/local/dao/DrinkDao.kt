package io.github.jamiesanson.brew.data.local.dao

import android.arch.lifecycle.LiveData
import android.arch.persistence.room.Dao
import android.arch.persistence.room.Insert
import android.arch.persistence.room.OnConflictStrategy
import android.arch.persistence.room.Query
import io.github.jamiesanson.brew.data.model.Drink

@Dao
interface DrinkDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertDrinks(drinks: Array<Drink>)

    @Insert
    fun insertDrink(drink: Drink)

    @Query("SELECT * FROM drink")
    fun loadAllDrinks(): LiveData<List<Drink>>
}