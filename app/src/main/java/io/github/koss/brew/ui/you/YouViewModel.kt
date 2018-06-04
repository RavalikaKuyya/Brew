package io.github.koss.brew.ui.you

import android.arch.lifecycle.ViewModel
import android.support.annotation.WorkerThread
import io.github.koss.brew.data.local.dao.DrinkDao
import io.github.koss.brew.repository.drinks.DrinkRepository
import io.github.koss.brew.util.SingleLiveEvent
import io.github.koss.brew.util.arch.EventBus
import io.reactivex.Single
import io.reactivex.rxkotlin.ofType
import javax.inject.Inject

class YouViewModel @Inject constructor(
        private val eventBus: EventBus,
        private val drinkRepository: DrinkRepository,
        private val drinkDao: DrinkDao
): ViewModel() {

    val reloadTrigger = SingleLiveEvent<Nothing>()

    init {
        eventBus.observable()
                .ofType<OnRefreshYouFragmentRequested>()
                .subscribe { reloadTrigger.call() }
    }

    fun syncAllDrinks() {
        drinkRepository.syncLocalDrinks()
    }

    fun hasLocalDrinks(): Single<Boolean> = drinkDao.getAllDrinksRx().map { it.isNotEmpty() }
}