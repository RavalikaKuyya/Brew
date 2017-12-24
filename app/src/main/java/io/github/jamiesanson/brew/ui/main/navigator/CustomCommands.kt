package io.github.jamiesanson.brew.ui.main.navigator

import ru.terrakok.cicerone.commands.Command

/**
 * Class encapsulating custom commands for Cicero
 */
sealed class CustomCommand: Command

class BackFromDrinkScreen: CustomCommand()
