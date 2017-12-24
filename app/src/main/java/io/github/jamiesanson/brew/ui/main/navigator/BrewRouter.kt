package io.github.jamiesanson.brew.ui.main.navigator

import ru.terrakok.cicerone.Router

class BrewRouter: Router() {

    fun backFromDrinkScreen() {
        executeCommand(BackFromDrinkScreen())
    }
}