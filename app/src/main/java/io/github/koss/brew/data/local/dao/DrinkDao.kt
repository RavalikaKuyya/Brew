package io.github.koss.brew.data.local.dao

import android.arch.lifecycle.LiveData
import android.arch.paging.DataSource
import android.arch.persistence.room.*
import io.github.koss.brew.data.model.Drink
import io.reactivex.Maybe
import io.reactivex.Single

@Dao
interface DrinkDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertDrinks(drinks: Array<Drink>)

    @Insert
    fun insertDrink(drink: Drink): Long

    @Query("SELECT * FROM drink")
    fun loadAllDrinks(): LiveData<List<Drink>>

    @Query("SELECT * FROM drink")
    fun getAllDrinks(): List<Drink>

    @Query("SELECT * FROM drink")
    fun getAllDrinksRx(): Single<List<Drink>>

    @Delete
    fun deleteDrink(drink: Drink)

    @Delete
    fun deleteAllDrinks(list: List<Drink>)

    @Query("SELECT * FROM drink WHERE id = :drinkId")
    fun getDrinkById(drinkId: Int): Maybe<Drink>

    @Query("UPDATE drink SET remoteId = :firebaseId WHERE id = :drinkId")
    fun updateDrinkUploadStatus(drinkId: Int, firebaseId: String)

    @Query("SELECT * FROM drink ORDER BY timestamp DESC")
    fun getPagedDrinks(): DataSource.Factory<Int, Drink>
}