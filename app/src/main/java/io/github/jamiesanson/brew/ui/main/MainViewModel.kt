package io.github.jamiesanson.brew.ui.main

import android.arch.lifecycle.ViewModel
import io.github.jamiesanson.brew.ui.main.navigator.BrewRouter
import io.github.jamiesanson.brew.ui.main.navigator.Screens
import io.github.jamiesanson.brew.util.event.ExitDrinkScreen
import io.github.jamiesanson.brew.util.event.MoveToDrinkScreen
import io.github.jamiesanson.brew.util.event.UiEvent
import io.github.jamiesanson.brew.util.event.UiEventBus
import ru.terrakok.cicerone.Router
import javax.inject.Inject

class MainViewModel @Inject constructor(
        uiEventBus: UiEventBus
): ViewModel() {
    private var router: BrewRouter? = null

    init {
        uiEventBus.events.observeForever(this::onUiEvent)
    }

    fun init(router: BrewRouter) {
        this.router = router
        router.newRootScreen(Screens.MAIN_SCREEN)
    }

    private fun onUiEvent(event: UiEvent?) {
        when (event) {
            is MoveToDrinkScreen -> router?.navigateTo(
                    Screens.ADD_DRINK_SCREEN,
                    event.settings)
            is ExitDrinkScreen -> router?.backFromDrinkScreen()
            null -> {}
        }
    }
}