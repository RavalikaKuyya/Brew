package io.github.jamiesanson.brew.ui.create.drink

import android.net.Uri


/**
 * Class encapsulating actions that can be made on the Drink screen
 */
sealed class DrinkAction

class DrinkSubmitted: DrinkAction()

data class PhotoChosen(val photo: Uri): DrinkAction()

data class TagAdded(val newTag: String): DrinkAction()

data class TagRemoved(val toRemove: String): DrinkAction()

data class TitleChanged(val title: String): DrinkAction()
