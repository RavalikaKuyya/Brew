package io.github.jamiesanson.brew.ui.home

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import io.github.jamiesanson.brew.util.event.RebuildHomescreen
import io.github.jamiesanson.brew.util.event.UiEventBus
import javax.inject.Inject

class HomeViewModel @Inject constructor(
        uiEventBus: UiEventBus
): ViewModel() {

    val rebuildTrigger: LiveData<Unit> = MutableLiveData()

    init {
        uiEventBus.events.observeForever {
            if (it is RebuildHomescreen) {
                (rebuildTrigger as MutableLiveData).postValue(null)
            }
        }
    }
}