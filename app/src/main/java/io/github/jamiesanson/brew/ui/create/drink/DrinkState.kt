package io.github.jamiesanson.brew.ui.create.drink

import io.github.jamiesanson.brew.data.model.Drink

/**
 * Class encapsulating states of Drink screen
 */
sealed class DrinkState(val drink: Drink)

class Empty: DrinkState(Drink())

class PhotoPresent(drink: Drink): DrinkState(drink)


