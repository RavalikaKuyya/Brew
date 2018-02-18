package io.github.jamiesanson.brew.ui.main

import android.arch.lifecycle.ViewModel
import io.github.jamiesanson.brew.ui.main.navigator.Add
import io.github.jamiesanson.brew.ui.main.navigator.BrewRouter
import io.github.jamiesanson.brew.ui.main.navigator.Remove
import io.github.jamiesanson.brew.ui.main.navigator.Screens
import io.github.jamiesanson.brew.util.event.*
import javax.inject.Inject

class MainViewModel @Inject constructor(
        uiEventBus: UiEventBus
): ViewModel() {
    private var router: BrewRouter? = null
    private var mainRootSet: Boolean = false

    init {
        uiEventBus.events.observeForever(this::onUiEvent)
    }

    fun init(router: BrewRouter) {
        this.router = router

        if (!mainRootSet) {
            router.newRootScreen(Screens.MAIN_SCREEN)
            mainRootSet = true
        }
    }

    private fun onUiEvent(event: UiEvent?) {
        when (event) {
            is MoveToAddDrinkScreen -> router?.executeCommand(Add(
                    Screens.ADD_DRINK_SCREEN,
                    event.settings))
            is ExitAddDrinkScreen -> router?.executeCommand(Remove(Screens.ADD_DRINK_SCREEN))
            is MoveToDrinkScreen -> router?.executeCommand(event.command)
            is ExitDrinkScreen -> router?.executeCommand(Remove(Screens.DRINK_SCREEN))
            null -> {}
        }
    }
}