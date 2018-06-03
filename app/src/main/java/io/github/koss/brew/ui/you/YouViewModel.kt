package io.github.koss.brew.ui.you

import android.arch.lifecycle.ViewModel
import io.github.koss.brew.util.SingleLiveEvent
import io.github.koss.brew.util.arch.EventBus
import io.reactivex.rxkotlin.ofType
import javax.inject.Inject

class YouViewModel @Inject constructor(
        private val eventBus: EventBus
): ViewModel() {

    val reloadTrigger = SingleLiveEvent<Nothing>()

    init {
        eventBus.observable()
                .ofType<OnRefreshYouFragmentRequested>()
                .subscribe { reloadTrigger.call() }
    }
}