package io.github.koss.brew.data.local.dao

import android.arch.lifecycle.LiveData
import android.arch.persistence.room.*
import android.net.Uri
import io.github.koss.brew.data.model.Drink
import io.reactivex.Maybe

@Dao
interface DrinkDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertDrinks(drinks: Array<Drink>)

    @Insert
    fun insertDrink(drink: Drink)

    @Query("SELECT * FROM drink")
    fun loadAllDrinks(): LiveData<List<Drink>>

    @Query("SELECT * FROM drink")
    fun getAllDrinks(): List<Drink>

    @Delete
    fun deleteDrink(drink: Drink)

    @Delete
    fun deleteAllDrinks(list: List<Drink>)

    @Query("SELECT * FROM drink WHERE id = :drinkId")
    fun getDrinkById(drinkId: Int): Maybe<Drink>

    @Query("UPDATE drink SET photoDeleteHash = :imageDeleteHash, photoId = :imageId, photoLink = :imageLink WHERE photoUri = :drinkImageUri")
    fun updateDrinkUploadStatus(drinkImageUri: Uri, imageId: String, imageDeleteHash: String, imageLink: String)
}