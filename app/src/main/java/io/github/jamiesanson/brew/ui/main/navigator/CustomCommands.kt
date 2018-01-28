package io.github.jamiesanson.brew.ui.main.navigator

import android.widget.ImageView
import io.github.jamiesanson.brew.data.model.Drink
import ru.terrakok.cicerone.commands.Command
import ru.terrakok.cicerone.commands.Forward

/**
 * Class encapsulating custom commands for Cicero
 */
sealed class CustomCommand: Command

class BackFromAddDrinkScreen : CustomCommand()

class ForwardToDrinkScreen(
        val imageView: ImageView,
        val drink: Drink): Forward(Screens.DRINK_SCREEN, drink)
