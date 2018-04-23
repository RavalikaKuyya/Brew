package io.github.koss.brew.ui.home

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import io.github.koss.brew.ui.home.content.HomeContent
import io.github.koss.brew.ui.home.content.recent.RecentDrinksContent
import io.github.koss.brew.util.event.RebuildHomescreen
import io.github.koss.brew.util.event.UiEventBus
import io.github.koss.brew.util.event.ViewAllClicked
import javax.inject.Inject

class HomeViewModel @Inject constructor(
        uiEventBus: UiEventBus,
        val content: HomeContent
): ViewModel() {

    /**
     * Rebuild trigger for homescreen. Boolean describes if the rebuild should be forced, i.e
     * the RecyclerView is reinitialised with a new controller.
     */
    val rebuildTrigger: LiveData<Boolean> = MutableLiveData()

    init {
        uiEventBus.events.observeForever {
            when (it) {
                is RebuildHomescreen -> (rebuildTrigger as MutableLiveData).postValue(false)
                is ViewAllClicked -> viewAllClicked()
                else -> {}
            }
        }
    }

    private fun viewAllClicked() {
        // Map the event to show the recent drinks in another way
        content.contentList = content.contentList.map {
            if (it is RecentDrinksContent) {
                return@map RecentDrinksContent(!it.asCarousel)
            }

            return@map it
        }

        // Force rebuild
        (rebuildTrigger as MutableLiveData).postValue(true)
    }
}