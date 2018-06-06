package io.github.koss.brew.util.arch

import android.arch.lifecycle.LiveData

/**
 * LiveData subclass for handling reduction of state
 */
class ReducibleLiveData<State, Event> constructor(
        private val initialState: State,
        private val reducer: (currentState: State, event: Event) -> State
): LiveData<State>() {

    init {
        value = initialState
    }

    fun send(event: Event) {
        val currentState = value ?: initialState
        val newState = reducer(currentState, event)

        if (currentState != newState) {
            value = newState
        }
    }
}