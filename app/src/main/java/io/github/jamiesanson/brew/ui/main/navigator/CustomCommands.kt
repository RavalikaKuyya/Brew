package io.github.jamiesanson.brew.ui.main.navigator

import android.widget.ImageView
import io.github.jamiesanson.brew.data.model.Drink
import io.github.jamiesanson.brew.ui.drink.DrinkRevealSettings
import ru.terrakok.cicerone.commands.Command
import ru.terrakok.cicerone.commands.Forward

/**
 * Class encapsulating custom commands for Cicero
 */
sealed class CustomCommand: Command

open class Add(val screenkey: String, val transitionData: Any? = null): CustomCommand()

open class Remove(val screenkey: String, val transitionData: Any? = null): CustomCommand()

class ForwardToDrinkScreen(
        val imageView: ImageView,
        drinkRevealSettings: DrinkRevealSettings): Add(Screens.DRINK_SCREEN, drinkRevealSettings)
