package io.github.jamiesanson.brew.util.event

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData

/**
 * Injectable event bus to UI events
 */
class UiEventBus {
    val events: LiveData<UiEvent> = MutableLiveData()

    fun postEvent(event: UiEvent) {
        (events as MutableLiveData).postValue(event)
    }
}