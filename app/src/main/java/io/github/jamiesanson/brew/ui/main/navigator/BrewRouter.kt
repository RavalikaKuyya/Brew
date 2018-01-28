package io.github.jamiesanson.brew.ui.main.navigator

import ru.terrakok.cicerone.Router
import ru.terrakok.cicerone.commands.Command

class BrewRouter: Router() {

    fun backFromDrinkScreen() {
        executeCommand(BackFromAddDrinkScreen())
    }

    public override fun executeCommand(command: Command?) {
        super.executeCommand(command)
    }
}