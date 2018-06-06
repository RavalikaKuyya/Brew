package io.github.koss.brew.repository.activity

import android.arch.lifecycle.LiveData
import android.arch.paging.LivePagedListBuilder
import android.arch.paging.PagedList
import io.github.koss.brew.data.local.dao.DrinkDao
import io.github.koss.brew.data.model.DrinkAdded
import io.github.koss.brew.data.model.ProfileActivity

class ActivityRepository(
        private val drinkDao: DrinkDao
) {

    fun getLocalActivity(): LiveData<PagedList<ProfileActivity>> {
        return LivePagedListBuilder(
                drinkDao.getPagedDrinks().map {
                    DrinkAdded(
                            drinkId = it.id.toString(),
                            drinkName = it.name,
                            drinkDescription = it.description ?: "",
                            imageLink = it.photoUri?.path ?: ""
                    ) as ProfileActivity
                },
                20
        ).build()
    }
}